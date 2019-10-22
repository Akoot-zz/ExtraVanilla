package net.akoot.plugins.extravanilla.advancements;

import net.akoot.plugins.extravanilla.ExtraVanilla;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

import java.util.Collection;

public class DoubleBed implements Advancement {

    // yea idk what im doin

    private ExtraVanilla plugin;

    public DoubleBed(ExtraVanilla instance) {
        plugin = instance;
    }

    @Override
    public Collection<String> getCriteria() {
        return null;
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(plugin, "memes/double_bed");
    }
}
