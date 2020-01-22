package net.akoot.plugins.extravanilla.serializable;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class Title implements ConfigurationSerializable, Comparable {

    private String id, name;
    private Rarity rarity;

    public Title(String id, String name, Rarity rarity) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
    }

    public static Title deserialize(Map<String, Object> args) {
        return new Title(args.get("id").toString(), args.get("name").toString(), Rarity.valueOf(args.get("rarity").toString()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Title) {
            return ((Title) o).getRarity().getRanking();
        }
        return 0;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("name", name);
        result.put("rarity", rarity.name());

        return result;
    }

    public int getRanking() {
        return getRarity().getRanking();
    }

    @Override
    public String toString() {
        return rarity.getColor() + name + ChatColor.RESET;
    }

    public String formatted(ChatColor color) {
        return rarity.getColor() + name + color + " (" + ChatColor.RESET + id + color + ")";
    }

    public enum Rarity {

        COMMON(ChatColor.GRAY, 1),
        UNCOMMON(ChatColor.DARK_GREEN, 2),
        RARE(ChatColor.LIGHT_PURPLE, 3),
        EPIC(ChatColor.RED, 4),
        LEGENDARY(ChatColor.GOLD, 5),
        STAFF(ChatColor.DARK_RED, 6);

        private ChatColor color;
        private int ranking;

        Rarity(ChatColor color, int ranking) {
            this.color = color;
            this.ranking = ranking;
        }

        public int getRanking() {
            return ranking;
        }

        public ChatColor getColor() {
            return color;
        }
    }
}
