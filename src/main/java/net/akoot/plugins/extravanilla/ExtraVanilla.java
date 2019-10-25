package net.akoot.plugins.extravanilla;

import net.akoot.plugins.extravanilla.commands.AfkCommand;
import net.akoot.plugins.extravanilla.commands.ChannelCommand;
import net.akoot.plugins.extravanilla.commands.ExtravanillaCommand;
import net.akoot.plugins.extravanilla.commands.TitleCommand;
import net.akoot.plugins.extravanilla.reference.ExtraPaths;
import net.akoot.plugins.extravanilla.serializable.Channel;
import net.akoot.plugins.extravanilla.serializable.Title;
import net.akoot.plugins.ultravanilla.Config;
import net.akoot.plugins.ultravanilla.Strings;
import net.akoot.plugins.ultravanilla.UltraVanilla;
import net.akoot.plugins.ultravanilla.util.IOUtil;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class ExtraVanilla extends JavaPlugin {

    private static ExtraVanilla instance;
    private UltraVanilla ultraVanilla;
    private Strings strings;
    private Config titles;
    private Config channels;

    public static ExtraVanilla getInstance() {
        return instance;
    }

    public UltraVanilla getUltraVanilla() {
        return ultraVanilla;
    }

    public Strings getStrings() {
        return strings;
    }

    public Config getTitles() {
        return titles;
    }

    public Config getChannels() {
        return channels;
    }

    @Override
    public void onEnable() {

        // Set the instance for getInstance()
        instance = this;

        // Register serializable classes
        ConfigurationSerialization.registerClass(Title.class);
        ConfigurationSerialization.registerClass(Channel.class);

        // Create directories
        getDataFolder().mkdirs();

        // Register strings instance for this plugin
        strings = new Strings(instance, getClass());

        // Copy defaults from the jar for config.yml if needed
        IOUtil.copyDefaults(new File(getDataFolder(), "config.yml"), getClass());

        // Register titles
        titles = new Config(instance, getClass(), "titles.yml");
        Titles.setTitles((List<Title>) titles.getConfig().getList(ExtraPaths.Titles.ROOT));

        // Register channels
        channels = new Config(instance, getClass(), "channels.yml");

        // Get the UltraVanilla plugin instance, disable this plugin otherwise
        ultraVanilla = (UltraVanilla) getServer().getPluginManager().getPlugin("UltraVanilla");
        if (ultraVanilla != null) {
            getLogger().info("Hooked to " + ultraVanilla.getDescription().getFullName() + "!");
        } else {
            getLogger().severe("Could not hook to UltraVanilla jar, disabling.");
            getServer().getPluginManager().disablePlugin(instance);
        }

        // Register /extravanilla command
        getCommand("extravanilla").setExecutor(new ExtravanillaCommand(instance, strings));

        // Register /channel command
        getCommand("channel").setExecutor(new ChannelCommand(instance, strings));

        // Register /titles command
        getCommand("title").setExecutor(new TitleCommand(instance, strings));

        // Register /afk command
        AfkCommand afkCommand = new AfkCommand(instance, strings);
        getCommand("afk").setExecutor(afkCommand);

        // Register events
        getServer().getPluginManager().registerEvents(new EventListener(instance), instance);

        getServer().getPluginManager().registerEvents(afkCommand, instance);
    }

    @Override
    public void onDisable() {
    }
}
