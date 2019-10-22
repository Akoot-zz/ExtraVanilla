package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.Titles;
import net.akoot.plugins.extravanilla.serializable.Title;
import net.akoot.plugins.ultravanilla.Strings;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class TitleCommand extends UltraCommand implements CommandExecutor, TabCompleter {

    public TitleCommand(JavaPlugin plugin, Strings strings) {
        super(plugin, strings, ChatColor.GRAY);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Sub-commands: none
        if (args.length == 0) {
            // Send usage
            return false;
        }

        /*
         list
         show-all
        */
        else if (args.length == 1) {

            // list
            if (args[0].equalsIgnoreCase("list")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    sender.sendMessage(list(command, "list.unlocked", Titles.getUnlockedTitlesFormatted(player, color), "%p", player.getName()));
                } else {
                    sender.sendMessage(uvStrings.getString("error.player-only"));
                }
            }

            // show-all
            else if (args[0].equalsIgnoreCase("show-all")) {
                sender.sendMessage(list(command, "list.all", Titles.getTitlesFormatted(color)));
            } else {
                return false;
            }
        }

        /*
         list <player>
         delete <id>
         set <id>
        */
        else if (args.length == 2) {

            // list <player>
            if (args[0].equalsIgnoreCase("list")) {
                if (hasPermission(sender, command, "list.other")) {
                    OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
                    sender.sendMessage(list(command, "list.all", Titles.getUnlockedTitlesFormatted(player, color), "%p", player.getName()));
                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "list other's unlocked titles"));
                }
            }

            // delete <id>
            else if (args[0].equalsIgnoreCase("delete")) {
                if (hasPermission(sender, command, "create")) {
                    Title title = Titles.getTitle(args[1]);
                    if (title != null) {
                        Titles.delete(title);
                        sender.sendMessage(message(command, "delete", "%t", title.formatted(color)));
                    }
                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "delete titles"));
                }
            }

            // set <id>
            else if (args[0].equalsIgnoreCase("set")) {

                // Check if the sender is a player
                if (sender instanceof Player) {

                    // Set the player and id
                    Player player = (Player) sender;
                    String id = args[1];
                    Title title = Titles.getTitle(id);

                    // Check if the id is valid
                    if (title == null) {
                        sender.sendMessage(error(command, "invalid-id", "%i", id));
                        return true;
                    }

                    // Check if the player has the title unlocked
                    if (Titles.hasTitle(player, id)) {
                        Titles.set(player, id);
                        sender.sendMessage(message(command, "set.self", "%t", title.formatted(color)));
                    } else {
                        sender.sendMessage(error(command, "locked-title", "%t", title.formatted(color)));
                    }
                } else {
                    sender.sendMessage(uvStrings.getString("error.player-only"));
                }
            }

            // bad syntax
            else {
                return false;
            }
        }

        /*
         modify-rarity <id> <rarity>
         modify-id <id> <id>
         set <player> <id>
         give <player> <id>
         remove <player> <id>
        */
        else if (args.length == 3) {

            // Set id for all sub-commands
            String id = args[1];

            // Create a player instance if the sub-command is "give", "set" or "give"
            OfflinePlayer player = null;

            // Set id to the 3rd argument if the sub-command is "give", "set" or "give" and set player to the 2nd argument
            if (args[0].toLowerCase().matches("give|set|remove")) {

                // Set id to the 3rd argument
                id = args[2];

                // Set player to the 2nd argument
                player = plugin.getServer().getOfflinePlayer(args[1]);

                // Check if the player is valid, if not send an error message
                if (!isValid(player)) {
                    sender.sendMessage(uvStrings.getString("error.player-null", "%p", args[1]));
                    return true;
                }
            }

            // Get the title from the id
            Title title = Titles.getTitle(id);

            // Check if the id is valid
            if (title == null) {
                sender.sendMessage(error(command, "invalid-id", "%i", id));
                return true;
            }

            // modify-rarity <id> <rarity>
            if (args[0].equalsIgnoreCase("modify-rarity")) {

                // Check if the sender has permission to modify id's
                if (hasPermission(sender, command, "modify")) {

                    Title.Rarity rarity = Title.Rarity.valueOf(args[1]);
                    title.setRarity(rarity);

                    sender.sendMessage(message(command, "modify.rarity", "%t", title.formatted(color), "%r", rarity.toString()));

                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "modify titles"));
                }
            }

            // modify-id <id> <id>
            else if (args[0].equalsIgnoreCase("modify-id")) {

                // Check if the sender has permission to modify id's
                if (hasPermission(sender, command, "modify")) {

                    String newId = args[1];
                    title.setId(newId);

                    sender.sendMessage(message(command, "modify.id", "%t", title.formatted(color), "%i", newId));

                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "modify titles"));
                }
            }

            // set <player> <id>
            else if (args[0].equalsIgnoreCase("set")) {

                // Check if the sender has permission to give players titles
                if (hasPermission(sender, command, "set.player")) {

                    // Give the player the title if they don't have it
                    if (hasPermission(sender, command, "give.player")) {

                        Titles.give(player, id);

                        sender.sendMessage(message(command, "give", "%p", player.getName(), "%t", title.formatted(color)));
                    }

                    Titles.set(player, id);

                    sender.sendMessage(message(command, "set.player", "%p", player.getName(), "%t", title.formatted(color)));

                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "set a title for other players"));
                }
            }

            // remove <player> <id>
            else if (args[0].equalsIgnoreCase("remove")) {

                // Check if the sender has permission to remove titles from players
                if (hasPermission(sender, command, "remove")) {

                    Titles.remove(player, id);

                    sender.sendMessage(message(command, "remove", "%p", player.getName(), "%t", title.formatted(color)));

                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "remove titles from players"));
                }
            }

            // give <id> <player>
            else if (args[0].equalsIgnoreCase("give")) {

                // Check if the sender has permission to give players titles
                if (hasPermission(sender, command, "give.player")) {

                    Titles.give(player, id);

                    sender.sendMessage(message(command, "give", "%p", player.getName(), "%t", title.formatted(color)));

                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "give a title to other players"));
                }
            } else {
                return false;
            }
        }

        /*
         create <id> <rarity> <name>
         modify-name <id> <name>
        */
        else {

            // create <id> <rarity> <name>
            if (args[0].equalsIgnoreCase("create")) {

                // Check if the sender can create titles
                if (hasPermission(sender, command, "create")) {

                    // Set id variable
                    String id = args[1];

                    // Check if the title id already exists
                    if (Titles.exists(id)) {
                        sender.sendMessage(error(command, "id-taken", "%i", id));
                        return true;
                    }

                    // Set rarity, name, and title variables
                    Title.Rarity rarity = Title.Rarity.valueOf(args[2]);
                    String name = getArg(args, 4);
                    Title title = new Title(id, name, rarity);

                    // Add the new title to titles in memory
                    Titles.add(title);
                    sender.sendMessage(message(command, "create", "%t", title.toString()));
                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "create titles"));
                }
            }

            // modify-name <id> <name>
            else if (args[0].equalsIgnoreCase("modify-name")) {

                // Check if the sender can modify titles
                if (hasPermission(sender, command, "modify")) {

                    // Set id, title, and newName variables
                    String id = args[1];
                    Title title = Titles.getTitle(id);
                    String newName = getArg(args, 3);

                    // Check if the id is valid
                    if (title == null) {
                        sender.sendMessage(error(command, "invalid-id", "%i", id));
                        return true;
                    }

                    // Set the title's name
                    title.setName(newName);
                    sender.sendMessage(message(command, "modify.name", "%t", title.toString()));
                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "modify titles"));
                }
            } else {
                return false;
            }

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> suggestions = new ArrayList<>();

        /*
         list
         remove
         show-all
         delete
         create
         give
         set
         modify-name
         modify-rarity
         modify-id
        */
        if (args.length == 1) {

            // Give these suggestions to everyone
            suggestions.add("set");
            suggestions.add("list");
            suggestions.add("show-all");

            // Check if the sender has permission to modify titles
            if (hasPermission(sender, command, "modify")) {
                suggestions.add("modify-name");
                suggestions.add("modify-rarity");
                suggestions.add("modify-id");
            }

            // Check if the sender has permission to give titles to players
            if (hasPermission(sender, command, "give")) {
                suggestions.add("give");
            }

            // Check if the sender has permission to remove titles from players
            if (hasPermission(sender, command, "remove")) {
                suggestions.add("remove");
            }

            // Check if the sender has permission to create and delete titles
            if (hasPermission(sender, command, "create")) {
                suggestions.add("delete");
                suggestions.add("create");
            }
        }

        /*
         list <player>
         delete <id>
         modify-rarity <id> <rarity>
         modify-id <id> <id>
         modify-name <id> <name>
        */
        else if (args.length == 2) {

            // list <player>
            if (args[0].equalsIgnoreCase("list")) {
                suggestions = getOfflinePlayerNames();
            }

            /*
             delete <id>
             modify-rarity <id> <rarity>
             modify-id <id> <id>
             modify-name <id> <name>
            */
            else if (args[0].toLowerCase().matches("delete|modify-rarity|modify-name|modify-id")) {
                suggestions = Titles.listIds();
            }

            // give <player> <id>
            else if (args[0].equalsIgnoreCase("give")) {
                if (hasPermission(sender, command, "give.player")) {
                    suggestions.addAll(getOfflinePlayerNames());
                }
            }

            /*
             set <player> <id>
            */
            else if (args[0].toLowerCase().matches("set")) {
                if (hasPermission(sender, command, "set.player")) {
                    suggestions.addAll(getOfflinePlayerNames());
                }
                if (sender instanceof Player) {
                    if (hasPermission(sender, command, "unlocked-all")) {
                        suggestions.addAll(Titles.listIds());
                    } else {
                        suggestions.addAll(Titles.getTitleIds((Player) sender));
                    }
                }
            }
        }

        /*
         modify-rarity <id> <rarity>
         modify-id <id> <id>
         set <player> <id>
         give <player> <id>
        */
        else if (args.length == 3) {

            // modify-rarity <id> <rarity>
            if (args[0].equalsIgnoreCase("modify-rarity")) {
                if (Titles.exists(args[1])) {
                    for (Title.Rarity rarity : Title.Rarity.values()) {
                        suggestions.add(rarity.name());
                    }
                }
            }

            // modify-id <id> <id>
            else if (args[0].equalsIgnoreCase("modify-id")) {
                if (Titles.exists(args[1])) {
                    suggestions = Titles.listIds();
                }
            }

            // modify-rarity <id> <rarity>
            else if (args[0].equalsIgnoreCase("create")) {
                if (!Titles.exists(args[1])) {
                    for (Title.Rarity rarity : Title.Rarity.values()) {
                        suggestions.add(rarity.name());
                    }
                }
            }

            // give <player> <id>
            else if (args[0].equalsIgnoreCase("give")) {
                if (hasPermission(sender, command, "give.player")) {
                    suggestions = Titles.listIds();
                }
            }

            // set <player> <id>
            else if (args[0].toLowerCase().matches("set|remove")) {
                if (hasPermission(sender, command, "set.player")) {
                    OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
                    if (isValid(player)) {
                        if (hasPermission(sender, command, "unlocked-all")) {
                            suggestions.addAll(Titles.listIds());
                        } else {
                            suggestions = Titles.getTitleIds(player);
                        }
                    }
                }
            }
        }

        return getSuggestions(suggestions, args);
    }
}
