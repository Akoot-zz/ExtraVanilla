package net.akoot.plugins.extravanilla;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.extravanilla.serializable.Title;
import net.akoot.plugins.ultravanilla.Users;
import net.akoot.plugins.ultravanilla.reference.Palette;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class EventListener implements Listener {

    private ExtraVanilla plugin;

    public EventListener(ExtraVanilla instance) {
        plugin = instance;
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        YamlConfiguration config = Users.getUser(player);

        String message = event.getMessage();

        message = Palette.translate(message);

        event.setMessage(message);

        String nickname = config.getString(ExtraPaths.User.NICKNAME, player.getName()) + ChatColor.RESET;
        ChatColor nameColor = ChatColor.valueOf(config.getString(ExtraPaths.User.NAME_COLOR, "WHITE"));
        ChatColor chatColor = ChatColor.valueOf(config.getString(ExtraPaths.User.CHAT_COLOR, "WHITE"));
        Title title = Titles.getTitleOrDefault(config.getString(ExtraPaths.User.SELECTED_TITLE));

        String format = plugin.getConfig().getString(ExtraPaths.CHAT_FORMAT, event.getFormat())
                .replaceAll("%t", title + "")
                .replaceAll("&\\{name-color}", nameColor + "")
                .replaceAll("%p", "%1\\$s")
                .replaceAll("&\\{chat-color}", chatColor + "")
                .replaceAll("%m", "%2\\$s");

        format = Palette.translate(format);

        event.setFormat(format);
    }
}

