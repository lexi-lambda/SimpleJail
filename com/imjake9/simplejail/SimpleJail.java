package com.imjake9.simplejail;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class SimpleJail extends JavaPlugin {
    
    private static final Logger log = Logger.getLogger("Minecraft");
    public ConsoleCommandSender console;
    private PermissionsPlugin bukkitPermissions;
    private PermissionManager pexPermissions;
    private Location jailLoc;
    private Location unjailLoc;
    private String jailGroup;
    private YamlConfiguration jailed;
    private SimpleJailPlayerListener listener;
    
    @Override
    public void onDisable() {
        log.info("[SimpleJail] " + this.getDescription().getName() + " v" + this.getDescription().getVersion() +  " disabled.");
    }

    @Override
    public void onEnable() {
        
        // Get console:
        console = this.getServer().getConsoleSender();
        
        // Load configuration:
        this.loadConfig();
        
        // Get permissions plugin:
        this.setupPermissions();
        if(!this.isEnabled()) return;
        
        listener = new SimpleJailPlayerListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);
        
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                
                long currentTime = System.currentTimeMillis();
                
                for (Player p : getServer().getOnlinePlayers()) {
                    
                    if (!playerIsJailed(p) || !playerIsTempJailed(p)) continue;

                    double tempTime = getTempJailTime(p);

                    if (tempTime <= currentTime) {
                        unjailPlayer(console, new String[]{p.getName()}, true);
                    }
                    
                }
                
            }
            
        }, 600, 600);
        
        log.info("[SimpleJail] " + this.getDescription().getName() + " v" + this.getDescription().getVersion() + " enabled.");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        
        if(commandLabel.equalsIgnoreCase("jail")) {
            if(!hasPermission(sender, "simplejail.jail")) {
                JailMessage.LACKS_PERMISSIONS.send(sender, "simplejail.jail");
                return true;
            }
            if (args.length != 1 && args.length != 2) return false;
            this.jailPlayer(sender, args);
            return true;
        } else if(commandLabel.equalsIgnoreCase("unjail")) {
            if(!hasPermission(sender, "simplejail.unjail")) {
                JailMessage.LACKS_PERMISSIONS.send(sender, "simplejail.unjail");
                return true;
            }
            if (args.length != 1) return false;
            this.unjailPlayer(sender, args);
            return true;
        } else if(commandLabel.equalsIgnoreCase("setjail")) {
            if(!hasPermission(sender, "simplejail.setjail")) {
                JailMessage.LACKS_PERMISSIONS.send(sender, "simplejail.setjail");
                return true;
            }
            if (args.length != 0 && args.length != 4) return false;
            this.setJail(sender, args);
            return true;
        } else if(commandLabel.equalsIgnoreCase("setunjail")) {
            if(!hasPermission(sender, "simplejail.setjail")) {
                JailMessage.LACKS_PERMISSIONS.send(sender, "simplejail.setjail");
                return true;
            }
            if (args.length != 0 && args.length != 4) return false;
            this.setUnjail(sender, args);
            return true;
        } else if(commandLabel.equalsIgnoreCase("jailtime")) {
            if(!hasPermission(sender, "simplejail.jailtime")) {
                JailMessage.LACKS_PERMISSIONS.send(sender, "simplejail.jailtime");
                return true;
            }
            if (args.length > 1) return false;
            this.jailTime(sender, args);
            return true;
        } else {
            return false;
        }
        
    }
    
    public void jailPlayer(CommandSender sender, String[] args) {
        Player player = this.getServer().getPlayer(args[0]);
        
        args[0] = (player == null) ? args[0].toLowerCase() : player.getName().toLowerCase();
        
        // Check if player is slready jailed:
        if(jailed.get(args[0]) != null) {
            JailMessage.ALREADY_IN_JAIL.send(sender, args[0]);
            return;
        }
        
        List<String> groupName = this.getGroups(args[0]);
        jailed.set(args[0] + ".groups", groupName);
        this.setGroup(args[0], jailGroup);
        
        int minutes = 0;
        
        if(args.length == 2) {
            minutes = this.parseTimeString(args[1]);
            if(minutes != -1) {
                double tempTime = System.currentTimeMillis() + (minutes * 60000);
                jailed.set(args[0] + ".tempTime", tempTime);
            }
        }
        
        // Move player into jail:
        if (player != null)
            player.teleport(jailLoc);
        else
            jailed.set(args[0] + ".status", "pending");
        
        this.saveJail();
        
        if (player != null) {
            if(args.length == 1 || minutes == -1) JailMessage.JAILED.send(player);
            else JailMessage.TEMPJAILED.send(player, this.prettifyMinutes(minutes));
        }
        if (args.length == 1 || minutes == -1) JailMessage.JAIL.send(sender, args[0]);
        else JailMessage.TEMPJAIL.send(sender, args[0], this.prettifyMinutes(minutes));
    }
    
    public void unjailPlayer(CommandSender sender, String[] args, boolean fromTempJail) {
        Player player = this.getServer().getPlayer(args[0]);
        
        args[0] = (player == null) ? args[0].toLowerCase() : player.getName().toLowerCase();
        
        // Check if player is in jail:
        if(jailed.get(args[0]) == null) {
            JailMessage.NOT_IN_JAIL.send(sender, args[0]);
            return;
        }
        
        // Check if player is offline:
        if (player == null) {
            jailed.set(args[0] + ".status", "freed");
            JailMessage.UNJAIL.send(sender, args[0]);
            return;
        }
        
        // Move player out of jail:
        player.teleport(unjailLoc);
        
        this.setGroup(args[0], jailed.getStringList(args[0] + ".groups"));
        
        jailed.set(args[0], null);
        
        this.saveJail();
        
        JailMessage.UNJAILED.send(player);
        if (fromTempJail) JailMessage.UNTEMPJAILED.print(args[0]);
        else JailMessage.UNJAIL.send(sender, args[0]);
    }
    
    public void unjailPlayer(CommandSender sender, String[] args) {
        this.unjailPlayer(sender, args, false);
    }
    
    public void setJail(CommandSender sender, String[] args) {
        if(!(sender instanceof Player) && args.length != 4) {
            JailMessage.ONLY_PLAYERS.send(sender);
            return;
        }
        if(args.length == 0) {
            Player player = (Player)sender;
            jailLoc = player.getLocation();
        } else {
            if(!(new Scanner(args[0]).hasNextInt()) || !(new Scanner(args[1]).hasNextInt()) || !(new Scanner(args[2]).hasNextInt())) {
                JailMessage.INVALID_COORDINATE.send(sender);
                return;
            }
            jailLoc = new Location(
                    this.getServer().getWorld(args[3]),
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]));
        }
        
        YamlConfiguration config = (YamlConfiguration) this.getConfig();
        config.set("jail.x", (int) jailLoc.getX());
        config.set("jail.y", (int) jailLoc.getY());
        config.set("jail.z", (int) jailLoc.getZ());
        config.set("jail.world", jailLoc.getWorld().getName());
        
        this.saveConfig();
        
        JailMessage.JAIL_POINT_SET.send(sender);
    }
    
    public void setUnjail(CommandSender sender, String[] args) {
        if(!(sender instanceof Player) && args.length != 4) {
            JailMessage.ONLY_PLAYERS.send(sender);
            return;
        }
        if(args.length == 0) {
            Player player = (Player)sender;
            unjailLoc = player.getLocation();
        } else {
            if(!(new Scanner(args[0]).hasNextInt()) || !(new Scanner(args[1]).hasNextInt()) || !(new Scanner(args[2]).hasNextInt())) {
                JailMessage.INVALID_COORDINATE.send(sender);
                return;
            }
            unjailLoc = new Location(
                    this.getServer().getWorld(args[3]),
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]));
        }
        
        YamlConfiguration config = (YamlConfiguration) this.getConfig();
        config.set("unjail.x", (int) unjailLoc.getX());
        config.set("unjail.y", (int) unjailLoc.getY());
        config.set("unjail.z", (int) unjailLoc.getZ());
        config.set("unjail.world", unjailLoc.getWorld().getName());
        
        this.saveConfig();
        
        JailMessage.UNJAIL_POINT_SET.send(sender);
    }
    
    public void jailTime(CommandSender sender, String[] args) {
        if(!(sender instanceof Player) && args.length == 0) {
            JailMessage.MUST_SPECIFY_TARGET.send(sender);
            return;
        }
        Player player = (args.length == 0) ? (Player)sender : this.getServer().getPlayer(args[0]);
        if(player == null) {
            JailMessage.PLAYER_NOT_FOUND.send(sender, args[0]);
            return;
        }
        if(!this.playerIsTempJailed(player)) {
            JailMessage.NOT_TEMPJAILED.send(sender, (args.length == 0) ? sender.getName() : args[0]);
            return;
        }
        int minutes = (int)((this.getTempJailTime(player) - System.currentTimeMillis()) / 60000);
        JailMessage.JAIL_TIME.send(sender, this.prettifyMinutes(minutes));
    }
    
    public void loadConfig() {
        // Init config files:
        YamlConfiguration config = (YamlConfiguration) this.getConfig();
        config.options().copyDefaults(true);
        config.addDefault("jailgroup", "Jailed");
        config.addDefault("jail.world", this.getServer().getWorlds().get(0).getName());
        config.addDefault("jail.x", 0);
        config.addDefault("jail.y", 0);
        config.addDefault("jail.z", 0);
        config.addDefault("unjail.world", this.getServer().getWorlds().get(0).getName());
        config.addDefault("unjail.x", 0);
        config.addDefault("unjail.y", 0);
        config.addDefault("unjail.z", 0);
        
        jailed = new YamlConfiguration();
        File f = new File(this.getDataFolder().getPath() + File.separator + "jailed.yml");
        
        try {
            if(!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            jailed.load(f);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        jailLoc = new Location(
                this.getServer().getWorld(config.getString("jail.world", this.getServer().getWorlds().get(0).getName())),
                config.getInt("jail.x", 0),
                config.getInt("jail.y", 0),
                config.getInt("jail.z", 0));
        unjailLoc = new Location(
                this.getServer().getWorld(config.getString("unjail.world", this.getServer().getWorlds().get(0).getName())),
                config.getInt("unjail.x", 0),
                config.getInt("unjail.y", 0),
                config.getInt("unjail.z", 0));
        jailGroup = config.getString("jailgroup", "Jailed");
        
        this.saveConfig();
        
    }
    
    public void setupPermissions() {
        
        Plugin bukkit = this.getServer().getPluginManager().getPlugin("PermissionsBukkit");
        Plugin pex = this.getServer().getPluginManager().getPlugin("PermissionsEx");
        
        boolean permissionsLoaded = false;
        
        if(bukkitPermissions == null){
           if(bukkit != null){
               bukkitPermissions = (PermissionsPlugin)bukkit;
               permissionsLoaded = true;
           }
        }
        if (pexPermissions == null) {
            if (pex != null) {
                pexPermissions = PermissionsEx.getPermissionManager();
                permissionsLoaded = true;
            }
        }
        
        if(!permissionsLoaded) {
            JailMessage.PERMISSIONS_NOT_FOUND.print();
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }
    
    public Location getJailLocation() {
        return jailLoc;
    }
    
    public Location getUnjailLocation() {
        return unjailLoc;
    }
    
    public boolean playerIsJailed(Player player) {
        if (jailed.get(player.getName().toLowerCase()) != null)
            return true;
        return false;
    }
    
    public boolean playerIsTempJailed(Player player) {
        if (jailed.get(player.getName().toLowerCase() + ".tempTime") != null)
            return true;
        return false;
    }
    
    public double getTempJailTime(Player player) {
        return jailed.getDouble(player.getName().toLowerCase() + ".tempTime", -1);
    }
    
    public JailStatus getPlayerStatus(Player player) {
        return this.getPlayerStatus(player.getName());
    }
    
    public JailStatus getPlayerStatus(String player) {
        return JailStatus.valueOf(jailed.getString(player.toLowerCase() + ".status", "jailed").toUpperCase());
    }
    
    public void setPlayerStatus (Player player, JailStatus status) {
        this.setPlayerStatus(player.getName(), status);
    }
    
    public void setPlayerStatus(String player, JailStatus status) {
        jailed.set(player.toLowerCase() + ".status", status);
    }
    
    public boolean hasPermission(CommandSender sender, String permission) {
        if (sender instanceof Player)
            return sender.hasPermission(permission);
        else return true;
    }
    
    public List<String> getGroups(String player) {
        if(bukkitPermissions != null) {
            List<Group> groups = bukkitPermissions.getGroups(player);
            List<String> stringGroups = new ArrayList<String>();
            for (Group g : groups) {
                stringGroups.add(g.getName());
            }
            return stringGroups;
        } else if (pexPermissions != null) {
            PermissionGroup[] groups = pexPermissions.getUser(player).getGroups();
            List<String> stringGroups = new ArrayList<String>();
            for (PermissionGroup g : groups) {
                stringGroups.add(g.getName());
            }
            return stringGroups;
        }
        
        return null;
    }
    
    public void setGroup(String player, String group) {
        if (bukkitPermissions != null)
            this.getServer().dispatchCommand(console, "permissions player setgroup " + player + " " + group);
        else if(pexPermissions != null)
            pexPermissions.getUser(player).setGroups(new String[] { group });
    }
    
    public void setGroup(String player, List<String> group) {
        if (bukkitPermissions != null) {
            String params = new String();
            for (String grp : group) {
                params += grp + ",";
            }
            this.getServer().dispatchCommand(console, "permissions player setgroup " + player + " " + params);
        } else if(pexPermissions != null) {
            pexPermissions.getUser(player).setGroups(group.toArray(new String[0]));
        }
    }
    
    public String prettifyMinutes(int minutes) {
        if (minutes == 1) return "one minute";
        if (minutes < 60) return minutes + " minutes";
        if (minutes % 60 == 0) {
            if(minutes / 60 == 1) return "one hour";
            else return (minutes / 60) + " hours";
        }
        int m = minutes % 60;
        int h = (minutes - m) / 60;
        return h + "h" + m + "m";
    }
    
    public int parseTimeString(String time) {
        if(!time.matches("[0-9]*h?[0-9]*m?")) return -1;
        if(time.matches("[0-9]+")) return Integer.parseInt(time);
        if(time.matches("[0-9]+m")) return Integer.parseInt(time.split("m")[0]);
        if(time.matches("[0-9]+h")) return Integer.parseInt(time.split("h")[0]) * 60;
        if(time.matches("[0-9]+h[0-9]+m")) {
            String[] split = time.split("[mh]");
            return (Integer.parseInt(split[0]) * 60) + Integer.parseInt(split[1]);
        }
        return -1;
    }
    
    public void saveJail() {
        try {
            jailed.save(new File(this.getDataFolder().getPath() + File.separator + "jailed.yml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public enum JailStatus {
        JAILED,
        PENDING,
        FREED;
        
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
    
    public enum JailMessage {
        PERMISSIONS_NOT_FOUND ("ERROR: Could not find permissions plugin."),
        LACKS_PERMISSIONS (ChatColor.RED + "You don't have permission to use that command (%1)."),
        ONLY_PLAYERS (ChatColor.RED + "Only players can use that."),
        JAIL (ChatColor.AQUA + "Player '%1' sent to jail."),
        UNJAIL (ChatColor.AQUA + "Player '%1' removed from jail."),
        TEMPJAIL (ChatColor.AQUA + "Player '%1' jailed for %2."),
        JAILED (ChatColor.AQUA + "You have been jailed!"),
        UNJAILED (ChatColor.AQUA + "You have been removed from jail."),
        TEMPJAILED (ChatColor.AQUA + "You have been jailed for %1."),
        UNTEMPJAILED ("Player '%1' auto-unjailed."),
        ALREADY_IN_JAIL (ChatColor.RED + "Player '%1' is already in jail!"),
        NOT_IN_JAIL (ChatColor.RED + "Player '%1' is not in jail!"),
        NOT_TEMPJAILED (ChatColor.RED + "Player '%1' is not tempjailed."),
        JAIL_POINT_SET (ChatColor.AQUA + "Jail point set."),
        UNJAIL_POINT_SET (ChatColor.AQUA + "Unjail point set."),
        INVALID_COORDINATE (ChatColor.RED + "Invalid coordinate."),
        MUST_SPECIFY_TARGET (ChatColor.RED + "You must specify a player."),
        PLAYER_NOT_FOUND (ChatColor.RED + "Couldn't find player '%1'."),
        JAIL_TIME (ChatColor.AQUA + "Remaining jail time: %1."),
        PLAYER_IS_JAILED(ChatColor.AQUA + "You are jailed.");
        
        private String format;
        
        JailMessage(String format) {
            this.format = format;
        }
        
        /**
         * Sends a message.
         * 
         * @param sender reciever
         */
        void send(CommandSender sender) {
            sender.sendMessage(format);
        }
        
        /**
         * Sends a message with arguments.
         * 
         * @param sender reciever
         * @param args list of arguments
         */
        void send(CommandSender sender, String... args) {
            String message = format;
            for(int i = 1; ; i++) {
                if (message.indexOf("%" + i) > 0) {
                    message = message.replaceAll("%" + i, args[i - 1]);
                } else break;
            }
            sender.sendMessage(message);
        }
        
        /**
         * Prints a message prefixed with [SimpleJail] to the console.
         */
        void print() {
            log.info("[SimpleJail] " + format);
        }
        
        
        /**
         * Prints a message with arguments prefixed with [SimpleJail] to the console.
         * 
         * @param args 
         */
        void print(String... args) {
            String message = format;
            for(int i = 1; ; i++) {
                if (message.indexOf("%" + i) > 0) {
                    message = message.replaceAll("%" + i, args[i - 1]);
                } else break;
            }
            log.info("[SimpleJail] " + message);
        }
    }
}
