package io.github.gonalez.znpcs.configuration;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.utility.Utils;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;

public class Configuration {
   private static final Method USE_OLD_PARAMETER_GSON;
   static final String CONFIG_FORMAT = ".json";
   private static final JsonParser JSON_PARSER;
   private static final Charset CHARSET;
   private final String name;
   private final Path path;
   private final Map<ConfigurationValue, Object> configurationValues;
   public static final Configuration CONFIGURATION;
   public static final Configuration MESSAGES;
   public static final Configuration CONVERSATIONS;
   public static final Configuration DATA;
   public static final ImmutableList<Configuration> SAVE_CONFIGURATIONS;

   protected Configuration(String name) {
      this(name, ServersNPC.PLUGIN_FOLDER.toPath().resolve(name + ".json"));
   }

   private Configuration(String name, Path path) {
      if (!path.getFileName().toString().endsWith(".json")) {
         throw new IllegalStateException("invalid configuration format for: " + path.getFileName());
      }

      this.name = name;
      this.path = path;
      this.configurationValues = ConfigurationValue.VALUES_BY_NAME
         .get(name)
         .stream()
         .collect(Collectors.toMap(c -> (ConfigurationValue)c, ConfigurationValue::getValue));
      this.onLoad();
   }

   protected void onLoad() {
      synchronized (this.path) {
         try {
            Reader reader = Files.newBufferedReader(this.path, CHARSET);

            try {
               JsonElement data = JSON_PARSER.parse(reader);
               if (data != null) {
                  for (ConfigurationValue configValue : this.configurationValues.keySet()) {
                     boolean single = this.configurationValues.size() == 1;
                     JsonElement jsonElement = single ? data : (data.isJsonObject() ? data.getAsJsonObject().get(configValue.name()) : null);
                     if (jsonElement != null && !jsonElement.isJsonNull()) {
                        if (!single && configValue.getPrimitiveType().isEnum()) {
                           this.configurationValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement, configValue.getPrimitiveType()));
                        } else {
                           if (USE_OLD_PARAMETER_GSON != null) {
                              Type type = (Type)USE_OLD_PARAMETER_GSON.invoke(
                                 null, null, configValue.getValue().getClass(), new Type[]{configValue.getPrimitiveType()}
                              );
                              this.configurationValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement, type));
                           }

                           Class<?> raw = configValue.getValue().getClass();
                           Type type;
                           if (raw.getTypeParameters().length == 0) {
                              type = raw;
                           } else {
                              type = TypeToken.getParameterized(raw, new Type[]{configValue.getPrimitiveType()}).getType();
                           }

                           this.configurationValues.put(configValue, ServersNPC.GSON.fromJson(jsonElement, type));
                        }
                     }
                  }

                  if (reader != null) {
                     reader.close();
                  }

                  return;
               }

               if (reader != null) {
                  reader.close();
               }
            } catch (Throwable throwable) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable throwable1) {
                     throwable.addSuppressed(throwable1);
                  }

                  return;
               }

               return;
            }
         } catch (NoSuchFileException var20) {
            return;
         } catch (IOException e) {
            throw new IllegalStateException("Failed to read config: " + this.name);
         } finally {
            this.save();
         }
      }
   }

   public void save() {
      synchronized (this.path) {
         try {
            Writer writer = Files.newBufferedWriter(this.path, CHARSET);

            try {
               ServersNPC.GSON
                  .toJson(this.configurationValues.size() == 1 ? this.configurationValues.values().iterator().next() : this.configurationValues, writer);
               if (writer != null) {
                  writer.close();
               }
            } catch (Throwable throwable) {
               if (writer != null) {
                  try {
                     writer.close();
                  } catch (Throwable throwable1) {
                     throwable.addSuppressed(throwable1);
                  }
               }

               throw throwable;
            }
         } catch (IOException e) {
            throw new IllegalStateException("Failed to save config: " + this.name);
         }
      }
   }

   public <T> T getValue(ConfigurationValue configValue) {
      synchronized (this.path) {
         return (T)this.configurationValues.get(configValue);
      }
   }

   public void sendMessage(CommandSender sender, ConfigurationValue configValue, Object... replaces) {
      sender.sendMessage(Utils.toColor(String.format(this.getValue(configValue), replaces)));
   }

   static {
      Method useOldParameterGson1;
      try {
         Class<?> gsonTypes = Class.forName("com.google.gson.internal.$Gson$Types");
         useOldParameterGson1 = gsonTypes.getDeclaredMethod("newParameterizedTypeWithOwner", Type.class, Type.class, Type[].class);
         useOldParameterGson1.setAccessible(true);
      } catch (ClassNotFoundException | NoSuchMethodException ignored) {
         useOldParameterGson1 = null;
      }

      USE_OLD_PARAMETER_GSON = useOldParameterGson1;
      JSON_PARSER = new JsonParser();
      CHARSET = StandardCharsets.UTF_8;
      CONFIGURATION = new Configuration("config");
      MESSAGES = new Configuration("messages");
      CONVERSATIONS = new Configuration("conversations");
      DATA = new Configuration("data");
      SAVE_CONFIGURATIONS = ImmutableList.of(CONVERSATIONS, DATA);
   }
}
