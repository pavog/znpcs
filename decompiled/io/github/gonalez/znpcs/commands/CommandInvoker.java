package io.github.gonalez.znpcs.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.command.CommandSender;

public class CommandInvoker {
   private final Command command;
   private final Method commandMethod;
   private final String permission;

   public CommandInvoker(Command command, Method commandMethod, String permission) {
      this.command = command;
      this.commandMethod = commandMethod;
      this.permission = permission;
   }

   public void execute(CommandSender sender, Object command) throws CommandPermissionException, CommandExecuteException {
      if (!this.permission.isEmpty() && !sender.hasPermission(this.permission)) {
         throw new CommandPermissionException("Insufficient permission.");
      }

      try {
         this.commandMethod.invoke(this.command, sender, command);
      } catch (IllegalAccessException | InvocationTargetException e) {
         throw new CommandExecuteException(e.getMessage(), e.getCause());
      }
   }
}
