package net.rhine.plugin.setmore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Setmore extends JavaPlugin {
    private org.bukkit.Location spawnLocation;
    private Map<String, Location> homeLocations = new HashMap<>();
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfig().getString("messages.thisCommandIsOnlyPlayer", "Only players can use this command!"));
            return true;
        }
        if (command.getName().equalsIgnoreCase("spawn")) {
            spawn(sender, command, label, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("setspawn")) {
            setspawn(sender, command, label, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("delspawn")) {
            delspawn(sender, command, label, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("spawninfo")) {
            spawninfo(sender, command, label, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("home")) {
            home(sender, command, label, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("sethome")) {
            sethome(sender, command, label, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("delhome")) {
            delhome(sender, command, label, args);
            return true;
        } else if (command.getName().equalsIgnoreCase("homelist")) {
            homelist(sender, command, label, args);
            return true;
        }
        return false;
    }

    private void spawn(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        FileConfiguration config = getConfig();
        if (!config.contains("spawn")) {
            String failedSpawnMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.failedSpawnIsNotBeenSetYet", "&cSpawn location has not been set yet!"));
            player.sendMessage(failedSpawnMessage);
        }
        else {
            String worldName = config.getString("spawn.world");
            double x = config.getDouble("spawn.x");
            double y = config.getDouble("spawn.y");
            double z = config.getDouble("spawn.z");
            float yaw = (float)config.getDouble("spawn.yaw");
            float pitch = (float)config.getDouble("spawn.pitch");
            Location spawnLocation = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
            player.teleport(spawnLocation);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successTeleportSpawn", "&aTeleported to spawn!")));
        }
    }

    private void setspawn(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        Location location = player.getLocation();
        double x = Math.round(location.getX() * 10.0D) / 10.0D;
        double y = Math.round(location.getY() * 10.0D) / 10.0D;
        double z = Math.round(location.getZ() * 10.0D) / 10.0D;
        FileConfiguration config = getConfig();
        config.set("spawn.world", location.getWorld().getName());
        config.set("spawn.x", Double.valueOf(x));
        config.set("spawn.y", Double.valueOf(y));
        config.set("spawn.z", Double.valueOf(z));
        config.set("spawn.yaw", Float.valueOf(location.getYaw()));
        config.set("spawn.pitch", Float.valueOf(location.getPitch()));
        saveConfig();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successSetSpawn", "&aSpawn location set!")));
    }

    private void delspawn(CommandSender sender, Command command, String label, String[] args) {
        if (config.contains("spawn")) {
            config.set("spawn", null);
            saveConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successDelSpawn", "&aSpawn location deleted!")));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.failedDeleteSpawnDoesNotExist", "&cSpawn location does not exist to delete!")));
        }
    }

    private void spawninfo(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        FileConfiguration config = getConfig();
        if (!config.contains("spawn")) {
            String failedSpawnMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.failedSpawnIsNotBeenSetYet", "&cSpawn location has not been set yet!"));
            player.sendMessage(failedSpawnMessage);
        }
        else {
            double x = config.getDouble("spawn.x");
            double y = config.getDouble("spawn.y");
            double z = config.getDouble("spawn.z");
            String spawnLocation = String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(z);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.spawnLocation") + spawnLocation));
        }
    }

    private void home(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        if (args.length != 1) {
            player.sendMessage("Usage: /home [home]");
        }
        else {
            String homeName = args[0];
            String uuid = player.getUniqueId().toString();
            String homeKey = "homes." + uuid + "." + homeName;
            if (getConfig().contains(homeKey)) {
                double x = getConfig().getDouble(homeKey + ".X");
                double y = getConfig().getDouble(homeKey + ".Y");
                double z = getConfig().getDouble(homeKey + ".Z");
                float yaw = (float)getConfig().getDouble(homeKey + ".Yaw");
                float pitch = (float)getConfig().getDouble(homeKey + ".Pitch");
                Location homeLocation = new Location(player.getWorld(), x, y, z, yaw, pitch);
                player.teleport(homeLocation);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successTeleportHome", "&aTeleported to the home.")));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.failedThisHomeDoesNotExist", "&cThis home does not exist")));
            }
        }
    }

    private void sethome(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        if (args.length != 1) {
            player.sendMessage("Usage: /sethome [home]");
        }
        else {
            String homeName = args[0];
            String uuid = player.getUniqueId().toString();
            String homeKey = "homes." + uuid + "." + homeName;
            double x = Math.round(player.getLocation().getX() * 10.0D) / 10.0D;
            double y = Math.round(player.getLocation().getY() * 10.0D) / 10.0D;
            double z = Math.round(player.getLocation().getZ() * 10.0D) / 10.0D;
            float yaw = player.getLocation().getYaw();
            float pitch = player.getLocation().getPitch();
            getConfig().set(homeKey + ".X", Double.valueOf(x));
            getConfig().set(homeKey + ".Y", Double.valueOf(y));
            getConfig().set(homeKey + ".Z", Double.valueOf(z));
            getConfig().set(homeKey + ".Yaw", Float.valueOf(yaw));
            getConfig().set(homeKey + ".Pitch", Float.valueOf(pitch));
            saveConfig();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successSetHome", "&aHome is set.")));
        }
    }

    private void delhome(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        if (args.length != 1) {
            player.sendMessage("Usage: /delhome [home]");
        }
        else {
            String homeName = args[0];
            String uuid = player.getUniqueId().toString();
            String homeKey = "homes." + uuid + "." + homeName;
            if (getConfig().contains(homeKey)) {
                getConfig().set(homeKey, null);
                saveConfig();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.successDelHome", "&aHome has been deleted.")));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.failedThisHomeDoesNotExist", "&cThis home does not exist")));
            }
        }
    }

    private void homelist(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        String uuid = player.getUniqueId().toString();
        ConfigurationSection homesSection = getConfig().getConfigurationSection("homes." + uuid);
        if (homesSection != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.yourHomes", "&fYour homes")));
            for (String homeName : homesSection.getKeys(false))
                player.sendMessage("- " + homeName);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.failedYouDoNotHaveHome", "&cYou don't have home.")));
        }
    }
}
