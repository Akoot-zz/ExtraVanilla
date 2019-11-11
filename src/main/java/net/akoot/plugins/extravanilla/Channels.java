package net.akoot.plugins.extravanilla;

import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.extravanilla.serializable.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channels {

    public enum SenderRole {
        OWNER, ADMIN, MEMBER
    }

    private static List<Channel> channels;

    public static void add(Channel channel) {
        channels.add(channel);
        save();
    }

    public static Channel get(UUID id) {
        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                channels.remove(channel);
                return channel;
            }
        }
        return null;
    }

    public static void remove(UUID id) {
        Channel channel = get(id);
        if (channel != null) {
            channels.remove(channel);
        }
        save();
    }

    public static List<Channel> getChannels() {
        return channels;
    }

    public static void save() {
        ExtraVanilla.getInstance().getConfig().set(ExtraPaths.Channels.ROOT, channels);
        ExtraVanilla.getInstance().saveConfig();
    }

    public static void reload(ExtraVanilla instance) {
        channels = (List<Channel>) instance.getConfig().getList(ExtraPaths.Channels.ROOT);
        channels = channels != null ? channels : new ArrayList<>();
    }
}
