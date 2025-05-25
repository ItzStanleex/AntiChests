package me.itzstanleex.antichests.utils;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;

public class ContainerData {
    private final Location location;
    private final Map<Material, Integer> removedItems;

    public ContainerData(Location location, Map<Material, Integer> removedItems) {
        this.location = location;
        this.removedItems = removedItems;
    }

    public Location getLocation() {
        return location;
    }

    public Map<Material, Integer> getRemovedItems() {
        return removedItems;
    }
}