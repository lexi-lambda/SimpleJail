package com.imjake9.simplejail3.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public class ConfigurationFile {
    
    private File file;
    private YamlConfiguration config;
    
    public ConfigurationFile(File file) {
        this.file = file;
        load();
    }
    
    public ConfigurationFile(Plugin plugin, String name) {
        this(new File(plugin.getDataFolder(), name));
    }
    
    private void load() {
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            if (!file.exists()) {
                InputStream defaultStream = getClass().getClassLoader().getResourceAsStream(file.getName());
                if (defaultStream != null) {
                    OutputStream outputStream = new FileOutputStream(file);
                    IOUtils.copy(defaultStream, outputStream);
                    outputStream.close();
                    defaultStream.close();
                } else {
                    file.createNewFile();
                }
            }
            
            config = new YamlConfiguration();
            config.load(file);
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[SimpleJail] Error loading configuration file.", ex);
        }
    }
    
    public void reload() {
        load();
    }
    
    public void save() {
        try {
            config.save(file);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[SimpleJail]  Error saving configuration file.", ex);
        }
    }
    
    public YamlConfiguration getData() {
        return config;
    }
    
}
