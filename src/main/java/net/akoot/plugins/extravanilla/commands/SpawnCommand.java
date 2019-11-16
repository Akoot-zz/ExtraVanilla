package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.Strings;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import net.akoot.plugins.ultravanilla.serializable.Position;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class SpawnCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public SpawnCommand(JavaPlugin instance, Strings strings) {
        super(instance, strings, ChatColor.GREEN);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        this.command = plugin.getCommand("spawn");

        if (command.getName().equals("spawn")) {
            Location spawn = (Location) plugin.getConfig().get(ExtraPaths.Config.SPAWN);
            if (spawn != null) {
                if (args.length == 0) {
                    if (sender instanceof Player) {
                        ((Player) sender).teleport(spawn);
                        sender.sendMessage(message("teleport.self"));
                    } else {
                        sender.sendMessage(spawn.toString());
                    }
                } else if (args.length == 1) {
                    List<Player> players = getPlayers(args[0]);
                    String playerList = playerList(players);
                    for (Player player : players) {
                        player.teleport(spawn);
                    }
                    sender.sendMessage(message("teleport.player", "%p", playerList));
                } else {
                    return false;
                }
            } else {
                sender.sendMessage(error("spawn-not-set"));
            }
        } else if (command.getName().equals("setspawn")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (hasPermission(sender, "set")) {
                        Location spawn = player.getLocation();
                        plugin.getConfig().set(ExtraPaths.Config.SPAWN, spawn);
                        plugin.saveConfig();
                        sender.sendMessage(message("set", "%l", Position.toStringTrimmed(spawn)));
                    } else {
                        sender.sendMessage(noPermission("set the spawn"));
                    }
                } else {
                    sender.sendMessage(playerOnly("set the spawn"));
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        this.command = command;

        if (command.getName().equals("spawn")) {
            if (args.length >= 2) {
                return Collections.emptyList();
            }
        } else if (command.getName().equals("setspawn")) {
            return Collections.emptyList();
        }
        return null;
    }
}
