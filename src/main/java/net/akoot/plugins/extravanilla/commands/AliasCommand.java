package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.Strings;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class AliasCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public AliasCommand(JavaPlugin instance, Strings strings) {
        super(instance, strings, ChatColor.YELLOW);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        this.command = command;

        // no args
        if (args.length == 0) {
            if (sender instanceof Player) {
                String alias = Users.getUser((Player) sender).getString(ExtraPaths.User.ALIAS);
                if (alias != null) {
                    sender.sendMessage(message("query.self", "%a", alias));
                } else {
                    sender.sendMessage(error("no-alias.self"));
                }
            } else {
                sender.sendMessage(uvStrings.getString("error.player-only", "%a", "set your alias"));
            }
        }

        // <player>
        else if (args.length == 1) {

            if (args[0].equalsIgnoreCase("unset")) {
                if (sender instanceof Player) {
                    Users.set((Player) sender, ExtraPaths.User.ALIAS, null);
                    sender.sendMessage(message("unset.self"));
                } else {
                    sender.sendMessage(uvStrings.getString("error.player-only", "%a", "set your alias"));
                }
                return true;
            }

            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            if (isValid(player)) {
                String alias = Users.getUser((Player) sender).getString(ExtraPaths.User.ALIAS);
                if (alias != null) {
                    sender.sendMessage(message("query.other", "%p", player.getName(), "%a", alias));
                } else {
                    sender.sendMessage(error("no-alias.other", "%p", player.getName()));
                }
            } else {
                sender.sendMessage(uvStrings.getString("error.player-unknown", "%p", args[0]));
            }
        }

        /*
         set <alias>
         unset <player>
         */
        else if (args.length == 2) {

            // set <alias>
            if (args[0].equalsIgnoreCase("set")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String alias = args[1];
                    Users.set(player, ExtraPaths.User.ALIAS, alias);
                    sender.sendMessage(message("set.self", "%a", alias));
                } else {
                    sender.sendMessage(uvStrings.getString("error.player-only", "%a", "set your alias"));
                }
            }

            // unset <player>
            else if (args[0].equalsIgnoreCase("unset")) {
                if (hasPermission(sender, "modify")) {
                    OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
                    if (isValid(player)) {
                        Users.set(player, ExtraPaths.User.ALIAS, null);
                        sender.sendMessage(message("unset.other" +
                                "", "%p", player.getName()));
                    } else {
                        sender.sendMessage(uvStrings.getString("error.player-unknown", "%p", args[1]));
                    }
                } else {
                    sender.sendMessage(uvStrings.getString("no-permission", "%a", "modify aliases for others"));
                }
            }
        }

        // set <player> <alias>
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                if (hasPermission(sender, "modify")) {
                    OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);
                    if (isValid(player)) {
                        String alias = args[2];
                        Users.set(player, ExtraPaths.User.ALIAS, alias);
                        sender.sendMessage(message("set.other", "%p", player.getName(), "%a", alias));
                    } else {
                        sender.sendMessage(uvStrings.getString("error.player-unknown", "%p", args[1]));
                    }
                } else {
                    sender.sendMessage(uvStrings.getString("no-permission", "%a", "modify aliases for others"));
                }
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;
        return null;
    }
}
