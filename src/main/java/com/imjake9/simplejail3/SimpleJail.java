package com.imjake9.simplejail3;

import com.imjake9.simplejail3.api.SimpleJailAPI;
import com.imjake9.simplejail3.commands.SimpleJailCommandHandler;
import com.imjake9.simplejail3.listeners.SimpleJailLoginListener;
import com.imjake9.simplejail3.listeners.SimpleJailRespawnListener;
import com.imjake9.simplejail3.utils.ConfigurationFile;
import com.imjake9.simplejail3.utils.SerializableLocation;
import java.io.IOException;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class SimpleJail extends JavaPlugin {
    
    static {
        ConfigurationSerialization.registerClass(SerializableLocation.class);
    }
    
    private static SimpleJail instance;
    private static SimpleJailAPI api;
    private static Permission permission;
    
    public static SimpleJail getInstance() {
        return instance;
    }
    
    public static SimpleJailAPI getAPI() {
        return api;
    }
    
    public static boolean hasPermissions() {
        return permission != null;
    }
    
    public static Permission getPermissions() {
        return permission;
    }
    
    private ConfigurationFile config;
    private ConfigurationFile data;
    
    private SimpleJailCommandHandler commandHandler;
    private SimpleJailLoginListener loginListener;
    private SimpleJailRespawnListener respawnListener;
    
    @Override
    public void onEnable() {
        instance = this;
        api = new SimpleJailAPI();
        
        config = new ConfigurationFile(this, "config.yml");
        data = new ConfigurationFile(this, "data.yml");
        
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        } else {
            Bukkit.getLogger().warning("[SimpleJail] No permissions plugin detected.");
        }
        
        commandHandler = new SimpleJailCommandHandler();
        loginListener = new SimpleJailLoginListener();
        respawnListener = new SimpleJailRespawnListener();
        Bukkit.getPluginManager().registerEvents(loginListener, this);
        Bukkit.getPluginManager().registerEvents(respawnListener, this);
        
        // handle tempjail unjail timing
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (String playerName : api.getTempjailedPlayers()) {
                    if (api.getPlayerJailTime(playerName) < System.currentTimeMillis()) {
                        api.setPlayerStatus(playerName, "PENDING-UNJAILED");
                        Player player = Bukkit.getPlayerExact(playerName);
                        if (player != null) api.handlePlayerStatus(player);
                    }
                }
            }
        }, 600, 600);
        
        // start Metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {}
    }
    
    @Override
    public void onDisable() {
        api = null;
        instance = null;
    }
    
    public ConfigurationFile getConfiguration() {
        return config;
    }
    
    public ConfigurationFile getData() {
        return data;
    }
    
}
