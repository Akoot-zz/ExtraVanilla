package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.UltraPlugin;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class AliasCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public AliasCommand(UltraPlugin instance) {
        super(instance, ChatColor.YELLOW);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        this.command = command;

        // no args
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                List<String> aliases = Users.getUser(player).getStringList(ExtraPaths.User.ALIASES);
                sender.sendMessage(list("query.self", aliases));
            } else {
                sender.sendMessage(playerOnly("set your alias"));
            }
        }

        // <player>
        else if (args.length == 1) {

            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            if (isValid(player)) {
                List<String> aliases = Users.getUser(player).getStringList(ExtraPaths.User.ALIASES);
                sender.sendMessage(list("query.other", aliases, "%p", player.getName()));
            } else {
                sender.sendMessage(playerInvalid(args[0]));
            }

        }

        /*
        add <alias>
        remove <alias>
         */
        else if (args.length == 2) {

            if (sender instanceof Player) {
                if (hasPermission(sender, "modify.self")) {
                    return modifyAlias(sender, (Player) sender, args[1], args[0]);
                } else {
                    sender.sendMessage(noPermission("modify your aliases"));
                }
            } else {
                sender.sendMessage(playerOnly("modify your aliases"));
            }
        }

        /*
        add <player> <alias>
        remove <player> <alias>
         */
        else if (args.length == 3) {

            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[1]);

            if (isValid(player)) {
                if (hasPermission(sender, "modify.other")) {
                    return modifyAlias(sender, player, args[2], args[0]);
                } else {
                    sender.sendMessage(noPermission("modify someone else's aliases"));
                }
            } else {
                sender.sendMessage(playerInvalid(args[1]));
            }
        }

        return true;
    }

    private boolean modifyAlias(CommandSender sender, OfflinePlayer player, String alias, String subCommand) {
        YamlConfiguration config = Users.getUser(player);
        List<String> aliases = config.getStringList(ExtraPaths.User.ALIASES);
        if (subCommand.equalsIgnoreCase("add")) {
            if (!aliases.contains(alias)) {
                aliases.add(alias);
                sender.sendMessage(message("modify.add", "%p", player.getName(), "%a", alias));
            } else {
                sender.sendMessage(error("alias-exists", "%p", player.getName(), "%a", alias));
                return true;
            }
        } else if (subCommand.equalsIgnoreCase("remove")) {
            if (aliases.contains(alias)) {
                aliases.remove(alias);
                sender.sendMessage(message("modify.remove", "%p", player.getName(), "%a", alias));
            } else {
                sender.sendMessage(error("alias-unknown", "%p", player.getName(), "%a", alias));
                return true;
            }
        } else {
            return false;
        }
        config.set(ExtraPaths.User.ALIASES, aliases);
        Users.saveUser(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;
        return null;
    }
}
