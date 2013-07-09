package com.imjake9.simplejail3.api;

import com.imjake9.simplejail3.SimpleJail;
import com.imjake9.simplejail3.commands.SimpleJailCommand;
import com.imjake9.simplejail3.utils.Messaging;
import com.imjake9.simplejail3.utils.SerializableLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;


public class SimpleJailAPI {
    
    public Location getJailLocation() {
        if (SimpleJail.getInstance().getData().getData().get("jail-location") == null)
            setJailLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
        return ((SerializableLocation) (SimpleJail.getInstance().getData().getData().get("jail-location"))).getLocation();
    }
    
    public void setJailLocation(Location location) {
        SimpleJail.getInstance().getData().getData().set("jail-location", new SerializableLocation(location));
        SimpleJail.getInstance().getData().save();
    }
    
    public Location getUnjailLocation() {
        if (SimpleJail.getInstance().getData().getData().get("unjail-location") == null)
            setUnjailLocation(new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
        return ((SerializableLocation) (SimpleJail.getInstance().getData().getData().get("unjail-location"))).getLocation();
    }
    
    public void setUnjailLocation(Location location) {
        SimpleJail.getInstance().getData().getData().set("unjail-location", new SerializableLocation(location));
        SimpleJail.getInstance().getData().save();
    }
    
    public List<String> getJailGroups() {
        if (SimpleJail.getInstance().getConfiguration().getData().get("jail-groups") == null)
            return new ArrayList<String>();
        return SimpleJail.getInstance().getConfiguration().getData().getStringList("jail-groups");
    }
    
    public Set<String> getJailedPlayers() {
        if (SimpleJail.getInstance().getData().getData().getConfigurationSection("status") == null)
            return new HashSet<String>();
        return SimpleJail.getInstance().getData().getData().getConfigurationSection("status").getKeys(false);
    }
    
    public Set<String> getTempjailedPlayers() {
        Set<String> jailed = getJailedPlayers();
        for (Iterator<String> it = jailed.iterator(); it.hasNext();) {
            String playerName = it.next();
            if (!isPlayerTempjailed(playerName))
                it.remove();
        }
        return jailed;
    }
    
    private ConfigurationSection getPlayerData(String playerName) {
        return SimpleJail.getInstance().getData().getData().getConfigurationSection("status." + playerName.toLowerCase());
    }
    
    private Object getPlayerData(String playerName, String path, Object def) {
        ConfigurationSection section = getPlayerData(playerName);
        if (section == null) return def;
        return section.get(path, def);
    }
    
    private long getPlayerDataLong(String playerName, String path, long def) {
        ConfigurationSection section = getPlayerData(playerName);
        if (section == null) return def;
        return section.getLong(path, def);
    }
    
    private Object getPlayerData(String playerName, String path) {
        return getPlayerData(playerName, path, null);
    }
    
    private void setPlayerData(String playerName, String path, Object data) {
        ConfigurationSection section = getPlayerData(playerName);
        if (section == null) section = SimpleJail.getInstance().getData().getData().createSection("status." + playerName.toLowerCase());
        section.set(path, data);
        SimpleJail.getInstance().getData().save();
    }
    
    public boolean isPlayerJailed(String playerName) {
        if (getPlayerData(playerName) == null) return false;
        return getPlayerStatus(playerName).equals("JAILED") || getPlayerStatus(playerName).equals("PENDING-JAILED");
    }
    
    public boolean isPlayerTempjailed(String playerName) {
        return isPlayerJailed(playerName) && getPlayerJailTime(playerName) != -1;
    }
    
    public void deletePlayerData(String playerName) {
        SimpleJail.getInstance().getData().getData().set("status." + playerName.toLowerCase(), null);
        SimpleJail.getInstance().getData().save();
    }
    
    public String getPlayerStatus(String playerName) {
        return (String) getPlayerData(playerName, "status");
    }
    
    public void setPlayerStatus(String playerName, String status) {
        setPlayerData(playerName, "status", status);
    }
    
    public long getPlayerJailTime(String playerName) {
        return getPlayerDataLong(playerName, "time", -1);
    }
    
    public void setPlayerJailTime(String playerName, long time) {
        setPlayerData(playerName, "time", time);
    }
    
    public List<String> getPlayerGroups(String playerName) {
        return (List<String>) getPlayerData(playerName, "group");
    }
    
    public void setPlayerGroups(String playerName, List<String> groups) {
        setPlayerData(playerName, "group", groups);
    }
    
    public void handlePlayerStatus(Player player) {
        String playerName = player.getName().toLowerCase();
        
        if (getPlayerData(playerName) == null) return;
        String status = getPlayerStatus(playerName);
        long time = getPlayerJailTime(playerName);
        
        if (status.equals("PENDING-JAILED")) {
            if (SimpleJail.hasPermissions()) {
                Permission permissions = SimpleJail.getPermissions();
                setPlayerGroups(playerName, Arrays.asList(permissions.getPlayerGroups(player)));
                for (String group : permissions.getPlayerGroups(player)) {
                    permissions.playerRemoveGroup(player, group);
                }
                for (String group : getJailGroups()) {
                    permissions.playerAddGroup(player, group);
                }
            }
            setPlayerStatus(playerName, "JAILED");
            handlePlayerStatus(player);
            return;
        }
        
        if (status.equals("JAILED")) {
            if (time == -1) {
                Messaging.send(player, Messaging.SimpleJailMessage.PLAYER_IN_JAIL);
            } else {
                if (System.currentTimeMillis() > time) {
                    setPlayerJailTime(playerName, time);
                    handlePlayerStatus(player);
                    return;
                }
                Messaging.send(player, Messaging.SimpleJailMessage.PLAYER_IN_TEMPJAIL, SimpleJailCommand.stringFromTime(time - System.currentTimeMillis()));
            }
            player.teleport(getJailLocation());
            return;
        }
        
        if (status.equals("PENDING-UNJAILED")) {
            if (SimpleJail.hasPermissions()) {
                Permission permissions = SimpleJail.getPermissions();
                for (String group : permissions.getPlayerGroups(player)) {
                    permissions.playerRemoveGroup(player, group);
                }
                for (String group : getPlayerGroups(playerName)) {
                    permissions.playerAddGroup(player, group);
                }
            }
            setPlayerStatus(playerName, "UNJAILED");
            handlePlayerStatus(player);
            return;
        }
        
        if (status.equals("UNJAILED")) {
            player.teleport(getUnjailLocation());
            deletePlayerData(playerName);
            Messaging.send(player, Messaging.SimpleJailMessage.PLAYER_IN_UNJAIL);
            return;
        }
    }
    
}
