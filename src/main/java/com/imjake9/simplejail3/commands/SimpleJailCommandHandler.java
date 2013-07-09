package com.imjake9.simplejail3.commands;

import com.imjake9.simplejail3.SimpleJail;
import com.imjake9.simplejail3.utils.Messaging;
import com.imjake9.simplejail3.utils.Messaging.SimpleJailMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;


public class SimpleJailCommandHandler implements CommandExecutor {
    
    private Map<String, SimpleJailCommand> commands;
    
    public SimpleJailCommandHandler() {
        commands = new HashMap<String, SimpleJailCommand>();
        commands.put("jail",      new JailCommand());
        commands.put("unjail",    new UnjailCommand());
        commands.put("setjail",   new SetJailCommand());
        commands.put("setunjail", new SetUnjailCommand());
        commands.put("jailtime",  new JailTimeCommand());
        for (String s : commands.keySet()) {
            PluginCommand command = SimpleJail.getInstance().getCommand(s);
            command.setExecutor(this);
            command.setPermissionMessage(Messaging.format(SimpleJailMessage.LACKS_PERMISSION, command.getPermission()));
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<String>();
        Map<String, String> flags = new HashMap<String, String>();
        for (String arg : args) {
            if (arg.startsWith("-")) {
                String flag, value;
                if (arg.contains("=")) {
                    flag = arg.substring(1, arg.indexOf("="));
                    value = arg.substring(arg.indexOf("=") + 1);
                } else {
                    flag = arg.substring(1);
                    value = "";
                }
                flags.put(flag, value);
            } else {
                arguments.add(arg);
            }
        }
        return commands.get(command.getName().toLowerCase()).handle(sender, flags, arguments);
    }
    
}
