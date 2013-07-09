package com.imjake9.simplejail3.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class SerializableLocation implements ConfigurationSerializable {
    
    private final Location location;
    
    public SerializableLocation(Location location) {
        this.location = location;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public static SerializableLocation deserialize(Map<String, Object> data) {
        return new SerializableLocation(new Location(
                Bukkit.getWorld(UUID.fromString((String) data.get("world"))),
                (Double) data.get("x"), (Double) data.get("y"), (Double) data.get("z"),
                ((Double) data.get("yaw")).floatValue(), ((Double) data.get("pitch")).floatValue()));
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("world", location.getWorld().getUID().toString());
        data.put("x", location.getX());
        data.put("y", location.getY());
        data.put("z", location.getZ());
        data.put("yaw", location.getYaw());
        data.put("pitch", location.getPitch());
        return data;
    }
    
}
