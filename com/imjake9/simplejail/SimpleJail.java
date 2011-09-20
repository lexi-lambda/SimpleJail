package com.imjake9.simplejail;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class SimpleJail extends JavaPlugin {
    
    private static final Logger log = Logger.getLogger("Minecraft");
    private ColouredConsoleSender console;
    public static PermissionHandler permissions;
    public static PermissionsPlugin bukkitPermissions;
    private int[] jailCoords = new int[3];
    private int[] unjailCoords = new int[3];
    private String jailGroup;
    private Configuration perms;
    private Configuration jailed;
    private boolean newPerms = false;
    private SimpleJailPlayerListener listener;
    public boolean useBukkitPermissions = true;
    
    @Override
    public void onDisable() {
        log.info("[SimpleJail] " + this.getDescription().getName() + " v" + this.getDescription().getVersion() +  " disabled.");
    }

    @Override
    public void onEnable() {
        
        // Get console:
        console = ((CraftServer)this.getServer()).getServer().console;
        
        // Load configuration:
        this.loadConfig();
        
        // Get permissions plugin:
        bukkitPermissions = (PermissionsPlugin)this.getServer().getPluginManager().getPlugin("PermissionsBukkit");
        if(!this.isEnabled()) return;
        
        listener = new SimpleJailPlayerListener(this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, listener, Priority.High, this);
        
        log.info("[SimpleJail] " + this.getDescription().getName() + " v" + this.getDescription().getVersion() + " enabled.");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        
        if(commandLabel.equalsIgnoreCase("jail") && args.length == 1) {
            if(!hasPermission(sender, "SimpleJail.jail")) return true;
            this.jailPlayer(sender, args);
            return true;
        } else if(commandLabel.equalsIgnoreCase("unjail") && args.length == 1) {
            if(!hasPermission(sender, "SimpleJail.unjail")) return true;
            this.unjailPlayer(sender, args);
            return true;
        } else if(commandLabel.equalsIgnoreCase("setjail") && (args.length == 0 || args.length == 3)) {
            if(!hasPermission(sender, "SimpleJail.setjail")) return true;
            this.setJail(sender, args);
            return true;
        } else if(commandLabel.equalsIgnoreCase("setunjail") && (args.length == 0 || args.length == 3)) {
            if(!hasPermission(sender, "SimpleJail.setjail")) return true;
            this.setUnjail(sender, args);
            return true;
        } else {
            if(!hasPermission(sender, "SimpleJail.jail")) return true;
            if(!hasPermission(sender, "SimpleJail.unjail")) return true;
            if(!hasPermission(sender, "SimpleJail.setjail")) return true;
            return false;
        }
        
    }
    
    public void jailPlayer(CommandSender sender, String[] args) {
        Player player = this.getServer().getPlayer(args[0]);
        
        if(player == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find player \"" + args[0] + ".");
            return;
        }
        
        args[0] = player.getName().toLowerCase();
        
        // Check if player is slready jailed:
        if(jailed.getProperty(args[0]) != null) {
            sender.sendMessage(ChatColor.RED + "That player is already in jail!");
            return;
        }
        
        // Move player into jail:
        player.teleport(new Location(player.getWorld(), jailCoords[0], jailCoords[1], jailCoords[2]));
        
        // if (bukkitPermissions != null) {
        List<String> groupName = this.getGroups(player);
        jailed.setProperty(args[0], groupName);
        this.setGroup(player, jailGroup);
        
        jailed.save();
        player.sendMessage(ChatColor.AQUA + "You have been jailed!");
        sender.sendMessage(ChatColor.AQUA + "Player sent to jail.");
    }
    
    public void unjailPlayer(CommandSender sender, String[] args) {
        Player player = this.getServer().getPlayer(args[0]);
        
        if(player == null) {
            sender.sendMessage(ChatColor.RED + "Couldn't find player \"" + args[0] + ".");
            return;
        }
        
        args[0] = player.getName().toLowerCase();
        
        // Check if player is in jail:
        if(jailed.getProperty(args[0]) == null) {
            sender.sendMessage(ChatColor.RED + "That player is not in jail!");
            return;
        }
        
        // Move player out of jail:
        player.teleport(new Location(player.getWorld(), unjailCoords[0], unjailCoords[1], unjailCoords[2]));
        
        // if (bukkitPermissions != null) {
        this.setGroup(player, jailed.getStringList(args[0], new ArrayList()));
        
        jailed.removeProperty(args[0]);
        jailed.save();
        
        player.sendMessage(ChatColor.AQUA + "You have been removed from jail!");
        sender.sendMessage(ChatColor.AQUA + "Player removed from jail.");
    }
    
    public void setJail(CommandSender sender, String args[]) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use that.");
            return;
        }
        if(args.length == 0) {
            Player player = (Player)sender;
            Location loc = player.getLocation();
            jailCoords[0] = loc.getBlockX();
            jailCoords[1] = loc.getBlockY();
            jailCoords[2] = loc.getBlockZ();
        } else {
            if(!(new Scanner(args[0]).hasNextInt()) || !(new Scanner(args[1]).hasNextInt()) || !(new Scanner(args[2]).hasNextInt())) {
                sender.sendMessage(ChatColor.RED + "Invalid coordinate.");
                return;
            }
            jailCoords[0] = Integer.parseInt(args[0]);
            jailCoords[1] = Integer.parseInt(args[1]);
            jailCoords[2] = Integer.parseInt(args[2]);
        }
        
        Configuration config = this.getConfiguration();
        config.setProperty("jail.x", jailCoords[0]);
        config.setProperty("jail.y", jailCoords[1]);
        config.setProperty("jail.z", jailCoords[2]);
        config.save();
        sender.sendMessage(ChatColor.AQUA + "Jail point saved.");
    }
    
    public void setUnjail(CommandSender sender, String args[]) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use that.");
            return;
        }
        if(args.length == 0) {
            Player player = (Player)sender;
            Location loc = player.getLocation();
            unjailCoords[0] = loc.getBlockX();
            unjailCoords[1] = loc.getBlockY();
            unjailCoords[2] = loc.getBlockZ();
        } else {
            if(!(new Scanner(args[0]).hasNextInt()) || !(new Scanner(args[1]).hasNextInt()) || !(new Scanner(args[2]).hasNextInt())) {
                sender.sendMessage(ChatColor.RED + "Invalid coordinate.");
                return;
            }
            unjailCoords[0] = Integer.parseInt(args[0]);
            unjailCoords[1] = Integer.parseInt(args[1]);
            unjailCoords[2] = Integer.parseInt(args[2]);
        }
        
        Configuration config = this.getConfiguration();
        config.setProperty("unjail.x", unjailCoords[0]);
        config.setProperty("unjail.y", unjailCoords[1]);
        config.setProperty("unjail.z", unjailCoords[2]);
        config.save();
        sender.sendMessage(ChatColor.AQUA + "Unjail point saved.");
    }
    
    public void loadConfig() {
        Configuration config = this.getConfiguration();
        jailCoords[0] = config.getInt("jail.x", 0);
        jailCoords[1] = config.getInt("jail.y", 0);
        jailCoords[2] = config.getInt("jail.z", 0);
        unjailCoords[0] = config.getInt("unjail.x", 0);
        unjailCoords[1] = config.getInt("unjail.y", 0);
        unjailCoords[2] = config.getInt("unjail.z", 0);
        jailGroup = config.getString("jailgroup", "Jailed");
        config.save();
        
        File f = new File(this.getDataFolder().getPath() + File.separator + "jailed.yml");
        try {
            if(!f.exists()) f.createNewFile();
        } catch (IOException ex) {}
        jailed = new Configuration(f);
        jailed.load();
    }
    
    public void setupPermissions() {
        
        Plugin bukkit = this.getServer().getPluginManager().getPlugin("PermissionsBukkit");
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Permissions");
        
        if(bukkitPermissions == null){
           if(bukkit != null){
               bukkitPermissions = (PermissionsPlugin)plugin;
           } else {
               log.info("[SimpleJail] ERROR: PermissionsBukkit not detected.");
               this.getServer().getPluginManager().disablePlugin(this);
               return;
           }
        }
    }
    
    public void convertPermission(String key) {
        List groupList = new ArrayList();
        groupList.add(key);
        jailed.removeProperty(key);
        jailed.setProperty(key, groupList);
    }
    
    public Location getJailLocation(Player player) {
        return new Location(player.getWorld(), jailCoords[0], jailCoords[1], jailCoords[2]);
    }
    
    public boolean playerIsJailed(Player player) {
        if (jailed.getProperty(player.getName().toLowerCase()) != null)
            return true;
        return false;
    }
    
    public boolean hasPermission(CommandSender sender, String permission) {
        if (sender instanceof Player)
            return sender.hasPermission(permission);
        else return true;
    }
    
    public List<String> getGroups(Player player) {
        // if(bukkitPermissions != null) {
        List<Group> groups = bukkitPermissions.getGroups(player.getName());
        List<String> stringGroups = new ArrayList<String>();
        for (Group g : groups) {
            stringGroups.add(g.getName());
        }
        return stringGroups;
    }
    
    public void setGroup(Player player, String group) {
        // if (bukkitPermissions != null)
        this.getServer().dispatchCommand(console, "permissions player setgroup " + player.getName() + " " + group);
    }
    
    public void setGroup(Player player, List<String> group) {
        // if (bukkitPermissions != null) {
        String params = new String();
        for (String grp : group) {
            params += " " + grp;
        }
        this.getServer().dispatchCommand(console, "permissions player setgroup " + player.getName() + params);
    }
}
