package me.itzstanleex.antichests;

import me.itzstanleex.antichests.commands.AntiChestCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiChests extends JavaPlugin {

    private static AntiChests instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("antichest").setExecutor(new AntiChestCommand());

        getLogger().info("AntiChest plugin has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AntiChest plugin has been disabled!");
    }

    public static AntiChests getInstance() {
        return instance;
    }
}