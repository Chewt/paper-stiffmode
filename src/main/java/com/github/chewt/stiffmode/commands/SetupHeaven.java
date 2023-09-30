package com.github.chewt.stiffmode.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.github.chewt.stiffmode.StiffMode;

public class SetupHeaven implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        StiffMode.getInstance().getLogger().info("Commandline args: ");
        for (String string : args) {
            StiffMode.getInstance().getLogger().info(string);
        }
        if (sender instanceof Player) {
            StiffMode.getInstance().getLogger().info("Is player");
            Player player = (Player) sender;
            if (player.isOp() == true) {
                StiffMode.getInstance().getLogger().info("Is op");
                if (args[0].equalsIgnoreCase("start")) {
                    StiffMode.getInstance().getLogger().info("Player issued start.");
                    StiffMode.getInstance().god = player;
                    StiffMode.getInstance().gods_location = player.getLocation();
                    StiffMode.getInstance().gods_gamemode = player.getGameMode();
                    StiffMode.getInstance().gods_inventory = Bukkit.createInventory(null, InventoryType.PLAYER);
                    StiffMode.getInstance().gods_inventory.setContents(player.getInventory().getContents());
                    player.getInventory().clear();
                    player.getInventory().addItem(new ItemStack(Material.ACACIA_BUTTON));
                    player.setGameMode(GameMode.CREATIVE);
                    player.teleport(StiffMode.getInstance().heaven);
                } else if (args[0].equalsIgnoreCase("stop")){
                    player.getInventory().clear();
                    player.getInventory().setContents(StiffMode.getInstance().gods_inventory.getContents());
                    player.setGameMode(StiffMode.getInstance().gods_gamemode);
                    player.teleport(StiffMode.getInstance().gods_location);
                    StiffMode.getInstance().god = null;
                }
            }
        }
        return true;
    }

}
