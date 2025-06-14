package me.itzstanleex.antichests.scanner;

import me.itzstanleex.antichests.AntiChests;
import me.itzstanleex.antichests.utils.MetricsManager;
import me.itzstanleex.antichests.utils.ContainerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ContainerScanner {

    private static final Set<Material> CONTAINER_TYPES = EnumSet.of(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.BARREL,
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX,
            Material.HOPPER,
            Material.DISPENSER,
            Material.DROPPER,
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.SMOKER,
            Material.BREWING_STAND,
            Material.CAMPFIRE,
            Material.SOUL_CAMPFIRE,
            Material.CRAFTER,
            Material.DECORATED_POT
    );

    public void scanAndClear(Player player, int radius) {
        player.sendMessage(ChatColor.YELLOW + "Scanning in progress...");

        Location center = player.getLocation();

        List<Location> containerLocations = findContainerLocations(center, radius);

        if (containerLocations.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "Scanning completed! No containers were found.");
            return;
        }

        player.sendMessage(ChatColor.GRAY + "Found " + containerLocations.size() + " containers, processing...");

        List<ContainerData> results = new ArrayList<>();

        // Gets batch size from configuration
        int batchSize = AntiChests.getInstance().getConfig().getInt("plugin.batch-size", 100);
        processContainersInBatches(containerLocations, results, player, 0, batchSize);
    }

    private List<Location> findContainerLocations(Location center, int radius) {
        List<Location> locations = new ArrayList<>();

        int chunkRadius = (radius / 16) + 1;
        int centerChunkX = center.getChunk().getX();
        int centerChunkZ = center.getChunk().getZ();

        for (int chunkX = centerChunkX - chunkRadius; chunkX <= centerChunkX + chunkRadius; chunkX++) {
            for (int chunkZ = centerChunkZ - chunkRadius; chunkZ <= centerChunkZ + chunkRadius; chunkZ++) {

                if (!center.getWorld().isChunkLoaded(chunkX, chunkZ)) {
                    continue;
                }

                for (int x = chunkX * 16; x < (chunkX * 16) + 16; x++) {
                    for (int z = chunkZ * 16; z < (chunkZ * 16) + 16; z++) {

                        double distance = Math.sqrt(Math.pow(x - center.getX(), 2) + Math.pow(z - center.getZ(), 2));
                        if (distance > radius) continue;

                        for (int y = center.getWorld().getMinHeight(); y <= center.getWorld().getMaxHeight(); y++) {
                            Block block = center.getWorld().getBlockAt(x, y, z);

                            if (CONTAINER_TYPES.contains(block.getType())) {
                                locations.add(block.getLocation());
                            }
                        }
                    }
                }
            }
        }

        return locations;
    }

    private void processContainersInBatches(List<Location> locations, List<ContainerData> results, Player player, int startIndex, int batchSize) {
        if (startIndex >= locations.size()) {
            // Records statistics for bStats
            int totalItemsRemoved = results.stream()
                    .mapToInt(data -> data.getRemovedItems().values().stream().mapToInt(Integer::intValue).sum())
                    .sum();

            MetricsManager.recordScan(totalItemsRemoved);

            displayResults(player, results);
            return;
        }

        int endIndex = Math.min(startIndex + batchSize, locations.size());

        for (int i = startIndex; i < endIndex; i++) {
            Location loc = locations.get(i);
            Block block = loc.getBlock();

            if (CONTAINER_TYPES.contains(block.getType())) {
                ContainerData data = processContainer(block);
                if (data != null && !data.getRemovedItems().isEmpty()) {
                    results.add(data);
                }
            }
        }

        int processed = endIndex;
        int total = locations.size();
        player.sendMessage(ChatColor.GRAY + "Processed " + processed + "/" + total + " containers...");

        final int nextIndex = endIndex;
        Bukkit.getScheduler().runTask(AntiChests.getInstance(), () -> {
            processContainersInBatches(locations, results, player, nextIndex, batchSize);
        });
    }

    private ContainerData processContainer(Block block) {
        if (!(block.getState() instanceof InventoryHolder)) {
            return null;
        }

        InventoryHolder holder = (InventoryHolder) block.getState();
        Inventory inventory = holder.getInventory();

        Map<Material, Integer> removedItems = new HashMap<>();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && !item.getType().isAir()) {
                Material material = item.getType();
                int amount = item.getAmount();

                removedItems.merge(material, amount, Integer::sum);
                inventory.setItem(i, null);
            }
        }

        if (removedItems.isEmpty()) {
            return null;
        }

        return new ContainerData(block.getLocation(), removedItems);
    }

    private void displayResults(Player player, List<ContainerData> results) {
        if (results.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "Scanning completed! No items were found.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Scanning completed! Found " + results.size() + " containers with items:");
        player.sendMessage(ChatColor.GRAY + "═══════════════════════════════════");

        for (ContainerData data : results) {
            Location loc = data.getLocation();
            StringBuilder message = new StringBuilder();
            message.append(ChatColor.YELLOW)
                    .append("Coordinates: ")
                    .append(ChatColor.WHITE)
                    .append(loc.getBlockX()).append(", ")
                    .append(loc.getBlockY()).append(", ")
                    .append(loc.getBlockZ())
                    .append(ChatColor.GRAY).append(" - ");

            boolean first = true;
            for (Map.Entry<Material, Integer> entry : data.getRemovedItems().entrySet()) {
                if (!first) {
                    message.append(", ");
                }
                message.append(ChatColor.RED)
                        .append(entry.getValue())
                        .append("x ")
                        .append(entry.getKey().name().toLowerCase().replace("_", " "));
                first = false;
            }

            player.sendMessage(message.toString());
        }

        player.sendMessage(ChatColor.GRAY + "═══════════════════════════════════");

        int totalItems = results.stream()
                .mapToInt(data -> data.getRemovedItems().values().stream().mapToInt(Integer::intValue).sum())
                .sum();

        player.sendMessage(ChatColor.GREEN + "Total removed: " + ChatColor.WHITE + totalItems + " items from " + results.size() + " containers");
    }
}
