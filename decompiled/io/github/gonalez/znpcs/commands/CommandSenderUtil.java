package io.github.gonalez.znpcs.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.github.gonalez.znpcs.utility.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandSenderUtil {
   private static final Joiner LINE_SEPARATOR_JOINER = Joiner.on("\n");
   private static final ImmutableList<String> HELP_PREFIX = ImmutableList.of("&7Examples:");

   public static void sendMessage(CommandSender sender, CommandInformation subCommand) {
      TextComponent textComponent = new TextComponent(ChatColor.GRAY + "/znpcs " + subCommand.name() + " ");
      CommandArgument[] arguments = subCommand.arguments();

      for (int i = 0; i < arguments.length; i++) {
         CommandArgument argument = arguments[i];
         textComponent.addExtra("<");
         String[] values = argument.value();

         for (int j = 0; j < values.length; j++) {
            textComponent.addExtra(values[j]);
            if (j < values.length - 1) {
               textComponent.addExtra(":");
            }
         }

         textComponent.addExtra(">");
         if (i < arguments.length - 1) {
            textComponent.addExtra(" ");
         }
      }

      if (subCommand.help() != null) {
         textComponent.setHoverEvent(
            new HoverEvent(
               Action.SHOW_TEXT,
               new ComponentBuilder(Utils.toColor(LINE_SEPARATOR_JOINER.join(Iterables.concat(HELP_PREFIX, ImmutableList.copyOf(subCommand.help()))))).create()
            )
         );
      }

      if (sender instanceof Player) {
         ((Player)sender).spigot().sendMessage(textComponent);
      } else {
         sender.sendMessage(textComponent.getText());
      }
   }
}
