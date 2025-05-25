package me.itzstanleex.antichests.commands;

import me.itzstanleex.antichests.scanner.ContainerScanner;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AntiChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You cant run this command from console!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("antichest.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /antichest <radius>");
            return true;
        }

        int radius;
        try {
            radius = Integer.parseInt(args[0]);
            if (radius <= 0 || radius > 2000) {
                player.sendMessage(ChatColor.RED + "Radius must be 1-2000!");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Radius must by number!");
            return true;
        }


        ContainerScanner scanner = new ContainerScanner();
        scanner.scanAndClear(player, radius);

        return true;
    }
}