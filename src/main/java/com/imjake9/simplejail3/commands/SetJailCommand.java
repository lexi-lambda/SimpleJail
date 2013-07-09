package com.imjake9.simplejail3.commands;

import com.imjake9.simplejail3.SimpleJail;
import com.imjake9.simplejail3.utils.Messaging;
import com.imjake9.simplejail3.utils.Messaging.SimpleJailMessage;
import com.imjake9.simplejail3.utils.SerializableLocation;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SetJailCommand extends SimpleJailCommand {
    
    @Override
    public boolean handle(CommandSender sender, Map<String, String> flags, List<String> args) {
        if (args.isEmpty()) {
            if (!(sender instanceof Player)) {
                Messaging.send(sender, SimpleJailMessage.ONLY_PLAYERS);
                return false;
            }
            Location location = ((Player) sender).getLocation();
            SimpleJail.getAPI().setJailLocation(new Location(
                    location.getWorld(),
                    location.getBlockX() + 0.5,
                    location.getBlockY(),
                    location.getBlockZ() + 0.5));
            Messaging.send(sender, SimpleJailMessage.JAIL_LOCATION_SET);
            return true;
        }
        if (args.size() >= 3) {
            int x, y, z;
            try {
                x = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ex) {
                Messaging.send(sender, SimpleJailMessage.INVALID_PARAMETER, "x", args.get(0));
                return false;
            }
            try {
                y = Integer.parseInt(args.get(1));
            } catch (NumberFormatException ex) {
                Messaging.send(sender, SimpleJailMessage.INVALID_PARAMETER, "y", args.get(1));
                return false;
            }
            try {
                z = Integer.parseInt(args.get(2));
            } catch (NumberFormatException ex) {
                Messaging.send(sender, SimpleJailMessage.INVALID_PARAMETER, "z", args.get(2));
                return false;
            }
            World world;
            if (args.size() >= 4) {
                world = Bukkit.getWorld(args.get(3));
                if (world == null) {
                    Messaging.send(sender, SimpleJailMessage.INVALID_PARAMETER, "world", args.get(3));
                    return true;
                }
            } else if (!(sender instanceof Player)) {
                Messaging.send(sender, SimpleJailMessage.MISSING_PARAMETER, "world");
                return false;
            } else {
                world = ((Player) sender).getWorld();
            }
            SimpleJail.getAPI().setJailLocation(new Location(world, x, y, z));
            Messaging.send(sender, SimpleJailMessage.JAIL_LOCATION_SET);
            return true;
        }
        return false;
    }
    
}
