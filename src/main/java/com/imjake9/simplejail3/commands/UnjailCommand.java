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


public class UnjailCommand extends SimpleJailCommand {
    
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
        if (!api.isPlayerJailed(playerName)) {
            Messaging.send(sender, SimpleJailMessage.PLAYER_NOT_JAILED, playerName);
            return true;
        }
        
        api.setPlayerStatus(playerName, "PENDING-UNJAILED");
        if (player != null) api.handlePlayerStatus(player);
        
        Messaging.send(sender, SimpleJailMessage.PLAYER_UNJAILED, playerName);
        return true;
    }
    
}
