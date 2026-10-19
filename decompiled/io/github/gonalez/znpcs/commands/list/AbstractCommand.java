package io.github.gonalez.znpcs.commands.list;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.commands.Command;
import io.github.gonalez.znpcs.commands.CommandArgument;
import io.github.gonalez.znpcs.commands.CommandInformation;
import io.github.gonalez.znpcs.commands.CommandInvoker;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;

abstract class AbstractCommand extends Command {
   private final Map<String, Function<List<String>, List<String>>> tabCompleteMap = new HashMap<>();

   protected AbstractCommand() {
      super("znpcs");
   }

   void registerTabComplete(String alias, Function<List<String>, List<String>> stringListFunction) {
      this.tabCompleteMap.put(alias, stringListFunction);
   }

   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      Entry<CommandInformation, CommandInvoker> entry = this.getSubcommandForArgs(args);
      if (args.length == 1) {
         return this.getCommands().stream().map(CommandInformation::name).filter(string -> !string.isEmpty()).collect(Collectors.toList());
      }

      if (entry != null) {
         CommandArgument[] cmdArgs = entry.getKey().arguments();
         if (cmdArgs.length != 0) {
            CommandArgument argument = cmdArgs[0];
            if (argument.value().length >= args.length - 1 && args.length >= 2 && this.tabCompleteMap.containsKey(argument.value()[args.length - 2])) {
               return this.tabCompleteMap.get(argument.value()[args.length - 2]).apply(ImmutableList.copyOf(args));
            }
         }
      }

      return Collections.emptyList();
   }
}
