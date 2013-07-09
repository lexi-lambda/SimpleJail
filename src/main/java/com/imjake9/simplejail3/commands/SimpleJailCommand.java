package com.imjake9.simplejail3.commands;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.CommandSender;


public abstract class SimpleJailCommand {
    
    /**
     * Parses a time from a user-inputted string, in milliseconds.
     * @param s formatted time
     * @return milliseconds, or -1 on failure
     */
    public static long timeFromString(String s) {
        Pattern p = Pattern.compile("(?:(\\d+)w)?(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");
        Matcher m = p.matcher(s);
        if (s.isEmpty() || !m.matches()) return -1;
        
        long t = 0;
        if (m.group(1) != null) t += Integer.parseInt(m.group(1)) * 604800000;
        if (m.group(2) != null) t += Integer.parseInt(m.group(2)) * 86400000;
        if (m.group(3) != null) t += Integer.parseInt(m.group(3)) * 3600000;
        if (m.group(4) != null) t += Integer.parseInt(m.group(4)) * 60000;
        if (m.group(5) != null) t += Integer.parseInt(m.group(5)) * 1000;
        return t;
    }
    
    /**
     * Returns a formatted time string from a number of milliseconds.
     * @param t milliseconds
     * @return formatted time
     */
    public static String stringFromTime(long t) {
        if (t <= 0) return "0s";
        
        long weeks = t / 604800000;
        long days = t % 604800000 / 86400000;
        long hours = t % 86400000 / 3600000;
        long minutes = t % 3600000 / 60000;
        long seconds = t % 60000 / 1000;
        
        String s = "";
        if (weeks > 0) s += weeks + "w";
        if (days > 0) s += days + "d";
        if (hours > 0) s += hours + "h";
        if (minutes > 0) s += minutes + "m";
        if (seconds > 0) s += seconds + "s";
        
        return s;
    }
    
    public abstract boolean handle(CommandSender sender, Map<String, String> flags, List<String> args);
    
}
