package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.ExtraVanilla;
import net.akoot.plugins.ultravanilla.UltraPlugin;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtravanillaCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public ExtravanillaCommand(UltraPlugin instance) {
        super(instance, ChatColor.DARK_AQUA);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Sub-commands: reload
        if (args.length == 1) {

            // reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (hasPermission(sender, "reload")) {
                    plugin.reloadConfig();
                    strings.reload();
                    ExtraVanilla.getTitles().reload();
                    sender.sendMessage(message("reload"));
                } else {
                    sender.sendMessage(uvStrings.getString("error.no-permission", "%a", "reload the configs"));
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
        if (args.length == 1) {
            return Collections.singletonList("reload");
        }
        return new ArrayList<>();
    }
}
