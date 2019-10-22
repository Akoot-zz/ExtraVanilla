package net.akoot.plugins.extravanilla.serializable;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Channel implements ConfigurationSerializable {

    private UUID id;
    private String title;
    private List<UUID> admins;
    private UUID owner;
    private List<UUID> members;
    private ChatColor color;

    public static Channel deserialize(Map<String, Object> args) {
        Channel channel = new Channel();
        channel.id = (UUID) args.get("id");
        channel.color = ChatColor.valueOf(args.get("color").toString());
        channel.members = (List<UUID>) args.get("members");
        channel.owner = (UUID) args.get("owner");
        channel.title = args.get("title").toString();
        channel.admins = (List<UUID>) args.get("admins");
        return channel;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<UUID> getAdmins() {
        return admins;
    }

    public void setAdmins(List<UUID> admins) {
        this.admins = admins;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("title", title);
        result.put("admins", admins);
        result.put("owner", owner);
        result.put("members", members);
        result.put("color", color);

        return result;
    }
}
