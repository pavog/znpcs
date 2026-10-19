package io.github.gonalez.znpcs.commands.list;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.commands.CommandArgument;
import io.github.gonalez.znpcs.commands.CommandInformation;
import io.github.gonalez.znpcs.commands.CommandSenderUtil;
import io.github.gonalez.znpcs.commands.list.inventory.ConversationGUI;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.configuration.ConfigurationValue;
import io.github.gonalez.znpcs.npc.FunctionContext;
import io.github.gonalez.znpcs.npc.FunctionFactory;
import io.github.gonalez.znpcs.npc.ItemSlot;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCAction;
import io.github.gonalez.znpcs.npc.NPCFunction;
import io.github.gonalez.znpcs.npc.NPCModel;
import io.github.gonalez.znpcs.npc.NPCPath;
import io.github.gonalez.znpcs.npc.NPCSkin;
import io.github.gonalez.znpcs.npc.NPCType;
import io.github.gonalez.znpcs.npc.conversation.Conversation;
import io.github.gonalez.znpcs.npc.conversation.ConversationModel;
import io.github.gonalez.znpcs.skin.SkinFetcher;
import io.github.gonalez.znpcs.skin.SkinFetcherServer;
import io.github.gonalez.znpcs.skin.SkinProperties;
import io.github.gonalez.znpcs.user.ZUser;
import io.github.gonalez.znpcs.utility.PlaceholderUtils;
import io.github.gonalez.znpcs.utility.Utils;
import io.github.gonalez.znpcs.utility.location.ZLocation;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class DefaultCommand extends AbstractCommand {
   private static final Joiner SPACE_JOINER = Joiner.on(" ");
   private final Path path;
   private final SkinFetcher skinFetcher;

   public DefaultCommand(Path path, SkinFetcher skinFetcher) {
      this.path = path;
      this.skinFetcher = skinFetcher;
      this.registerTabComplete(
         "npc-id", strings -> ConfigurationConstants.NPC_LIST.stream().map(npcModel -> String.valueOf(npcModel.getId())).collect(Collectors.toList())
      );
      this.registerTabComplete("type", strings -> Arrays.stream(NPCType.values()).map(Enum::name).collect(Collectors.toList()));
      this.registerTabComplete("toggle-type", strings -> FunctionFactory.ALL.stream().map(NPCFunction::getName).collect(Collectors.toList()));
      this.registerTabComplete("method", new Function<List<String>, List<String>>() {
         public List<String> apply(List<String> strings) {
            Integer id = Ints.tryParse(strings.get(1));
            if (id == null) {
               return ImmutableList.of();
            }

            NPC npc = NPC.find(id);
            return npc == null ? ImmutableList.of() : ImmutableList.copyOf(npc.getNpcPojo().getNpcType().getCustomizationLoader().getMethods().keySet());
         }
      });
   }

   private void fetchSkin(final CommandSender commandSender, final NPC npc, final String name, @Nullable final SkinFetcher.SkinFetchListener listener) {
      Configuration.MESSAGES.sendMessage(commandSender, ConfigurationValue.FETCHING_SKIN, name);
      this.skinFetcher.fetchSkin(name, new SkinFetcher.SkinFetchListener() {
         @Override
         public void onSuccess(SkinFetcherServer server, SkinProperties skinProperties) {
            npc.changeSkin(NPCSkin.forValues(skinProperties.getValue(), skinProperties.getSignature()), true);
            commandSender.sendMessage(ChatColor.GREEN + "Skin data received from: " + ChatColor.WHITE + server.getName());
            Configuration.MESSAGES.sendMessage(commandSender, ConfigurationValue.GET_SKIN);
            if (listener != null) {
               listener.onSuccess(server, skinProperties);
            }
         }

         @Override
         public void onError(Throwable throwable) {
            Configuration.MESSAGES.sendMessage(commandSender, ConfigurationValue.CANT_GET_SKIN, name);
         }
      });
   }

   @CommandInformation(arguments = {}, name = "", permission = "")
   public void defaultCommand(CommandSender sender, ImmutableList<String> args) {
      sender.sendMessage(Utils.toColor("&2&l------ &fZNPCs by &7ZNetwork &2&l------"));
      sender.sendMessage(Utils.toColor("&7&nhttps://www.spigotmc.org/resources/znpcs.80940"));
      sender.sendMessage("");
      sender.sendMessage(ChatColor.GRAY + "Commands:");

      for (CommandInformation command : this.getCommands()) {
         if (!command.name().isEmpty()) {
            CommandSenderUtil.sendMessage(sender, command);
         }
      }

      sender.sendMessage("");
      sender.sendMessage(
         Utils.toColor("&7Hover over commands for helpful tips. Require further assistance? Join our Discord community: &bhttps://discord.com/invite/RhNMH4T")
      );
      sender.sendMessage(Utils.toColor("&2&l-----------------------------"));
   }

   @CommandInformation(
      arguments = @CommandArgument({"id", "type", "name"}),
      name = "create",
      permission = "znpcs.cmd.create",
      help = " &f&l* &e/znpcs create <npc_id> PLAYER Qentin"
   )
   public void createNPC(CommandSender sender, ImmutableList<String> args) {
      if (args.size() < 3) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            boolean foundNPC = ConfigurationConstants.NPC_LIST.stream().anyMatch(npcx -> npcx.getId() == id);
            if (foundNPC) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_FOUND);
            } else {
               NPCType npcType = NPCType.valueOf(((String)args.get(1)).toUpperCase());
               String name = ((String)args.get(2)).trim();
               if (name.length() >= 3 && name.length() <= 16) {
                  NPC npc = ServersNPC.createNPC(id, npcType, ((Player)sender).getLocation(), name);
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
                  if (npcType == NPCType.PLAYER) {
                     this.fetchSkin(sender, npc, name, null);
                  }
               } else {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NAME_LENGTH);
               }
            }
         }
      }
   }

   @CommandInformation(arguments = @CommandArgument("npc-id"), name = "delete", permission = "znpcs.cmd.delete", help = " &f&l* &e/znpcs delete <npc_id>")
   public void deleteNPC(CommandSender sender, ImmutableList<String> args) {
      if (args.isEmpty()) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               ServersNPC.deleteNPC(id);
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
            }
         }
      }
   }

   @CommandInformation(arguments = {}, name = "list", permission = "znpcs.cmd.list")
   public void list(CommandSender sender, ImmutableList<String> args) {
      if (ConfigurationConstants.NPC_LIST.isEmpty()) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_NPC_FOUND);
      } else {
         sender.sendMessage(ChatColor.DARK_GREEN + "NPC list:");

         for (NPCModel npcModel : ConfigurationConstants.NPC_LIST) {
            List<BaseComponent> parts = new ArrayList<>();
            String message = "- "
               + npcModel.getId()
               + " "
               + npcModel.getHologramLines().toString()
               + " ("
               + npcModel.getLocation().getWorldName()
               + " "
               + (int)npcModel.getLocation().getX()
               + " "
               + (int)npcModel.getLocation().getY()
               + " "
               + (int)npcModel.getLocation().getZ()
               + ") ";
            TextComponent textComponent = new TextComponent(message);
            textComponent.setColor(ChatColor.GREEN);
            parts.add(textComponent);
            TextComponent textComponent2 = new TextComponent("[TELEPORT]");
            textComponent2.setBold(Boolean.TRUE);
            textComponent2.setColor(ChatColor.DARK_GREEN);
            textComponent2.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to teleport this npc!").color(ChatColor.GREEN).create()));
            textComponent2.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/znpcs teleport " + npcModel.getId()));
            parts.add(textComponent2);
            parts.add(new TextComponent(" "));
            TextComponent textComponent3 = new TextComponent("[DELETE]");
            textComponent3.setBold(Boolean.TRUE);
            textComponent3.setColor(ChatColor.DARK_RED);
            textComponent3.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to delete this npc!").color(ChatColor.RED).create()));
            textComponent3.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/znpcs delete " + npcModel.getId()));
            parts.add(textComponent3);
            ((Player)sender).spigot().sendMessage(parts.toArray(new BaseComponent[0]));
         }
      }
   }

   @CommandInformation(
      arguments = @CommandArgument({"npc-id", "skin"}),
      name = "skin",
      permission = "znpcs.cmd.skin",
      help = {
            " &f&l* &e/znpcs skin <npc_id> Notch",
            " &f&l* &e/znpcs skin <npc_id> %someplugin_balance_top_1% refreshSkinDuration",
            "&7If &erefreshSkinDuration &7is present in the command arguments",
            "&7the skin will be 'refreshed' periodically every that duration, ",
            "&7in seconds. The skin will be refreshed using the skin name, ",
            "&7placeholders are supported, example: &e%someplugin_balance_top_1%"
      }
   )
   public void setSkin(final CommandSender sender, final ImmutableList<String> args) {
      if (args.isEmpty()) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            final NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               String skin = ((String)args.get(1)).trim();
               foundNPC.getNpcPojo().setSkinName(skin);
               this.fetchSkin(
                  sender,
                  foundNPC,
                  PlaceholderUtils.formatPlaceholders(skin),
                  new SkinFetcher.SkinFetchListener() {
                     @Override
                     public void onSuccess(SkinFetcherServer server, SkinProperties skinProperties) {
                        if (args.size() == 3) {
                           Integer refreshSkinDuration = Ints.tryParse((String)args.get(2));
                           if (refreshSkinDuration != null) {
                              foundNPC.getNpcPojo().setRefreshSkinDuration(refreshSkinDuration);
                              sender.sendMessage(
                                 ChatColor.GREEN + "The skin will refresh every: " + ChatColor.YELLOW + refreshSkinDuration + ChatColor.GREEN + " seconds."
                              );
                           }
                        } else {
                           foundNPC.getNpcPojo().setRefreshSkinDuration(0);
                        }
                     }
                  }
               );
            }
         }
      }
   }

   @CommandInformation(
      arguments = @CommandArgument({"npc-id", "slot"}),
      name = "equip",
      permission = "znpcs.cmd.equip",
      help = {" &f&l* &e/znpcs equip <npc_id> [HAND,OFFHAND,HELMET,CHESTPLATE,LEGGINGS,BOOTS]", "&8(You need to have the item in your hand.)"}
   )
   public void equip(CommandSender sender, ImmutableList<String> args) {
      if (args.size() < 2) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               foundNPC.getNpcPojo().getNpcEquip().put(ItemSlot.valueOf(((String)args.get(1)).toUpperCase()), ((Player)sender).getInventory().getItemInHand());
               foundNPC.getPackets().flushCache("equipPackets");
               foundNPC.getViewers().forEach(foundNPC::sendEquipPackets);
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
            }
         }
      }
   }

   @CommandInformation(
      arguments = {@CommandArgument("npc-id"), @CommandArgument({"set", "add", "list", "remove"})},
      name = "lines",
      permission = "znpcs.cmd.lines",
      help = {
            " &f&l* &e/znpcs lines <npc_id> add Example Line",
            " &f&l* &e/znpcs lines <npc_id> remove 0",
            " &f&l* &e/znpcs lines <npc_id> set First Second Third-Space"
      }
   )
   public void changeLines(CommandSender sender, ImmutableList<String> args) {
      if (args.size() < 2) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               if (((String)args.get(1)).equals("clear")) {
                  foundNPC.getNpcPojo().getHologramLines().clear();
               } else {
                  switch ((String)args.get(1)) {
                     case "set":
                        this.setHologramLines(foundNPC, args.subList(2, args.size()));
                        break;
                     case "add":
                        foundNPC.getNpcPojo().getHologramLines().add(SPACE_JOINER.join(args.subList(2, args.size())));
                        break;
                     case "list":
                        sender.sendMessage(ChatColor.YELLOW + "NPC Lines:");

                        for (int i = 0; i < foundNPC.getNpcPojo().getHologramLines().size(); i++) {
                           sender.sendMessage(
                              ChatColor.translateAlternateColorCodes('&', String.format("&a%d &7- " + foundNPC.getNpcPojo().getHologramLines().get(i), i))
                           );
                        }
                        break;
                     case "remove":
                        Integer num = Ints.tryParse((String)args.get(2));
                        if (num == null) {
                           Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
                           return;
                        }

                        if (num >= foundNPC.getNpcPojo().getHologramLines().size()) {
                           Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_LINE_FOUND);
                           return;
                        }

                        foundNPC.getNpcPojo().getHologramLines().remove(foundNPC.getNpcPojo().getHologramLines().get(num));
                        break;
                     default:
                        this.setHologramLines(foundNPC, args.subList(1, args.size()));
                  }
               }

               foundNPC.createHologram();
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
            }
         }
      }
   }

   private void setHologramLines(NPC foundNPC, List<String> lines) {
      List<String> reversedLines = new ArrayList<>(lines);
      Collections.reverse(reversedLines);
      foundNPC.getNpcPojo().setHologramLines(reversedLines);
   }

   @CommandInformation(arguments = @CommandArgument("npc-id"), name = "move", permission = "znpcs.cmd.move", help = " &f&l* &e/znpcs move <npc_id>")
   public void move(CommandSender sender, ImmutableList<String> args) {
      if (args.isEmpty()) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               foundNPC.getNpcPojo().setLocation(new ZLocation(((Player)sender).getLocation()));
               foundNPC.changeType(foundNPC.getNpcPojo().getNpcType());
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
            }
         }
      }
   }

   @CommandInformation(
      arguments = @CommandArgument({"npc-id", "type"}),
      name = "type",
      permission = "znpcs.cmd.type",
      help = " &f&l* &e/znpcs type <npc_id> ZOMBIE"
   )
   public void changeType(CommandSender sender, ImmutableList<String> args) {
      if (args.size() < 2) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               NPCType npcType = NPCType.valueOf(((String)args.get(1)).toUpperCase());
               if (npcType != NPCType.PLAYER && npcType.getConstructor() == null) {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.UNSUPPORTED_ENTITY);
               } else {
                  foundNPC.changeType(npcType);
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
               }
            }
         }
      }
   }

   @CommandInformation(
      name = "action",
      arguments = {@CommandArgument("npc-id"), @CommandArgument({"add", "remove", "cooldown", "list"})},
      permission = "znpcs.cmd.action",
      help = {
            " &f&l* &e/znpcs action <npc_id> add SERVER skywars",
            " &f&l* &e/znpcs action <npc_id> add CMD spawn",
            " &f&l* &e/znpcs action <npc_id> remove <action_id>",
            " &f&l* &e/znpcs action <npc_id> cooldown <action_id> <delay_in_seconds>",
            " &f&l* &e/znpcs action <npc_id> list"
      }
   )
   public void action(CommandSender sender, ImmutableList<String> args) {
      if (args.isEmpty()) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               ImmutableList<String> immutableList = args.subList(1, args.size());
               switch ((String)args.get(1)) {
                  case "add":
                     if (immutableList.isEmpty()) {
                        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.ACTION_ADD_INCORRECT_USAGE);
                        return;
                     }

                     foundNPC.getNpcPojo()
                        .getClickActions()
                        .add(new NPCAction(((String)immutableList.get(1)).toUpperCase(), SPACE_JOINER.join(Iterables.skip(immutableList, 2))));
                     Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
                     break;
                  case "remove":
                     Integer actionId = Ints.tryParse((String)immutableList.get(1));
                     if (actionId == null) {
                        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
                     } else {
                        if (actionId >= foundNPC.getNpcPojo().getClickActions().size()) {
                           Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_ACTION_FOUND);
                           return;
                        }

                        foundNPC.getNpcPojo().getClickActions().remove(actionId.intValue());
                        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
                     }
                     break;
                  case "cooldown":
                     Integer actionDelay = Ints.tryParse((String)immutableList.get(2));
                     Integer actionIdx = Ints.tryParse((String)immutableList.get(1));
                     if (actionIdx != null && actionDelay != null) {
                        if (actionIdx >= foundNPC.getNpcPojo().getClickActions().size()) {
                           Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_ACTION_FOUND);
                           return;
                        }

                        foundNPC.getNpcPojo().getClickActions().get(actionIdx).setDelay(actionDelay);
                        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
                     } else {
                        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
                     }
                     break;
                  case "list":
                     if (foundNPC.getNpcPojo().getClickActions().isEmpty()) {
                        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_ACTION_FOUND);
                     } else {
                        foundNPC.getNpcPojo()
                           .getClickActions()
                           .forEach(s -> sender.sendMessage("&8(&a" + foundNPC.getNpcPojo().getClickActions().indexOf(s) + "&8) &6" + s.toString()));
                     }
               }
            }
         }
      }
   }

   @CommandInformation(
      arguments = @CommandArgument({"npc-id", "toggle-type", "value"}),
      name = "toggle",
      permission = "znpcs.cmd.toggle",
      help = " &f&l* &e/znpcs toggle <npc_id> look"
   )
   public void toggle(CommandSender sender, ImmutableList<String> args) {
      if (args.size() < 2) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               NPCFunction npcFunction = FunctionFactory.findFunctionForName((String)args.get(1));
               if (npcFunction.getName().equalsIgnoreCase("glow")) {
                  npcFunction.doRunFunction(foundNPC, new FunctionContext.ContextWithValue(foundNPC, (String)args.get(2)));
               } else {
                  npcFunction.doRunFunction(foundNPC, new FunctionContext.DefaultContext(foundNPC));
               }

               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
            }
         }
      }
   }

   @CommandInformation(
      arguments = @CommandArgument({"npc-id", "method"}),
      name = "customize",
      permission = "znpcs.cmd.customize",
      help = " &f&l* &e/znpcs customize <npc_id> <customization>"
   )
   public void customize(CommandSender sender, ImmutableList<String> args) {
      if (args.size() < 2) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               NPCType npcType = foundNPC.getNpcPojo().getNpcType();
               ImmutableList<String> immutableList = args.subList(1, args.size());
               String methodName = (String)immutableList.get(0);
               if (npcType.getCustomizationLoader().contains(methodName)) {
                  Method method = npcType.getCustomizationLoader().getMethods().get(methodName);
                  Iterable<String> split = Iterables.skip(immutableList, 1);
                  if (Iterables.size(split) < method.getParameterTypes().length) {
                     Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.TOO_FEW_ARGUMENTS);
                     return;
                  }

                  String[] values = (String[])Iterables.toArray(split, String.class);
                  npcType.updateCustomization(foundNPC, methodName, values);
                  foundNPC.getNpcPojo().getCustomizationMap().put(methodName, values);
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
               } else {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.METHOD_NOT_FOUND);

                  for (Entry<String, Method> method : npcType.getCustomizationLoader().getMethods().entrySet()) {
                     sender.sendMessage(ChatColor.YELLOW + method.getKey() + " " + SPACE_JOINER.join(method.getValue().getParameterTypes()));
                  }
               }
            }
         }
      }
   }

   @CommandInformation(
      arguments = {@CommandArgument("npc-id"), @CommandArgument({"set", "create", "exit", "path", "list"})},
      name = "path",
      isMultiple = true,
      permission = "znpcs.cmd.path",
      help = {" &f&l* &e/znpcs path create name", " &f&l* &e/znpcs path set <npc_id> name"}
   )
   public void path(CommandSender sender, ImmutableList<String> args) {
      if (args.isEmpty()) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         ZUser znpcUser = ZUser.find(Objects.requireNonNull(((Player)sender).getPlayer()));
         if (znpcUser != null) {
            switch ((String)args.get(0)) {
               case "set":
                  Integer id = Ints.tryParse((String)args.get(1));
                  if (id == null) {
                     Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
                     return;
                  }

                  NPC foundNPC = NPC.find(id);
                  if (foundNPC == null) {
                     Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
                     return;
                  }

                  foundNPC.setPath(NPCPath.AbstractTypeWriter.find((String)args.get(2)));
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
                  break;
               case "create":
                  String pathName = (String)args.get(1);
                  if (pathName.length() < 3 || pathName.length() > 16) {
                     Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NAME_LENGTH);
                     return;
                  }

                  if (NPCPath.AbstractTypeWriter.find(pathName) != null) {
                     Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.PATH_FOUND);
                     return;
                  }

                  if (znpcUser.isHasPath()) {
                     sender.sendMessage(ChatColor.RED + "You already have a path creator active, to remove it use /znpcs path exit.");
                     return;
                  }

                  File file = this.path.resolve(pathName + ".path").toFile();
                  NPCPath.AbstractTypeWriter.forCreation(file, znpcUser, NPCPath.AbstractTypeWriter.TypeWriter.MOVEMENT);
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.PATH_START);
                  break;
               case "exit":
                  znpcUser.setHasPath(false);
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.EXIT_PATH);
                  break;
               case "list":
                  if (NPCPath.AbstractTypeWriter.getPaths().isEmpty()) {
                     Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_PATH_FOUND);
                  } else {
                     NPCPath.AbstractTypeWriter.getPaths().forEach(path -> sender.sendMessage(ChatColor.GREEN + path.getName()));
                  }
            }
         }
      }
   }

   @CommandInformation(
      arguments = @CommandArgument("npc-id"),
      name = "teleport",
      permission = "znpcs.cmd.teleport",
      help = " &f&l* &e/znpcs teleport <npc_id>"
   )
   public void teleport(CommandSender sender, ImmutableList<String> args) {
      if (args.isEmpty()) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               ((Player)sender).teleport(foundNPC.getLocation());
            }
         }
      }
   }

   @CommandInformation(
      arguments = @CommandArgument({"npc-id", "line-height"}),
      name = "height",
      permission = "znpcs.cmd.height",
      help = {" &f&l* &e/znpcs height <npc_id> 2", "&8Add more height to the hologram of the npc"}
   )
   public void changeHologramHeight(CommandSender sender, ImmutableList<String> args) {
      if (args.size() < 2) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               Double givenHeight = Doubles.tryParse((String)args.get(1));
               if (givenHeight == null) {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
               } else {
                  foundNPC.getNpcPojo().setHologramHeight(givenHeight);
                  foundNPC.createHologram();
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
               }
            }
         }
      }
   }

   @CommandInformation(
      arguments = @CommandArgument({"create", "remove", "gui", "set"}),
      name = "conversation",
      isMultiple = true,
      permission = "znpcs.cmd.conversation",
      help = {
            " &f&l* &e/znpcs conversation create first",
            " &f&l* &e/znpcs conversation remove first",
            " &f&l* &e/znpcs conversation set <npc_id> first [CLICK:RADIUS]",
            " &f&l* &e/znpcs conversation gui &8(&7Open a gui to manage conversations&8)",
            "",
            "&8RADIUS: &7it is activated when the player is near the npc",
            "&8CLICK: &7it is activated when the player interacts with the npc"
      }
   )
   public void conversations(CommandSender sender, ImmutableList<String> args) {
      if (args.isEmpty()) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         switch ((String)args.get(0)) {
            case "create":
               String conversationName = (String)args.get(1);
               if (Conversation.exists(conversationName)) {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.CONVERSATION_FOUND);
                  return;
               }

               if (conversationName.length() < 3 || conversationName.length() > 16) {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NAME_LENGTH);
                  return;
               }

               ConfigurationConstants.NPC_CONVERSATIONS.add(new Conversation(conversationName));
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
               break;
            case "remove":
               String conversationName = (String)args.get(1);
               if (!Conversation.exists(conversationName)) {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_CONVERSATION_FOUND);
                  return;
               }

               ConfigurationConstants.NPC_CONVERSATIONS.remove(Conversation.forName(conversationName));
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
               break;
            case "gui":
               ((Player)sender).openInventory(new ConversationGUI((Player)sender).build());
               break;
            case "set":
               ImmutableList<String> immutableList = args.subList(1, args.size());
               if (immutableList.size() < 2) {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.CONVERSATION_SET_INCORRECT_USAGE);
                  return;
               }

               Integer id = Ints.tryParse((String)immutableList.get(0));
               if (id == null) {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
                  return;
               }

               NPC foundNPC = NPC.find(id);
               if (foundNPC == null) {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
                  return;
               }

               String conversationName = (String)immutableList.get(1);
               if (Conversation.exists(conversationName)) {
                  foundNPC.getNpcPojo()
                     .setConversation(new ConversationModel(conversationName, immutableList.size() > 1 ? (String)immutableList.get(2) : "CLICK"));
               } else {
                  foundNPC.getNpcPojo().setConversation(null);
               }

               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
         }
      }
   }

   @CommandInformation(
      arguments = @CommandArgument({"npc-id", "scale"}),
      name = "scale",
      permission = "znpcs.cmd.scale",
      help = " &f&l* &e/znpcs scale <npc_id> 2"
   )
   public void scale(CommandSender sender, ImmutableList<String> args) {
      if (args.size() < 2) {
         Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      } else {
         Integer id = Ints.tryParse((String)args.get(0));
         if (id == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
         } else {
            NPC foundNPC = NPC.find(id);
            if (foundNPC == null) {
               Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
            } else {
               Double scale = Doubles.tryParse((String)args.get(1));
               if (scale == null) {
                  Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
               } else {
                  try {
                     Attribute attribute = Attribute.valueOf("SCALE");
                     Entity livingEntity = (Entity)foundNPC.getBukkitEntity();
                     foundNPC.getNpcPojo().getBukkitAttributes().put(attribute.name(), Math.max(1.0, scale));
                     double height = scale + livingEntity.getHeight() * scale / 2.0 - livingEntity.getHeight();
                     height += foundNPC.getNpcPojo().getNpcType().getHoloHeight();
                     foundNPC.getNpcPojo().setHologramHeight(height - 0.1);
                     foundNPC.respawn();
                     Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
                  } catch (IllegalArgumentException exception) {
                     sender.sendMessage(ChatColor.RED + "Scale command is only supported in 1.20.5+");
                  }
               }
            }
         }
      }
   }
}
