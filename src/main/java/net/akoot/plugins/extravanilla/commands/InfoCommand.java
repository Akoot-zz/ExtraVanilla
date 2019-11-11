package net.akoot.plugins.extravanilla.commands;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.extravanilla.util.ExtraUtil;
import net.akoot.plugins.ultravanilla.Strings;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.commands.UltraCommand;
import net.akoot.plugins.ultravanilla.reference.Palette;
import net.akoot.plugins.ultravanilla.reference.UltraPaths;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public InfoCommand(JavaPlugin instance, Strings strings) {
        super(instance, strings, ChatColor.WHITE);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        this.command = command;

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Map<String, String> info = getInfo(player);
                for (String key : info.keySet()) {
                    player.sendMessage(key + ": " + info.get(key));
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("search")) {
                String search = args[1];
                List<String> results = new ArrayList<>();
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    String username = player.getName();
                    String displayName = ChatColor.stripColor(player.getDisplayName());
                    String alias = Users.getUser(player).getString(ExtraPaths.User.ALIAS);
                    List<String> pastNames = Users.getUser(player).getStringList(UltraPaths.User.PAST_NAMES);
                    if (username.contains(search)) {
                        results.add(username.replace(search, Palette.TRUE + search + color));
                    } else if (displayName.contains(search)) {
                        results.add(displayName.replace(search, Palette.TRUE + search + color) + " (" + username + ")");
                    } else if (alias != null && alias.contains(search)) {
                        results.add(alias.replace(search, Palette.TRUE + search + color) + " (" + username + ")");
                    } else {
                        for (String name : pastNames) {
                            if (name.contains(search)) {
                                results.add(name.replace(search, Palette.TRUE + search + color) + " (" + username + ")");
                                break;
                            }
                        }
                    }
                }
                sender.sendMessage(color + "Found: " + String.join(", ", results));
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.command = command;
        return null;
    }

    private Map<String, String> getInfo(Player player) {

        Map<String, String> info = new HashMap<>();
        YamlConfiguration user = Users.getUser(player);

        // Username
        info.put("Username", player.getName());

        // Nickname
        String nickname = user.getString(ExtraPaths.User.NICKNAME);
        if (nickname != null)
            info.put("Nickname", nickname);

        // Aliases
        List<String> aliases = ExtraUtil.getAliases(player);
        if (!aliases.isEmpty())
            info.put("Also known as", String.join(", ", aliases));

        // Playtime
        info.put("Playtime", ExtraUtil.getTimeString(ExtraUtil.getPlaytimeMillis(player)));
        return info;
    }
}