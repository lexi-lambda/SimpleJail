package com.imjake9.simplejail3.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Messaging {
    
    public static interface MessageTemplate {
        String getFormat(String... args);
        MessageLevel getLevel();
    }
    
    public static enum SimpleJailMessage implements MessageTemplate {
        INVALID_PARAMETER(MessageLevel.ERROR, "Parameter <i>%1</i> cannot be <i>%2</i>."),
        JAIL_LOCATION_SET(MessageLevel.SUCCESS, "Jail location set."),
        LACKS_PERMISSION(MessageLevel.ERROR, "You don't have permission to do that. (%1)"),
        MISSING_PARAMETER(MessageLevel.ERROR, "Missing parameter <i>%1</i>."),
        ONLY_PLAYERS(MessageLevel.ERROR, "Only players can use that."),
        PLAYER_ALREADY_JAILED(MessageLevel.ERROR, "Player <i>%1</i> is already in jail."),
        PLAYER_IN_JAIL(MessageLevel.ERROR, "You're in jail!"),
        PLAYER_IN_TEMPJAIL(MessageLevel.ERROR, "You're in jail for <b>%1</b>!"),
        PLAYER_IN_UNJAIL(MessageLevel.INFO, "You've been removed from jail."),
        PLAYER_JAILED(MessageLevel.SUCCESS, "Player <i>%1</i> sent to jail."),
        PLAYER_NOT_JAILED(MessageLevel.ERROR, "Player <i>%1</i> isn't in jail."),
        PLAYER_NOT_TEMPJAILED(MessageLevel.ERROR, "Player <i>%1</i> isn't tempjailed."),
        PLAYER_TEMPJAILED(MessageLevel.SUCCESS, "Player <i>%1</i> sent to jail for <i>%2</i>."),
        PLAYER_UNJAILED(MessageLevel.SUCCESS, "Player <i>%1</i> removed from jail."),
        TEMPJAIL_TIME(MessageLevel.INFO, "Player <i>%1</i> is jailed for <b>%2</b>."),
        UNJAIL_LOCATION_SET(MessageLevel.SUCCESS, "Unjail location set.");
        
        private MessageLevel level;
        private String format;
        
        private SimpleJailMessage(String format) {
            this(MessageLevel.NORMAL, format);
        }
        
        private SimpleJailMessage(MessageLevel level, String format) {
            this.level = level;
            this.format = format;
        }
        
        @Override
        public String getFormat(String... args) {
            return format;
        }
        
        @Override
        public MessageLevel getLevel() {
            return level;
        }
    }
    
    public static enum MessageLevel {
        NORMAL(ChatColor.WHITE),
        INFO(ChatColor.AQUA),
        ERROR(ChatColor.RED),
        SUCCESS(ChatColor.GREEN),
        COMPLETE(ChatColor.DARK_AQUA);
        
        private ChatColor prefix;
        
        private MessageLevel(ChatColor prefix) {
            this.prefix = prefix;
        }
        
        public ChatColor getPrefix() {
            return prefix;
        }
        
        public String getOpeningTag() {
            return "<" + getPrefix().name().toLowerCase() + ">";
        }
        
        public String getClosingTag() {
            return "</" + getPrefix().name().toLowerCase() + ">";
        }
    }
    
    private static final Logger logger = Bukkit.getLogger();
    
    public static void info(String message) {
        logger.info("[SimpleJail] " + message);
    }
    public static void info(MessageTemplate message, String... args) {
        info(format(message, args));
    }
    
    public static void warning(String message) {
        logger.warning("[SimpleJail] " + message);
    }
    public static void warning(MessageTemplate message, String... args) {
        warning(format(message, args));
    }
    
    public static void severe(String message) {
        logger.severe("[SimpleJail] " + message);
    }
    public static void severe(MessageTemplate message, String... args) {
        severe(format(message, args));
    }
    
    public static void send(CommandSender sender, MessageTemplate message, String... args) {
        sender.sendMessage(format(message, args));
    }
    public static void broadcast(MessageTemplate message, String... args) {
        Bukkit.broadcastMessage(format(message, args));
    }
    public static void broadcastAs(Player player, MessageTemplate message, String... args) {
        player.chat(format(message, args));
    }
    
    public static String format(MessageTemplate message, String... args) {
        return parseFormatString(message.getLevel().getOpeningTag() + message.getFormat(args) + message.getLevel().getClosingTag(), args);
    }
    
    /**
     * Takes a special format string and fills in arguments, then resolves
     * HTML-esque tags to ChatColor patterns. Intended for use with
     * MessageTemplate formats.
     * @param format
     * @param args
     * @return parsed string
     */
    private static String parseFormatString(String format, String... args) {
        // escape all uninteresting % signs
        format = format.replaceAll("%(?=\\D)", "%%");
        // fill all arguments (%n)
        Pattern argsPattern = Pattern.compile("(?<!%)%(\\d+)");
        Matcher argsMatcher;
        while ((argsMatcher = argsPattern.matcher(format)).find()) {
            int num = Integer.parseInt(argsMatcher.group(1));
            // replace and escape all % characters
            format = format.replace("%" + num, args[num - 1].replace("%", "%%"));
        }
        // unescape the % character (Java is smart enough to do this correctly)
        format = format.replace("%%", "%");
        
        // parse HTML-esque tags into ChatColors
        // keep track of existing styles (we have to handle format codes and colors separately)
        List<ChatColor> colors = new LinkedList<ChatColor>();
        List<ChatColor> formats = new LinkedList<ChatColor>();
        while (format.contains("<") && format.contains(">")) {
            // get actual style name
            String tag = format.substring(format.indexOf("<") + 1, format.indexOf(">")).toLowerCase();
            // check if it's closing or not
            boolean closing = false;
            if (tag.charAt(0) == '/') {
                closing = true;
                tag = tag.substring(1);
            }
            // get actual style
            ChatColor style;
            boolean isColor = false;
            if (tag.equals("b")) {
                style = ChatColor.BOLD;
            } else if (tag.equals("i")) {
                style = ChatColor.ITALIC;
            } else if (tag.equals("u")) {
                style = ChatColor.UNDERLINE;
            } else if (tag.equals("s")) {
                style = ChatColor.STRIKETHROUGH;
            } else {
                isColor = true;
                try {
                    style = ChatColor.valueOf(tag.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Invalid message format tag '" + tag + "'.", ex);
                }
            }
            // insert style
            String styleString;
            if (!closing) {
                // append new style
                (isColor ? colors : formats).add(style);
                styleString = style.toString();
                // if it's a color, we need to re-initialize the formats (they are replaced otherwise)
                if (isColor) {
                    for (ChatColor cc : formats) {
                        styleString += cc.toString();
                    }
                }
            } else {
                // pop off style and re-add old ones for simplicity
                (isColor ? colors : formats).remove((isColor ? colors : formats).lastIndexOf(style));
                styleString = ChatColor.RESET.toString();
                for (ChatColor cc : colors) {
                    styleString += cc.toString();
                }
                for (ChatColor cc : formats) {
                    styleString += cc.toString();
                }
            }
            // insert it into the message
            format = format.substring(0, format.indexOf("<")) + styleString + format.substring(format.indexOf(">") + 1);
        }
        // replace HTML-esque entities
        format = format.replace("&lt;", "<");
        format = format.replace("&gt;", ">");
        return format;
    }
    
}
