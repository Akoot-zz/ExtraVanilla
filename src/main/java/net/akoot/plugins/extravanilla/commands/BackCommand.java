package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.UltraPlugin;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import net.akoot.plugins.ultravanilla.serializable.PositionLite;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class BackCommand extends UltraCommand implements CommandExecutor, TabExecutor, Listener {

    public BackCommand(UltraPlugin instance) {
        super(instance, ChatColor.YELLOW);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.command = command;

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                PositionLite lastTpPosition = Users.getPositionLite(player, ExtraPaths.User.LAST_TP_LOCATION);
                if (lastTpPosition != null) {
                    player.teleport(lastTpPosition.getLocation());
                    sender.sendMessage(message("teleport.player"));
                } else {
                    sender.sendMessage(error("teleport.location-null.sender"));
                }
            }
        } else if (args.length == 1) {
            List<Player> playerList = getPlayers(args[0]);
            for (Player player : playerList) {
                PositionLite lastTpPosition = Users.getPositionLite(player, ExtraPaths.User.LAST_TP_LOCATION);
                if (lastTpPosition != null) {
                    player.teleport(lastTpPosition.getLocation());
                    sender.sendMessage(message("teleport.sender", "%p", player.getName(), "%l", lastTpPosition.toString()));
                    player.sendMessage(message("teleport.player"));
                } else {
                    sender.sendMessage(error("teleport.location-null.player", "%p", player.getName()));
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

    private void updateLastTeleport(Player player) {
        updateLastTeleport(player, player.getLocation());
    }

    private void updateLastTeleport(Player player, Location location) {
        YamlConfiguration config = Users.getUser(player);
        config.set(ExtraPaths.User.LAST_TP_LOCATION, location);
        Users.saveUser(player);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        updateLastTeleport(event.getPlayer(), event.getFrom());
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        updateLastTeleport(event.getEntity());
    }
}