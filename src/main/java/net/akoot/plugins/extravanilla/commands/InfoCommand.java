package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.extravanilla.util.ExtraUtil;
import net.akoot.plugins.ultravanilla.UltraPlugin;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import net.akoot.plugins.ultravanilla.reference.Palette;
import net.akoot.plugins.ultravanilla.reference.UltraPaths;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public InfoCommand(UltraPlugin instance) {
        super(instance, ChatColor.WHITE);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        this.command = command;

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                printInfo(sender, player);
            } else {
                sender.sendMessage(playerOnly("show info about yourself"));
            }
        } else if (args.length == 1) {
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(args[0]);
            if (isValid(player)) {
                printInfo(sender, player);
            } else {
                sender.sendMessage(playerInvalid(args[0]));
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("search")) {
                String search = args[1];
                List<String> results = new ArrayList<>();
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    String username = player.getName();
                    String displayName = ChatColor.stripColor(player.getDisplayName());
                    String alias = Users.getUser(player).getString(ExtraPaths.User.ALIASES);
                    List<String> pastNames = Users.getUser(player).getStringList(UltraPaths.User.PAST_NAMES);
                    if (username.toLowerCase().contains(search.toLowerCase())) {
                        results.add(username.replace(search, Palette.TRUE + search + color));
                    } else if (displayName.toLowerCase().contains(search.toLowerCase())) {
                        results.add(displayName.replace(search, Palette.TRUE + search + color) + " (" + Palette.NOUN + username + color + ")");
                    } else if (alias != null && alias.toLowerCase().contains(search.toLowerCase())) {
                        results.add(alias.replace(search, Palette.TRUE + search + color) + " (" + username + ")");
                    } else {
                        for (String name : pastNames) {
                            if (name.toLowerCase().contains(search.toLowerCase())) {
                                results.add(name.replace(search, Palette.TRUE + search + color) + " (" + username + ")");
                                break;
                            }
                        }
                    }
                }
                sender.sendMessage(list("search", results));
            } else {
                return false;
            }
        }

        return true;
    }

    private void printInfo(CommandSender sender, OfflinePlayer player) {
        Map<String, String> info = getInfo(player);
        List<String> values = new ArrayList<>();
        for (String key : info.keySet()) {
            values.add(format("item-format", "%k", key, "%v", info.get(key)));
        }
        sender.sendMessage(list("info", values, "%p", player.getName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;
        return null;
    }

    private Map<String, String> getInfo(OfflinePlayer player) {

        // Create a hash map
        Map<String, String> info = new HashMap<>();

        // Get the player's config
        YamlConfiguration user = Users.getUser(player);

        // Username
        info.put("Usernames", user.getStringList(UltraPaths.User.PAST_NAMES).toString());

        // Nickname
        String nickname = user.getString(ExtraPaths.User.NICKNAME);
        if (nickname != null)
            info.put("Nickname", nickname);

        // Aliases
        List<String> aliases = user.getStringList(ExtraPaths.User.ALIASES);
        if (!aliases.isEmpty())
            info.put("Aliases", String.join(", ", aliases));

        // Playtime
        info.put("Playtime", ExtraUtil.getTimeString(ExtraUtil.getPlaytimeMillis(player)));
        return info;
    }
}
