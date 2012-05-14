package com.imjake9.simplejail.utils;

import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;


public class Messager {
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    private final Plugin plugin;
    
    public Messager(Plugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Logs a message with the plugin's name.
     * 
     * @param message 
     */
    public void info(String message) {
        log.info("[" + plugin.getDescription().getName() + "]" + message);
    }
    
    /**
     * Logs a message with the plugin's name.
     * 
     * @param template
     * @param args 
     */
    public void info(MessageTemplate template, String... args) {
        info(Messaging.fillArgs(template, args));
    }
    
    /**
     * Logs a message with the plugin's name.
     * 
     * @param message 
     */
    public void warning(String message) {
        log.warning("[" + plugin.getDescription().getName() + "]" + message);
    }
    
    /**
     * Logs a message with the plugin's name.
     * 
     * @param template
     * @param args 
     */
    public void warning(MessageTemplate template, String... args) {
        warning(Messaging.fillArgs(template, args));
    }
    
    /**
     * Logs a message with the plugin's name.
     * 
     * @param message 
     */
    public void severe(String message) {
        log.severe("[" + plugin.getDescription().getName() + "]" + message);
    }
    
    /**
     * Logs a message with the plugin's name.
     * 
     * @param template
     * @param args 
     */
    public void severe(MessageTemplate template, String... args) {
        severe(Messaging.fillArgs(template, args));
    }

}
