package me.itzstanleex.antichests.utils;

import me.itzstanleex.antichests.AntiChests;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;

public class MetricsManager {

    private final AntiChests plugin;
    private Metrics metrics;
    private static int totalScansPerformed = 0;
    private static int totalItemsRemoved = 0;

    public MetricsManager(AntiChests plugin) {
        this.plugin = plugin;
    }

    /**
     * Initializes bStats metrics if enabled in config.yml
     */
    public void initializeMetrics() {
        if (!plugin.getConfig().getBoolean("bstats.enabled", true)) {
            plugin.getLogger().info("bStats are disabled in the configuration.");
            return;
        }

        try {
            int pluginId = 26171;

            metrics = new Metrics(plugin, pluginId);
            
            addCustomCharts();

            plugin.getLogger().info("bStats successfully initialized!");

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize bStats: " + e.getMessage());
        }
    }

    /**
     * Adds custom charts for bStats
     */
    private void addCustomCharts() {
        // Chart for total scans performed
        metrics.addCustomChart(new SingleLineChart("total_scans", () -> totalScansPerformed));

        // Chart for total items removed
        metrics.addCustomChart(new SingleLineChart("total_items_removed", () -> totalItemsRemoved));

        // Chart for number of online players during scan
        metrics.addCustomChart(new SingleLineChart("players_online_during_scan", () -> {
            return Bukkit.getOnlinePlayers().size();
        }));
    }

    /**
     * Records a performed scan
     * @param itemsRemoved number of items removed
     */
    public static void recordScan(int itemsRemoved) {
        totalScansPerformed++;
        totalItemsRemoved += itemsRemoved;
    }

    /**
     * Shuts down bStats metrics
     */
    public void shutdown() {
        if (metrics != null) {
            try {
                metrics.shutdown();
                plugin.getLogger().info("bStats have been shut down.");
            } catch (Exception e) {
                plugin.getLogger().warning("Error while shutting down bStats: " + e.getMessage());
            }
        }
    }
}
