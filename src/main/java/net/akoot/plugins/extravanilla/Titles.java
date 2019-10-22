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

    public static void save() {
        ExtraVanilla.getInstance().getTitles().getConfig().set(ExtraPaths.Titles.ROOT, titles);
        ExtraVanilla.getInstance().getTitles().saveConfig();
    }

    public static Title getTitle(String id) {
        if (id != null) {
            for (Title title : titles) {
                if (title.getId().equals(id)) {
                    return title;
                }
            }
        }
        return getDefaultTitle();
    }

    public static List<String> listIds() {
        List<String> list = new ArrayList<>();
        for (Title title : titles) {
            list.add(title.getId());
        }
        return list;
    }

    public static List<String> getUnlockedTitlesFormatted(OfflinePlayer player, ChatColor color) {
        return getTitlesFormatted(getTitles(player), color);
    }

    public static List<String> getTitlesFormatted(List<Title> titles, ChatColor color) {
        List<String> list = new ArrayList<>();
        for (Title title : titles) {
            list.add(title.formatted(color));
        }
        return list;
    }

    public static List<String> getTitlesFormatted(ChatColor color) {
        return getTitlesFormatted(titles, color);
    }

    public static void give(OfflinePlayer player, String id) {
        if (!hasTitle(player, id)) {
            List<String> unlockedTitles = getTitleIds(player);
            unlockedTitles.add(id);
            Users.getUser(player).set(ExtraPaths.User.UNLOCKED_TITLES, unlockedTitles);
            Users.saveUser(player);
        }
    }

    public static void set(OfflinePlayer player, String id) {
        Users.getUser(player).set(ExtraPaths.User.SELECTED_TITLE, id);
        Users.saveUser(player);
    }

    public static void remove(OfflinePlayer player, String id) {

        if (hasTitle(player, id)) {
            List<String> unlockedTitles = getTitleIds(player);

            unlockedTitles.remove(id);
            Users.getUser(player).set(ExtraPaths.User.UNLOCKED_TITLES, unlockedTitles);

            if (getSelectedId(player).equals(id)) {
                set(player, getHighestRankingTitle(player).getId());
            }

            Users.saveUser(player);
        }
    }

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

    public static Title getSelectedTitle(OfflinePlayer player) {
        return getTitle(getSelectedId(player));
    }

    public static String getSelectedId(OfflinePlayer player) {
        return Users.getUser(player).getString(ExtraPaths.User.SELECTED_TITLE);
    }

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

    public static List<String> getTitleIds(OfflinePlayer player) {
        return Users.getUser(player).getStringList(ExtraPaths.User.UNLOCKED_TITLES);
    }

    public static List<String> getUnlockedTitleNames(OfflinePlayer player) {
        List<String> list = new ArrayList<>();
        for (Title title : getTitles(player)) {
            list.add(title.toString());
        }
        return list;
    }

    public static boolean exists(String id) {
        for (Title title : titles) {
            if (title.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasTitle(OfflinePlayer player, String id) {
        for (Title title : getTitles(player)) {
            if (title.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
