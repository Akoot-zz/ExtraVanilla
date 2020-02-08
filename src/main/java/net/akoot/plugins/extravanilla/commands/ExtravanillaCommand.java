package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.ultravanilla.UltraPlugin;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class ExtravanillaCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public ExtravanillaCommand(UltraPlugin instance) {
        super(instance, ChatColor.DARK_AQUA);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.command = command;
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;
        return new ArrayList<>();
    }
}
