package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class Command extends BukkitCommand {
   private static final String WHITESPACE = " ";
   private static final CommandMap COMMAND_MAP;
   private final Map<CommandInformation, CommandInvoker> subCommands = new HashMap<>();

   public Command(String name) {
      super(name);
      this.load();
   }

   private void load() {
      COMMAND_MAP.register(this.getName(), this);

      for (Method method : this.getClass().getMethods()) {
         if (method.isAnnotationPresent(CommandInformation.class)) {
            CommandInformation cmdInfo = method.getAnnotation(CommandInformation.class);
            this.subCommands.put(cmdInfo, new CommandInvoker(this, method, cmdInfo.permission()));
         }
      }
   }

   public Set<CommandInformation> getCommands() {
      return this.subCommands.keySet();
   }

   @Nullable
   protected Entry<CommandInformation, CommandInvoker> getSubcommandForArgs(String[] args) {
      return this.subCommands
         .entrySet()
         .stream()
         .filter(command -> command.getKey().name().contentEquals(args.length > 0 ? args[0] : ""))
         .findFirst()
         .orElse(null);
   }

   public boolean execute(CommandSender sender, String commandLabel, String[] args) {
      Entry<CommandInformation, CommandInvoker> test = this.getSubcommandForArgs(args);
      if (test == null) {
         sender.sendMessage(ChatColor.RED + "Unknown subcommand for arguments.");
         return false;
      }

      try {
         ImmutableList<String> list = ImmutableList.copyOf(args);
         test.getValue().execute(sender, ImmutableList.copyOf(Iterables.skip(list, 1)));
      } catch (CommandExecuteException e) {
         sender.sendMessage(ChatColor.RED + "Failed to execute command.");
         e.printStackTrace();
      } catch (CommandPermissionException e) {
         sender.sendMessage(ChatColor.RED + "No permission.");
      }

      return true;
   }

   static {
      try {
         COMMAND_MAP = (CommandMap)CacheRegistry.BUKKIT_COMMAND_MAP.load().get(Bukkit.getServer());
      } catch (IllegalAccessException exception) {
         throw new IllegalStateException("can't access bukkit command map.");
      }
   }
}
