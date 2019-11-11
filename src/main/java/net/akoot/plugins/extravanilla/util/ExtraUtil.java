package net.akoot.plugins.extravanilla.util;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.reference.UltraPaths;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ExtraUtil {

    public static long getPlaytimeMillis(Player player) {
        YamlConfiguration config = Users.getUser(player);
        long difference = System.currentTimeMillis() - config.getLong(UltraPaths.User.LAST_LEAVE, System.currentTimeMillis());
        return config.getLong(UltraPaths.User.PLAYTIME, 0L) + difference;
    }

    public static long getPlaytimeSeconds(Player player) {
        return getPlaytimeMillis(player) / 1000L;
    }

    public static long getPlaytimeTicks(Player player) {
        return getPlaytimeSeconds(player) * 20L;
    }

    public static int getPlaytimeMinutes(Player player) {
        return (int) (getPlaytimeSeconds(player) / 60L);
    }

    public static double getPlaytimeHours(Player player) {
        return getPlaytimeMinutes(player) / 60.0;
    }

    public static String getTimeString(long milliseconds) {
        long seconds = milliseconds / 1000L;
        if (seconds > 60) {
            int minutes = (int) (seconds / 60);
            if (minutes > 60) {
                int hours = minutes / 60;
                if (hours > 24) {
                    int days = hours / 24;
                    hours = hours % 24;
                    return String.format("%dd%dh", days, hours);
                }
                minutes = minutes % 60;
                return String.format("%dh%dm", hours, minutes);
            }
            seconds = seconds % 60;
            return String.format("%dm%ds", minutes, seconds);
        }
        return seconds + "s";
    }

    public static String getNickname(Player player) {
        return Users.getUser(player).getString(ExtraPaths.User.NICKNAME);
    }

    public static boolean hasNickname(Player player) {
        return getNickname(player) != null;
    }

    public static Player getPlayer(String search) {

        Player player = Bukkit.getServer().getPlayer(search);

        if (player == null) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                for (String alias : getAliases(p)) {
                    if (alias.startsWith(search)) {
                        return p;
                    }
                }
            }
        }

        return player;
    }

    public static List<String> getAliases(Player player) {
        List<String> aliases = new ArrayList<>();
        String alias = Users.getUser(player).getString(ExtraPaths.User.ALIAS);
        String nick = Users.getUser(player).getString(ExtraPaths.User.NICKNAME);
        if (nick != null) aliases.add(ChatColor.stripColor(nick));
        if (alias != null) aliases.add(alias);
        return aliases;
    }
}
