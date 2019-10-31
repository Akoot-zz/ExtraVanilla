package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.ultravanilla.Strings;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ChannelCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public ChannelCommand(JavaPlugin instance, Strings strings) {
        super(instance, strings, ChatColor.GREEN);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        this.command = command;

        sender.sendMessage(message("test", "%p", sender.getName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;
        return null;
    }
}