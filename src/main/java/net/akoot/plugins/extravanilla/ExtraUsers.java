package net.akoot.plugins.extravanilla;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.ultravanilla.Users;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ExtraUsers {

    public static List<String> getAliases(Player player) {
        List<String> aliases = new ArrayList<>();
        String alias = Users.getUser(player).getString(ExtraPaths.User.ALIAS);
        String nick = Users.getUser(player).getString(ExtraPaths.User.NICKNAME);
        if (nick != null) aliases.add(ChatColor.stripColor(nick));
        if (alias != null) aliases.add(alias);
        return aliases;
    }
}
