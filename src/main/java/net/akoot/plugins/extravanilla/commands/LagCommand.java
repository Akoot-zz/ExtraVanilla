package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.ultravanilla.UltraPlugin;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class LagCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public LagCommand(UltraPlugin instance) {
        super(instance, ChatColor.GOLD);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.command = command;

        if (args.length == 0) {

            double[] tps = Bukkit.getTPS();
            String tps1m = String.format("%.1f", tps[0]);
            String tps5m = String.format("%.1f", tps[1]);
            String tps15m = String.format("%.1f", tps[2]);
            sender.sendMessage(message("tps", "%t1m", tps1m, "%t5m", tps5m, "%t15m", tps15m));

//            TODO: figure out ping
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;
        return null;
    }
}
