package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.UltraPlugin;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import net.akoot.plugins.ultravanilla.reference.Palette;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class NicknameCommand extends UltraCommand implements CommandExecutor, TabExecutor, Listener {

    public NicknameCommand(UltraPlugin instance) {
        super(instance, ChatColor.DARK_GREEN);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.command = command;

        if (args.length == 0) {
            if (sender instanceof Player) {

            } else {
                sender.sendMessage(playerOnly("display your nickname"));
            }
        } else if (args.length == 1) {
            if (hasPermission(sender, "set")) {
                if (sender instanceof Player) {

                    Player player = (Player) sender;

                    String nickname = args[0];

                    nick(player, nickname);

                    sender.sendMessage(message("set.self", "%n", nickname));
                } else {
                    sender.sendMessage(playerOnly("set your nickname"));
                }
            } else {
                sender.sendMessage(noPermission("set your nickname"));
            }
        } else if (args.length == 2) {
            if (hasPermission(sender, "set.others")) {

                OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
                String nickname = args[1];

                if (isValid(player)) {
                    nick(player, nickname);
                } else {
                    sender.sendMessage(playerInvalid(args[0]));
                }

                sender.sendMessage(message("set.other", "%n", nickname));

            } else {
                sender.sendMessage(noPermission("set other players nicknames"));
            }
        } else {
            return false;
        }
        return true;
    }

    private void nick(OfflinePlayer player, String nickname) {

        Users.getUser(player).set(ExtraPaths.User.NICKNAME, nickname);
        Users.saveUser(player);

        if (player.isOnline()) {
            updateNick((Player) player, nickname);
        }
    }

    private void updateNick(Player player, String nickname) {
        nickname = Palette.translate(nickname);
        player.setDisplayName(nickname);
        player.setPlayerListName(nickname);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;
        return null;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nickname = Users.getUser(player).getString(ExtraPaths.User.NICKNAME, "");
        if (!nickname.isEmpty()) {
            updateNick(player, nickname);
        }
    }
}
