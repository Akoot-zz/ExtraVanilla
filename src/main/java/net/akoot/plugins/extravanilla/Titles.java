package net.akoot.plugins.extravanilla;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.extravanilla.serializable.Title;
import net.akoot.plugins.ultravanilla.Users;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class Titles {

    private static List<Title> titles = new ArrayList<>();

    public static void setTitles(List<Title> titles) {
        Titles.titles = titles;
    }

    /**
     * Get a default title (instead of getting null)
     *
     * @return A default title
     */
    public static Title getDefaultTitle() {
        return new Title(
                ExtraVanilla.getInstance().getConfig().getString(ExtraPaths.Titles.DEFAULT_ID),
                ExtraVanilla.getInstance().getConfig().getString(ExtraPaths.Titles.DEFAULT_NAME),
                Title.Rarity.COMMON);
    }

    /**
     * Add a title to the list
     *
     * @param title
     */
    public static void add(Title title) {
        titles.add(title);
        save();
    }

    /**
     * Remove a title from the list, also remove it from all players
     *
     * @param title
     */
    public static void delete(Title title) {
        titles.remove(title);
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            remove(player, title.getId());
        }
        save();
    }

    /**
     * Save the titles to titles.yml
     */
    public static void save() {
        ExtraVanilla.getInstance().getTitles().getConfig().set(ExtraPaths.Titles.ROOT, titles);
        ExtraVanilla.getInstance().getTitles().saveConfig();
    }

    /**
     * Get the title from memory if it exists, else return null
     *
     * @param id The ID of the title
     * @return The title object if it exists in memory, null if it doesn't exist
     */
    public static Title getTitle(String id) {
        if (id != null) {
            for (Title title : titles) {
                if (title.getId().equals(id)) {
                    return title;
                }
            }
        }
        return null;
    }

    /**
     * Get a list of IDs from all of the titles in memory
     * @return
     */
    public static List<String> listIds() {
        List<String> list = new ArrayList<>();
        for (Title title : titles) {
            list.add(title.getId());
        }
        return list;
    }

    /**
     * Get the unlocked titles of a player formatted
     * @param player The player
     * @param color The default color
     * @return The list of unlocked titles of a player: formatted
     */
    public static List<String> getUnlockedTitlesFormatted(OfflinePlayer player, ChatColor color) {
        return getTitlesFormatted(getTitles(player), color);
    }

    /**
     * Get the list of titles but as a formatted string: title.formatted(color)
     * @param titles A list of titles to add to the string list
     * @param color The default color
     * @return A list of titles formatted
     */
    public static List<String> getTitlesFormatted(List<Title> titles, ChatColor color) {
        List<String> list = new ArrayList<>();
        for (Title title : titles) {
            list.add(title.formatted(color));
        }
        return list;
    }

    /**
     * Get the list of all the titles but formatted
     * @param color The default color
     * @return The list of all the titles formatted
     */
    public static List<String> getTitlesFormatted(ChatColor color) {
        return getTitlesFormatted(titles, color);
    }

    /**
     * Give a title to a player (unlocks it for them)
     * @param player The player to give the title to
     * @param id The ID of the title to be given to the player
     */
    public static void give(OfflinePlayer player, String id) {
        if (!hasTitle(player, id)) {
            List<String> unlockedTitles = getTitleIds(player);
            unlockedTitles.add(id);
            Users.getUser(player).set(ExtraPaths.User.UNLOCKED_TITLES, unlockedTitles);
            Users.saveUser(player);
        }
    }

    /**
     * Set the display title of a player
     * @param player The player to set the display title for
     * @param id The ID of the title to be the display title for the player
     */
    public static void set(OfflinePlayer player, String id) {
        Users.getUser(player).set(ExtraPaths.User.SELECTED_TITLE, id);
        Users.saveUser(player);
    }

    /**
     * Remove a title from a player
     * @param player
     * @param id
     */
    public static void remove(OfflinePlayer player, String id) {
        if (hasTitle(player, id)) {
            List<String> unlockedTitles = getTitleIds(player);

            unlockedTitles.remove(id);
            Users.getUser(player).set(ExtraPaths.User.UNLOCKED_TITLES, unlockedTitles);

            if (getDisplayTitleId(player).equals(id)) {
                set(player, getHighestRankingTitle(player).getId());
            }

            Users.saveUser(player);
        }
    }

    /**
     * Get the highest ranking title a player owns
     * @param player The player
     * @return The highest ranking title a player owns
     */
    public static Title getHighestRankingTitle(OfflinePlayer player) {
        List<Title> unlockedTitles = getTitles(player);
        Title title = getDefaultTitle();
        for (Title unlockedTitle : unlockedTitles) {
            if (unlockedTitle.getRanking() > title.getRanking()) {
                title = unlockedTitle;
            }
        }
        return title;
    }

    /**
     * Get the display title of a player
     *
     * @param player The player
     * @return The display title of a player
     */
    public static Title getDisplayTitle(OfflinePlayer player) {
        return getTitle(getDisplayTitleId(player));
    }

    /**
     * Get the ID of the display title of a player
     *
     * @param player The player
     * @return The ID of the display title of a player
     */
    public static String getDisplayTitleId(OfflinePlayer player) {
        return Users.getUser(player).getString(ExtraPaths.User.SELECTED_TITLE);
    }

    /**
     *
     * @param player
     * @return
     */
    public static List<Title> getTitles(OfflinePlayer player) {
        List<Title> unlockedTitles = new ArrayList<>();
        for (String id : getTitleIds(player)) {
            Title title = getTitle(id);
            if (title != null) {
                unlockedTitles.add(title);
            }
        }
        return unlockedTitles;
    }

    /**
     * Get all of the player's unlocked title IDs
     * @param player The player
     * @return The IDs of all the titles a player has unlocked
     */
    public static List<String> getTitleIds(OfflinePlayer player) {
        return Users.getUser(player).getStringList(ExtraPaths.User.UNLOCKED_TITLES);
    }

    /**
     * Get the names of all the unlocked titles from a player
     * @param player The player
     * @return
     */
    public static List<String> getUnlockedTitleNames(OfflinePlayer player) {
        List<String> list = new ArrayList<>();
        for (Title title : getTitles(player)) {
            list.add(title.toString());
        }
        return list;
    }

    /**
     * Check if a title ID is valid
     * @param id The title ID
     * @return Wether or not the title ID is valid
     */
    public static boolean exists(String id) {
        for (Title title : titles) {
            if (title.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a player has the title unlocked
     * @param player The player
     * @param id The ID
     * @return Wether or not a player has the title unlocked
     */
    public static boolean hasTitle(OfflinePlayer player, String id) {
        for (Title title : getTitles(player)) {
            if (title.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a title from an ID, if none of the titles have that ID then return the default title
     * @param id The ID of the title
     * @return The title if it exists, if it doesn't then return the default title specified in config.yml
     */
    public static Title getTitleOrDefault(String id) {
        Title title = getTitle(id);
        return title != null ? title : getDefaultTitle();
    }
}
