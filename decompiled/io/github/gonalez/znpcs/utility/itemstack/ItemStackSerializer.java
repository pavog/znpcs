package io.github.gonalez.znpcs.utility.itemstack;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
   private static final ItemStack DEFAULT = new ItemStack(Material.AIR);

   private ItemStack deserializeItemStack(byte[] data) {
      try {
         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

         ItemStack var4;
         try {
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

            try {
               var4 = (ItemStack)bukkitObjectInputStream.readObject();
            } catch (Throwable var8) {
               try {
                  bukkitObjectInputStream.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            bukkitObjectInputStream.close();
         } catch (Throwable var9) {
            try {
               byteArrayInputStream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         byteArrayInputStream.close();
         return var4;
      } catch (IOException | ClassNotFoundException e) {
         return DEFAULT;
      }
   }

   public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      String base64String = json.getAsString();
      byte[] data = Base64.getDecoder().decode(base64String);
      return this.deserializeItemStack(data);
   }

   private String serializeItemStack(ItemStack itemStack) {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

         String var4;
         try {
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);

            try {
               bukkitObjectOutputStream.writeObject(itemStack);
               var4 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            } catch (Throwable var8) {
               try {
                  bukkitObjectOutputStream.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            bukkitObjectOutputStream.close();
         } catch (Throwable var9) {
            try {
               byteArrayOutputStream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }

            throw var9;
         }

         byteArrayOutputStream.close();
         return var4;
      } catch (IOException e) {
         throw new JsonParseException("Cannot serialize itemstack", e);
      }
   }

   public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
      String serialized = this.serializeItemStack(src);
      return new JsonPrimitive(serialized);
   }
}
