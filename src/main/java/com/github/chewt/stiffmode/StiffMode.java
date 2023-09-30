package com.github.chewt.stiffmode;

import net.kyori.adventure.text.Component;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.chewt.stiffmode.commands.SetupHeaven;
import com.github.chewt.stiffmode.commands.SetupHell;

public class StiffMode extends JavaPlugin implements Listener {

    private static StiffMode instance;

    public String heaven_worldname;
    public String hell_worldname;

    public Location heaven;
    public Location heavens_button;
    public Location hell;
    public Location hells_button;
    public Location earth;

    public Player god;
    public Inventory gods_inventory;
    public Location gods_location;
    public GameMode gods_gamemode;

    File config;
    FileConfiguration configz;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        instance = this;

        // Register commands
        this.getCommand("setupHeaven").setExecutor(new SetupHeaven());
        this.getCommand("setupHell").setExecutor(new SetupHell());

        // Load configuration
        config = new File(getDataFolder(), "stiffcore-config.yml");
        configz = YamlConfiguration.loadConfiguration(config);
        saveConfig();


        heaven_worldname = configz.getString("heaven.worldname", "none");
        hell_worldname = configz.getString("hell.worldname", "none");

        // Load heaven and hell, and store spawn locations of each
        if (heaven_worldname == "none" && hell_worldname == "none") {
            getLogger().info("You must supply world names for at least one of heaven or hell!");
            if (heaven_worldname == "none") {
                configz.set("heaven.worldname", "none");
            }
            if (hell_worldname == "none") {
                configz.set("hell.worldname", "none");
            }
            saveConfig();
        } else {
            if (hell_worldname != "none") {
                getServer().createWorld(new WorldCreator(hell_worldname));
                hell = Bukkit.getWorld(hell_worldname).getSpawnLocation();
                hells_button = new Location(hell.getWorld(), configz.getInt("hell.button.x", 0), configz.getInt("hell.button.y", 0), configz.getInt("hell.button.z", 0));
            }
            if (heaven_worldname != "none") {
                getServer().createWorld(new WorldCreator(heaven_worldname));
                heaven = Bukkit.getWorld(heaven_worldname).getSpawnLocation();
                heavens_button = new Location(heaven.getWorld(), configz.getInt("heaven.button.x", 0), configz.getInt("heaven.button.y", 0), configz.getInt("heaven.button.z", 0));
            }
            earth = Bukkit.getWorld("world").getSpawnLocation();
        }
    }

    public void saveConfig() {
        try {
            configz.save(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onButtonPlace(BlockPlaceEvent event) {
        if (event.getPlayer() == god)
        {
            if (event.getBlock().getType() == Material.ACACIA_BUTTON) {
                if (event.getBlock().getWorld() == hell.getWorld())
                    setHellsButtonLocation(event.getBlock().getLocation());
                else if (event.getBlock().getWorld() == heaven.getWorld())
                    setHeavensButtonLocation(event.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onButtonPress(PlayerInteractEvent event) {
        Block blockPressed = event.getClickedBlock();
        if (blockPressed == null || event.getPlayer() == god)
            return;

        // Send to heaven or hell if press button
        if (blockPressed.getType() == Material.ACACIA_BUTTON && event.getPlayer().getWorld() == hell.getWorld()) {
            if (blockPressed.getLocation().equals(hells_button)) {
                event.getPlayer().teleport(heaven);
            }
        } else if (blockPressed.getType() == Material.ACACIA_BUTTON && event.getPlayer().getWorld() == heaven.getWorld()) {
            if (blockPressed.getLocation().equals(heavens_button)) {
                event.getPlayer().setGameMode(GameMode.SURVIVAL);
                event.getPlayer().teleport(earth);
            }
        }
    }

    public void setHeavensButtonLocation(Location location) {
        configz.set("heaven.button.x", location.getX());
        configz.set("heaven.button.y", location.getY());
        configz.set("heaven.button.z", location.getZ());
        saveConfig();
        heavens_button = location;
    }

    public void setHellsButtonLocation(Location location) {
        configz.set("hell.button.x", location.getX());
        configz.set("hell.button.y", location.getY());
        configz.set("hell.button.z", location.getZ());
        saveConfig();
        hells_button = location;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(
                Component.text("Hello, " + event.getPlayer().getName() + "! Stiffmode is enabled on this server. If you die, you will have to undergo a challenge to make it back to the mortal realm."));
    }

    @EventHandler
    public void OnPlayerRespawn (PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        int monstersKilled = getMonstersKilledByPlayer(player);
        int animalsKilled = getAnimalsKilledByPlayer(player);
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
        if (monstersKilled >= animalsKilled) {
            player.sendMessage(Component.text("Sending you to heaven..."));
            event.setRespawnLocation(heaven);
        } else {
            player.sendMessage(Component.text("Sending you to hell..."));
            event.setRespawnLocation(hell);
        }
    }

    private int getMonstersKilledByPlayer(Player player) {
        int totalKilled = 0;
        totalKilled += player.getStatistic(Statistic.KILL_ENTITY, EntityType.CREEPER);
        totalKilled += player.getStatistic(Statistic.KILL_ENTITY, EntityType.ZOMBIE);
        totalKilled += player.getStatistic(Statistic.KILL_ENTITY, EntityType.SKELETON);

        return totalKilled;
    }

    private int getAnimalsKilledByPlayer(Player player) {
        int totalKilled = 0;
        totalKilled += player.getStatistic(Statistic.KILL_ENTITY, EntityType.COW);
        totalKilled += player.getStatistic(Statistic.KILL_ENTITY, EntityType.SHEEP);
        totalKilled += player.getStatistic(Statistic.KILL_ENTITY, EntityType.PIG);
        totalKilled += player.getStatistic(Statistic.KILL_ENTITY, EntityType.CHICKEN);

        return totalKilled;
    }

    public static StiffMode getInstance() {
        return instance;
    }
}
