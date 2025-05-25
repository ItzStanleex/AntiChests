# AntiChests Plugin
A powerful Minecraft Bukkit/Spigot plugin designed to prevent builders from leaving behind containers with valuable items when building maps or structures.

# Description
AntiChests is a utility plugin that helps server administrators and map creators ensure clean builds by scanning and clearing all containers within a specified radius. This prevents builders from accidentally (or intentionally) leaving behind chests, barrels, or other containers filled with valuable items like diamond blocks, bedrock, or other restricted materials.

# Features

- Comprehensive Container Scanning - Detects all types of containers including chests, barrels, shulker boxes, hoppers, furnaces, and more

- Detailed Reporting - Shows exact coordinates and items removed from each container

- Batch Processing - Processes containers in batches to minimize server impact

# Supported Container Types

- Chests (regular and trapped)
Barrels
All Shulker Box variants (16 colors)
Hoppers
Dispensers & Droppers
Furnaces, Blast Furnaces & Smokers

# Installation

Download the latest release from the Releases page
Place the AntiChests.jar file in your server's plugins folder
Restart your server and Enjoy!

# Usage
Command ``/antichest <radius>``

Parameters:

radius - The scanning radius in blocks (1-2000)

**Examples:**
- ``/antichest 50``    Scans 50 blocks around you (recommended for testing)


- ``/antichest 100``   Scans 100 blocks around you (good balance)


- ``/antichest 500``   Scans 500 blocks around you (may cause lag)

Permissions
antichest.use - Allows usage of the /antichest command


*If you find bug, dm me on discord (My discord: itzstanleex)*

⚠️ Warning: This plugin will permanently remove all items from containers within the specified radius. Make sure to backup your world before using it on important builds!