package net.akoot.plugins.extravanilla.util;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.reference.UltraPaths;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class ExtraUtil {

    /**
     * Get the total time a player has played in milliseconds
     *
     * @param player The player
     * @return The number of milliseconds a player has played
     */
    public static long getPlaytimeMillis(OfflinePlayer player) {
        YamlConfiguration config = Users.getUser(player);
        return config.getLong(UltraPaths.User.PLAYTIME, 0L);
    }

    /**
     * Get the total time a player has played in seconds
     *
     * @param player The player
     * @return The number of seconds a player has played
     */
    public static long getPlaytimeSeconds(OfflinePlayer player) {
        return getPlaytimeMillis(player) / 1000L;
    }

    /**
     * Get the total time a player has played in in-game ticks
     *
     * @param player The player
     * @return The number of ticks a player has played
     */
    public static long getPlaytimeTicks(OfflinePlayer player) {
        return getPlaytimeSeconds(player) * 20L;
    }

    /**
     * Get the total time a player has played in minutes
     *
     * @param player The player
     * @return The number of minutes a player has played
     */
    public static int getPlaytimeMinutes(OfflinePlayer player) {
        return (int) (getPlaytimeSeconds(player) / 60L);
    }

    /**
     * Get the total time a player has played in hours
     *
     * @param player The player
     * @return The number of hours a player has played
     */
    public static double getPlaytimeHours(OfflinePlayer player) {
        return getPlaytimeMinutes(player) / 60.0;
    }

    /**
     * Get the time in format dhms (days hours minutes seconds) Example: 1d2h
     * @param milliseconds The time in milliseconds
     * @return The time in format dmhs
     */
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

    /**
     * Get a player's nickname
     *
     * @param player The player
     * @return The player's nickname, null if they don't have one
     */
    public static String getNickname(OfflinePlayer player) {
        return Users.getUser(player).getString(ExtraPaths.User.NICKNAME);
    }

    /**
     * Check if a player has a nickname
     *
     * @param player The player
     * @return Whether or not a player has a nickname
     */
    public static boolean hasNickname(OfflinePlayer player) {
        return getNickname(player) != null;
    }

    /**
     * Get a player from a username or alias or nickname
     * @param search The search name
     * @return A player if found, null if not
     */
    public static Player getPlayer(String search) {

        // Get the player using the Server
        Player player = Bukkit.getServer().getPlayer(search);

        // If the search wasn't a username which was online, try matching it to the player's alias
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

    /**
     * Get the aliases of a player (not including username)
     *
     * @param player The player
     * @return A list of aliases which belong to a player
     */
    public static List<String> getAliases(OfflinePlayer player) {

        // Get the "alias" and "nickname" from a player
        List<String> aliases = Users.getUser(player).getStringList(ExtraPaths.User.ALIASES);
        String nick = Users.getUser(player).getString(ExtraPaths.User.NICKNAME);

        // Add the nickname to the alias list if the player has one
        if (nick != null) aliases.add(ChatColor.stripColor(nick));

        // Add the username
        aliases.add(player.getName());

        // Return the list
        return aliases;
    }
}
