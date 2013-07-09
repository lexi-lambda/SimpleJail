package com.imjake9.simplejail3.commands;

import com.imjake9.simplejail3.SimpleJail;
import com.imjake9.simplejail3.api.SimpleJailAPI;
import com.imjake9.simplejail3.utils.Messaging;
import com.imjake9.simplejail3.utils.Messaging.SimpleJailMessage;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class JailTimeCommand extends SimpleJailCommand {
    
    @Override
    public boolean handle(CommandSender sender, Map<String, String> flags, List<String> args) {
        SimpleJailAPI api = SimpleJail.getAPI();
        String playerName;
        if (args.isEmpty()) {
            if (!(sender instanceof Player)) {
                Messaging.send(sender, SimpleJailMessage.MISSING_PARAMETER, "player");
                return false;
            }
            playerName = ((Player) sender).getName();
        } else {
            playerName = args.get(0);
        }
        if (!api.isPlayerTempjailed(playerName)) {
            Messaging.send(sender, SimpleJailMessage.PLAYER_NOT_TEMPJAILED, playerName);
            return true;
        }
        
        long time = api.getPlayerJailTime(playerName) - System.currentTimeMillis();
        Messaging.send(sender, SimpleJailMessage.TEMPJAIL_TIME, playerName, SimpleJailCommand.stringFromTime(time));
        return true;
    }
    
}
