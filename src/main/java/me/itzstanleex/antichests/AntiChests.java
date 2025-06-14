package me.itzstanleex.antichests;

import me.itzstanleex.antichests.commands.AntiChestCommand;
import me.itzstanleex.antichests.utils.MetricsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiChests extends JavaPlugin {

    private static AntiChests instance;
    private MetricsManager metricsManager;

    @Override
    public void onEnable() {
        instance = this;

        // Loads/creates the configuration file
        saveDefaultConfig();

        // Registers the command
        getCommand("antichest").setExecutor(new AntiChestCommand());

        // Initializes bStats
        metricsManager = new MetricsManager(this);
        metricsManager.initializeMetrics();

        getLogger().info("AntiChest plugin has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Shuts down bStats when the plugin is disabled
        if (metricsManager != null) {
            metricsManager.shutdown();
        }

        getLogger().info("AntiChest plugin has been disabled!");
    }

    public static AntiChests getInstance() {
        return instance;
    }

}
