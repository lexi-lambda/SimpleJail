package com.imjake9.simplejail3.commands;

import com.imjake9.simplejail3.SimpleJail;
import com.imjake9.simplejail3.api.SimpleJailAPI;
import com.imjake9.simplejail3.utils.Messaging;
import com.imjake9.simplejail3.utils.Messaging.SimpleJailMessage;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JailCommand extends SimpleJailCommand {
    
    @Override
    public boolean handle(CommandSender sender, Map<String, String> flags, List<String> args) {
        SimpleJailAPI api = SimpleJail.getAPI();
        if (args.isEmpty()) {
            Messaging.send(sender, SimpleJailMessage.MISSING_PARAMETER, "player");
            return false;
        }
        String playerName = args.get(0).toLowerCase();
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) playerName = player.getName().toLowerCase();
        if (api.isPlayerJailed(playerName)) {
            Messaging.send(sender, SimpleJailMessage.PLAYER_ALREADY_JAILED, playerName);
            return true;
        }
        
        long time = -1;
        if (args.size() >= 2) {
            time = SimpleJailCommand.timeFromString(args.get(1).toLowerCase());
            if (time == -1) {
                Messaging.send(sender, SimpleJailMessage.INVALID_PARAMETER, "time", args.get(1));
                return false;
            }
        }
        
        api.setPlayerStatus(playerName, "PENDING-JAILED");
        if (time != -1) api.setPlayerJailTime(playerName, System.currentTimeMillis() + time);
        if (player != null) api.handlePlayerStatus(player);
        
        if (time == -1) {
            Messaging.send(sender, SimpleJailMessage.PLAYER_JAILED, playerName);
        } else {
            Messaging.send(sender, SimpleJailMessage.PLAYER_TEMPJAILED, playerName, SimpleJailCommand.stringFromTime(time));
        }
        return true;
    }
    
}
