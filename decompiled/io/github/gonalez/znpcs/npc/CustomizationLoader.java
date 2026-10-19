package io.github.gonalez.znpcs.npc;

import com.google.common.collect.Iterables;
import io.github.gonalez.znpcs.cache.CachePackage;
import io.github.gonalez.znpcs.cache.TypeCache;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class CustomizationLoader {
   private final Class<? extends Entity> entityClass;
   private final Map<String, Method> methods;

   public CustomizationLoader(EntityType entityType, Iterable<String> methodsName) {
      this(entityType.getEntityClass(), methodsName);
   }

   protected CustomizationLoader(Class<? extends Entity> entityClass, Iterable<String> methodsName) {
      this.entityClass = entityClass;
      this.methods = this.loadMethods(methodsName);
   }

   protected Map<String, Method> loadMethods(Iterable<String> iterable) {
      Map<String, Method> builder = new HashMap<>();

      for (Method method : this.entityClass.getMethods()) {
         if (!builder.containsKey(method.getName()) && Iterables.contains(iterable, method.getName())) {
            for (Class<?> parameter : method.getParameterTypes()) {
               TypeProperty typeProperty = TypeProperty.forType(parameter);
               if (typeProperty == null) {
                  if (parameter.isEnum()) {
                     new TypeCache.BaseCache.EnumLoader(new TypeCache.CacheBuilder(CachePackage.DEFAULT).withClassName(parameter.getTypeName())).load();
                  } else {
                     maybeLoadInstanceFields(parameter);
                  }
               }
            }

            builder.put(method.getName(), method);
         }
      }

      return builder;
   }

   private static void maybeLoadInstanceFields(Class<?> parameter) {
      for (Field field : parameter.getDeclaredFields()) {
         if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && parameter.isAssignableFrom(field.getType())) {
            new TypeCache.BaseCache.FieldLoader(new TypeCache.CacheBuilder(CachePackage.DEFAULT).withClassName(parameter).withFieldName(field.getName()))
               .load();
         }
      }
   }

   public boolean contains(String name) {
      return this.methods.containsKey(name);
   }

   public Map<String, Method> getMethods() {
      return this.methods;
   }
}
