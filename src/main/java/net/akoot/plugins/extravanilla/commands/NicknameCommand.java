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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class NicknameCommand extends UltraCommand implements CommandExecutor, TabExecutor, Listener {

    public NicknameCommand(UltraPlugin instance) {
        super(instance, ChatColor.DARK_GREEN);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        this.command = command;

        String name = command.getName();
        switch (name) {
            case "nickname":
                return handleNick(sender, args);
            case "namecolor":
                return handleColor(sender, args, ExtraPaths.User.NAME_COLOR);
            case "chatcolor":
                return handleColor(sender, args, ExtraPaths.User.CHAT_COLOR);
        }
        return false;
    }

    private boolean handleColor(CommandSender sender, String[] args, String key) {

        if (args.length == 0) {
            if (sender instanceof Player) {
                Users.getUser((Player) sender).set(key, null);
                sender.sendMessage(message("reset"));
            } else {
                sender.sendMessage(playerOnly(message("reset").toLowerCase()));
            }
        } else if (args.length == 1) {
            if (hasPermission(sender, "set.self")) {
                if (sender instanceof Player) {
                    ChatColor chatColor = ChatColor.valueOf(args[0].toUpperCase());
                    Player player = (Player) sender;
                    Users.getUser(player).set(key, chatColor.name());
                    if (key.equals(ExtraPaths.User.NAME_COLOR)) {
                        updateNick(player, chatColor + player.getDisplayName());
                    }
                    sender.sendMessage(message("set.self", "%c", chatColor + chatColor.name()));
                } else {
                    sender.sendMessage(playerOnly(message("set.self", "%c", "anything")));
                }
            } else {
                sender.sendMessage(noPermission(message("set.self", "%c", "anything")));
            }
        } else if (args.length == 2) {
            if (hasPermission(sender, "set.other")) {
                if (sender instanceof Player) {
                    OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
                    if (isValid(player)) {
                        ChatColor chatColor = ChatColor.valueOf(args[1].toUpperCase());
                        Users.getUser(player).set(key, chatColor.name());
                        if (key.equals(ExtraPaths.User.NAME_COLOR)) {
                            if (player.isOnline()) {
                                updateNick(player, chatColor + ((Player) player).getDisplayName());
                            }
                        }
                        sender.sendMessage(message("set.other", "%p", player.getName(), "%c", chatColor + chatColor.name()));
                    } else {
                        sender.sendMessage(playerInvalid(args[0]));
                    }
                } else {
                    sender.sendMessage(playerOnly(message("set.self", "%c", "anything")));
                }
            } else {
                sender.sendMessage(noPermission(message("set.self", "%c", "anything")));
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean handleNick(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Users.getUser((Player) sender).set(ExtraPaths.User.NICKNAME, null);
                sender.sendMessage(message("clear"));
            } else {
                sender.sendMessage(playerOnly("reset your nickname"));
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
        updateNick(player, nickname);
    }

    private void updateNick(OfflinePlayer player, String nickname) {
        if (player.isOnline()) {
            Player onlinePlayer = (Player) player;
            nickname = Palette.translate(nickname);
            onlinePlayer.setDisplayName(nickname);
            onlinePlayer.setPlayerListName(nickname);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;

        List<String> suggestions = new ArrayList<>();

        if (command.getName().equals("nickname")) {
            if (args.length == 2) {
                return suggestions;
            } else {
                return null;
            }
        } else {

            if (args.length >= 3) {
                return suggestions;
            }

            for (ChatColor c : ChatColor.values()) {
                suggestions.add(c.name());
            }
            if (args.length == 1) {
                for (OfflinePlayer player : plugin.getServer().getOfflinePlayers()) {
                    suggestions.add(player.getName());
                }
            }
        }
        return getSuggestions(suggestions, args);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nickname = Users.getUser(player).getString(ExtraPaths.User.NICKNAME, "");
        ChatColor nameColor = ChatColor.valueOf(Users.getUser(player).getString(ExtraPaths.User.NAME_COLOR, "RESET"));
        if (nickname != null && !nickname.isEmpty()) {
            updateNick(player, nameColor + nickname);
        }
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatColor chatColor = ChatColor.valueOf(Users.getUser(player).getString(ExtraPaths.User.CHAT_COLOR, "RESET"));
        event.setMessage(chatColor + event.getMessage());
    }
}
