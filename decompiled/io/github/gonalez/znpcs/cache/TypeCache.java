package io.github.gonalez.znpcs.cache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.UnmodifiableIterator;
import io.github.gonalez.znpcs.utility.Version;
import io.github.gonalez.znpcs.utility.VersionRange;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface TypeCache {
   abstract class BaseCache<T> {
      private static final Logger LOGGER = Logger.getLogger(TypeCache.BaseCache.class.getName());
      protected final TypeCache.CacheBuilder cacheBuilder;
      protected Class<?> BUILDER_CLASS;
      private T cached;
      private boolean loaded = false;

      protected BaseCache(TypeCache.CacheBuilder cacheBuilder) {
         this.cacheBuilder = cacheBuilder;

         for (String classes : cacheBuilder.className) {
            try {
               this.BUILDER_CLASS = Class.forName(classes);
               if (cacheBuilder.foundFirst) {
                  break;
               }
            } catch (Throwable var5) {
            }
         }
      }

      public T load() {
         if (this.loaded) {
            return this.cached;
         }

         try {
            if (this.BUILDER_CLASS == null) {
               throw new IllegalStateException("can't find class for: " + this.cacheBuilder.className);
            }

            T eval = this.cached != null ? this.cached : (this.cached = this.onLoad());
            if (eval == null) {
               throw new NullPointerException();
            }
         } catch (Throwable var2) {
         }

         this.loaded = true;
         return this.cached;
      }

      private void log(String message) {
         LOGGER.log(Level.WARNING, message);
      }

      protected abstract T onLoad() throws Exception;

      public static class ClazzLoader extends TypeCache.BaseCache<Class<?>> {
         public ClazzLoader(TypeCache.CacheBuilder cacheBuilder) {
            super(cacheBuilder);
         }

         protected Class<?> onLoad() {
            return this.BUILDER_CLASS;
         }
      }

      public static class ConstructorLoader extends TypeCache.BaseCache<Constructor<?>> {
         public ConstructorLoader(TypeCache.CacheBuilder cacheBuilder) {
            super(cacheBuilder);
         }

         protected Constructor<?> onLoad() throws NoSuchMethodException {
            Constructor<?> constructor = null;
            if (Iterables.size(this.cacheBuilder.parameterTypes) <= 1) {
               constructor = Iterables.size(this.cacheBuilder.parameterTypes) > 0
                  ? this.BUILDER_CLASS.getDeclaredConstructor((Class<?>[])Iterables.get(this.cacheBuilder.parameterTypes, 0))
                  : this.BUILDER_CLASS.getDeclaredConstructor();
            } else {
               UnmodifiableIterator var2 = this.cacheBuilder.parameterTypes.iterator();

               while (var2.hasNext()) {
                  Class<?>[] keyParameters = (Class<?>[])var2.next();

                  try {
                     return this.BUILDER_CLASS.getDeclaredConstructor(keyParameters);
                  } catch (NoSuchMethodException var5) {
                  }
               }
            }

            if (constructor != null) {
               constructor.setAccessible(true);
            }

            return constructor;
         }
      }

      public static class EnumLoader extends TypeCache.BaseCache<Enum<?>[]> {
         public EnumLoader(TypeCache.CacheBuilder cacheBuilder) {
            super(cacheBuilder);
         }

         protected Enum<?>[] onLoad() {
            Enum[] arrayOfEnum = (Enum[])this.BUILDER_CLASS.getEnumConstants();

            for (Enum<?> enumConstant : arrayOfEnum) {
               TypeCache.ClassCache.register(enumConstant.name(), enumConstant, this.BUILDER_CLASS);
            }

            return arrayOfEnum;
         }
      }

      public static class FieldLoader extends TypeCache.BaseCache<Field> {
         public FieldLoader(TypeCache.CacheBuilder cacheBuilder) {
            super(cacheBuilder);
         }

         protected Field onLoad() throws NoSuchFieldException {
            if (this.cacheBuilder.expectType != null) {
               for (Class<?> currentClass = this.BUILDER_CLASS; currentClass != null; currentClass = currentClass.getSuperclass()) {
                  for (Field field1 : currentClass.getDeclaredFields()) {
                     if (field1.getType() == this.cacheBuilder.expectType) {
                        field1.setAccessible(true);
                        return field1;
                     }
                  }
               }
            }

            Field field = null;
            if (this.BUILDER_CLASS != null) {
               field = this.BUILDER_CLASS.getDeclaredField(this.cacheBuilder.fieldName);
            }

            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) {
               try {
                  TypeCache.ClassCache.register(field.getName(), field.get(null), field.getDeclaringClass());
               } catch (IllegalAccessException e) {
                  throw new RuntimeException(e);
               }
            }

            return field;
         }

         public TypeCache.BaseCache.FieldLoader.AsValueField asValueField() {
            return new TypeCache.BaseCache.FieldLoader.AsValueField(this);
         }

         private static class AsValueField extends TypeCache.BaseCache<Object> {
            private final TypeCache.BaseCache.FieldLoader fieldLoader;

            public AsValueField(TypeCache.BaseCache.FieldLoader fieldLoader) {
               super(fieldLoader.cacheBuilder);
               this.fieldLoader = fieldLoader;
            }

            @Override
            protected Object onLoad() throws IllegalAccessException, NoSuchFieldException {
               Field field = this.fieldLoader.onLoad();
               return field.get(null);
            }
         }
      }

      public static class MethodLoader extends TypeCache.BaseCache<Method> {
         public MethodLoader(TypeCache.CacheBuilder builder) {
            super(builder);
         }

         protected Method onLoad() {
            Method maybeFindAtFirst = this.findMethodByReturnType(this.cacheBuilder.expectType);
            if (maybeFindAtFirst != null) {
               return maybeFindAtFirst;
            }

            if (this.cacheBuilder.searchByParameters) {
               Method method = Arrays.stream(this.BUILDER_CLASS.getDeclaredMethods())
                  .filter(
                     method1 -> Iterables.elementsEqual(
                        ImmutableList.copyOf(method1.getParameterTypes()), ImmutableList.copyOf((Class[])this.cacheBuilder.parameterTypes.get(0))
                     )
                  )
                  .findFirst()
                  .orElse(null);
               if (method != null) {
                  return method;
               }
            }

            Multimap<String, Boolean> methods = this.cacheBuilder.methods;

            for (Entry<String, Boolean> entry : methods.entries()) {
               if (entry.getValue()) {
                  String methodName = entry.getKey();
                  ImmutableList<Class<?>[]> parameterTypes = this.cacheBuilder.parameterTypes;

                  try {
                     Method[] methodsToSearch = this.cacheBuilder.foundFirst ? this.BUILDER_CLASS.getDeclaredMethods() : this.BUILDER_CLASS.getMethods();

                     for (Method method : methodsToSearch) {
                        if (this.isValidMethod(method)
                           && method.getName().equals(methodName)
                           && (this.cacheBuilder.expectType == null || method.getReturnType() == this.cacheBuilder.expectType)) {
                           method.setAccessible(true);
                           if (parameterTypes.isEmpty()) {
                              return method;
                           }

                           UnmodifiableIterator var12 = parameterTypes.iterator();

                           Class<?>[] params;
                           do {
                              if (!var12.hasNext()) {
                                 return method;
                              }

                              params = (Class<?>[])var12.next();
                           } while (Iterables.elementsEqual(ImmutableList.copyOf(method.getParameterTypes()), ImmutableList.copyOf(params)));
                        }
                     }

                     throw new NoSuchMethodException();
                  } catch (NoSuchMethodException var14) {
                  }
               }
            }

            return null;
         }

         private Method findMethodByReturnType(Class<?> expectedType) {
            if (!this.cacheBuilder.methods.isEmpty()) {
               return null;
            }

            if (expectedType == null) {
               return null;
            }

            for (Method method : this.BUILDER_CLASS.getDeclaredMethods()) {
               method.setAccessible(true);
               if (this.isValidMethod(method) && method.getReturnType() == expectedType) {
                  return method;
               }
            }

            return null;
         }

         private boolean isValidMethod(Method method) {
            if (!this.cacheBuilder.parameterTypes.isEmpty()) {
               UnmodifiableIterator var2 = this.cacheBuilder.parameterTypes.iterator();

               while (var2.hasNext()) {
                  Class<?>[] params = (Class<?>[])var2.next();
                  if (!Iterables.elementsEqual(ImmutableList.copyOf(method.getParameterTypes()), ImmutableList.copyOf(params))) {
                     return false;
                  }
               }
            }

            return !method.getName().equals("valueOf") && !method.getName().equalsIgnoreCase("getOriginWorld");
         }
      }
   }

   class CacheBuilder {
      private static final String EMPTY_STRING = "";
      private final CachePackage cachePackage;
      private final CacheCategory cacheCategory;
      private final String fieldName;
      private final List<String> className;
      private final Multimap<String, Boolean> methods;
      private final String additionalData;
      private final ImmutableList<Class<?>[]> parameterTypes;
      private final Class<?> expectType;
      private final boolean foundFirst;
      private final boolean searchByParameters;

      public CacheBuilder(CachePackage cachePackage) {
         this(cachePackage, CacheCategory.DEFAULT, new ArrayList<>(), "", LinkedHashMultimap.create(), "", ImmutableList.of(), null, false, false);
      }

      protected CacheBuilder(
         CachePackage cachePackage,
         CacheCategory cacheCategory,
         List<String> className,
         String fieldName,
         Multimap<String, Boolean> methods,
         String additionalData,
         ImmutableList<Class<?>[]> parameterTypes,
         Class<?> expectType,
         boolean foundFirst,
         boolean searchByParameters
      ) {
         this.cachePackage = cachePackage;
         this.cacheCategory = cacheCategory;
         this.className = className;
         this.methods = ImmutableMultimap.copyOf(methods);
         this.fieldName = fieldName;
         this.additionalData = additionalData;
         this.parameterTypes = parameterTypes;
         this.expectType = expectType;
         this.foundFirst = foundFirst;
         this.searchByParameters = searchByParameters;
      }

      public TypeCache.CacheBuilder acceptFirst() {
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            this.cacheCategory,
            this.className,
            this.fieldName,
            this.methods,
            this.additionalData,
            this.parameterTypes,
            this.expectType,
            true,
            this.searchByParameters
         );
      }

      public TypeCache.CacheBuilder searchByParameters() {
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            this.cacheCategory,
            this.className,
            this.fieldName,
            this.methods,
            this.additionalData,
            this.parameterTypes,
            this.expectType,
            this.foundFirst,
            true
         );
      }

      public TypeCache.CacheBuilder withCategory(CacheCategory category) {
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            category,
            this.className,
            this.fieldName,
            this.methods,
            this.additionalData,
            this.parameterTypes,
            this.expectType,
            this.foundFirst,
            this.searchByParameters
         );
      }

      public TypeCache.CacheBuilder withClassName(String className) {
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            this.cacheCategory,
            ImmutableList.builder().addAll(this.className).add(this.formatClass(className)).build(),
            this.fieldName,
            this.methods,
            this.additionalData,
            this.parameterTypes,
            this.expectType,
            this.foundFirst,
            this.searchByParameters
         );
      }

      public TypeCache.CacheBuilder withClassName(Class<?> clazz) {
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            this.cacheCategory,
            ImmutableList.builder().addAll(this.className).add(clazz == null ? "" : clazz.getName()).build(),
            this.fieldName,
            this.methods,
            this.additionalData,
            this.parameterTypes,
            this.expectType,
            this.foundFirst,
            this.searchByParameters
         );
      }

      public TypeCache.CacheBuilder withMethodName(String methodName) {
         return this.withMethodName(methodName, true);
      }

      public TypeCache.CacheBuilder withMethodName(String methodName, boolean isSupported) {
         Multimap<String, Boolean> newMethods = LinkedHashMultimap.create(this.methods);
         newMethods.put(methodName, isSupported);
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            this.cacheCategory,
            this.className,
            this.fieldName,
            newMethods,
            this.additionalData,
            this.parameterTypes,
            this.expectType,
            this.foundFirst,
            this.searchByParameters
         );
      }

      public TypeCache.CacheBuilder withMethodName(String methodName, VersionRange... versionRanges) {
         return this.withMethodName(methodName, Arrays.stream(versionRanges).anyMatch(vr -> vr.rangeContains(Version.SERVER_VERSION)));
      }

      public TypeCache.CacheBuilder withFieldName(String fieldName) {
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            this.cacheCategory,
            this.className,
            fieldName,
            this.methods,
            this.additionalData,
            this.parameterTypes,
            this.expectType,
            this.foundFirst,
            this.searchByParameters
         );
      }

      public TypeCache.CacheBuilder withAdditionalData(String additionalData) {
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            this.cacheCategory,
            this.className,
            this.fieldName,
            this.methods,
            additionalData,
            this.parameterTypes,
            this.expectType,
            this.foundFirst,
            this.searchByParameters
         );
      }

      public TypeCache.CacheBuilder withParameterTypes(Class<?>... types) {
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            this.cacheCategory,
            this.className,
            this.fieldName,
            this.methods,
            this.additionalData,
            ImmutableList.copyOf(Iterables.concat(this.parameterTypes, ImmutableList.of(types))),
            this.expectType,
            this.foundFirst,
            this.searchByParameters
         );
      }

      public TypeCache.CacheBuilder withExpectResult(Class<?> expectType) {
         return new TypeCache.CacheBuilder(
            this.cachePackage,
            this.cacheCategory,
            this.className,
            this.fieldName,
            this.methods,
            this.additionalData,
            this.parameterTypes,
            expectType,
            this.foundFirst,
            this.searchByParameters
         );
      }

      protected String formatClass(String className) {
         switch (this.cachePackage) {
            case MINECRAFT_SERVER:
            case CRAFT_BUKKIT:
               return String.format(
                  (
                        this.cachePackage == CachePackage.CRAFT_BUKKIT
                           ? this.cachePackage.getFixedPackageName()
                           : this.cachePackage.getForCategory(this.cacheCategory, this.additionalData)
                     )
                     + ".%s",
                  className
               );
            case DEFAULT:
               return className;
            default:
               throw new IllegalArgumentException("Unexpected package " + this.cachePackage.name());
         }
      }
   }

   class ClassCache {
      protected static final ConcurrentMap<TypeCache.ClassCache.CacheKey, Object> CACHE = new ConcurrentHashMap<>();

      public static Object find(String name, Class<?> objectClass) {
         return CACHE.get(new TypeCache.ClassCache.CacheKey(name, objectClass));
      }

      public static void register(String name, Object object, Class<?> objectClass) {
         CACHE.putIfAbsent(new TypeCache.ClassCache.CacheKey(name, objectClass), object);
      }

      private static class CacheKey {
         private final Class<?> type;
         private final String value;

         public CacheKey(String value, Class<?> type) {
            this.type = type;
            this.value = value;
         }

         @Override
         public boolean equals(Object o) {
            if (this == o) {
               return true;
            } else if (o != null && this.getClass() == o.getClass()) {
               TypeCache.ClassCache.CacheKey classKey = (TypeCache.ClassCache.CacheKey)o;
               return Objects.equals(this.type, classKey.type) && Objects.equals(this.value, classKey.value);
            } else {
               return false;
            }
         }

         @Override
         public int hashCode() {
            return Objects.hash(this.type, this.value);
         }
      }
   }
}
