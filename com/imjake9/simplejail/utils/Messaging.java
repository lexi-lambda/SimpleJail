package com.imjake9.simplejail.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messaging {
    
    private static final Logger log = Logger.getLogger("Minecraft");
    
    /**
     * Logs a message to the console with the INFO and JSE prefixes.
     * 
     * @param message 
     */
    public static void info(String message) {
        log.info("[JServer] " + message);
    }
    
    /**
     * Logs a message to the console with the INFO and JSE prefixes.
     * 
     * @param message
     * @param args 
     */
    public static void info(MessageTemplate message, String... args) {
        info(fillArgs(message, args));
    }
    
    /**
     * Logs a message to the console with the WARNING and JSE prefixes.
     * 
     * @param message 
     */
    public static void warning(String message) {
        log.warning("[JServer] " + message);
    }
    
    /**
     * Logs a message to the console with the WARNING and JSE prefixes.
     * 
     * @param message
     * @param args 
     */
    public static void warning(MessageTemplate message, String... args) {
        warning(fillArgs(message, args));
    }
    
    /**
     * Logs a message to the console with the SEVERE and JSE prefixes.
     * 
     * @param message 
     */
    public static void severe(String message) {
        log.severe("[JServer] " + message);
    }
    
    /**
     * Logs a message to the console with the SEVERE and JSE prefixes.
     * 
     * @param message
     * @param args 
     */
    public static void severe(MessageTemplate message, String... args) {
        severe(fillArgs(message, args));
    }
    
    /**
     * Sends a message to a player.
     * 
     * @param message
     * @param sender
     * @param args 
     */
    public static void send(MessageTemplate message, CommandSender sender, String... args) {
        sender.sendMessage(fillArgs(message, args));
    }
    
    /**
     * Broadcasts a message to the server.
     * 
     * @param message
     * @param args 
     */
    public static void broadcast(MessageTemplate message, String... args) {
        Bukkit.broadcastMessage(fillArgs(message, args));
    }
    
    /**
     * Broadcasts a message as if it were chatted by a player.
     * 
     * @param message
     * @param player
     * @param args 
     */
    public static void broadcastAs(MessageTemplate message, Player sender, String... args) {
        sender.chat(fillArgs(message, args));
    }
    
    /**
     * Parses HTML-style tags into ChatColor formats.
     * 
     * @param message message to parse
     * @return formatted message
     */
    public static String parseStyling(String message) {
        // Keep all current stylings
        List<ChatColor> styles = new ArrayList<ChatColor>();
        // Loop through style tags
        while (message.contains("<") && message.contains(">")) {
            // Get style tag
            String tag = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
            // Get if it's closing or not
            boolean closing = false;
            if (tag.charAt(0) == '/') {
                closing = true;
                tag = tag.substring(1);
            }
            // Get actual style
            ChatColor style = null;
            boolean reset = true;
            if (tag.equalsIgnoreCase("b")) {
                style = ChatColor.BOLD;
            } else if (tag.equalsIgnoreCase("i")) {
                style = ChatColor.ITALIC;
            } else if (tag.equalsIgnoreCase("u")) {
                style = ChatColor.UNDERLINE;
            } else if (ChatColor.valueOf(tag.toUpperCase()) != null) {
                reset = false;
                style = ChatColor.valueOf(tag.toUpperCase());
            }
            // Insert style
            if (style != null) {
                if (!closing) {
                    // Add new style
                    styles.add(style);
                    message = message.substring(0, message.indexOf("<")) + style + message.substring(message.indexOf(">") + 1);
                } else if (styles.contains(style)) {
                    if (reset) {
                        // Pop off style and re-add old ones
                        styles.remove(styles.lastIndexOf(style));
                        String newStyles = ChatColor.RESET.toString();
                        for (ChatColor cc : styles) {
                            newStyles += cc;
                        }
                        message = message.substring(0, message.indexOf("<")) + newStyles + message.substring(message.indexOf(">") + 1);
                    } else {
                        // Find how much needs to be re-printed
                        int chopIndex = 0;
                        for (int i = styles.size() - 1; i >= 0; i--) {
                            ChatColor cc = styles.get(i);
                            if (!cc.equals(ChatColor.BOLD) && !cc.equals(ChatColor.ITALIC) && !cc.equals(ChatColor.UNDERLINE) && !cc.equals(ChatColor.STRIKETHROUGH)) {
                                chopIndex = i;
                                break;
                            }
                        }
                        String newStyles = ChatColor.WHITE.toString();
                        while (styles.size() > chopIndex) {
                            if (styles.size() - chopIndex > 1) {
                                newStyles += styles.get(chopIndex);
                            }
                            styles.remove(chopIndex);
                        }
                        message = message.substring(0, message.indexOf("<")) + newStyles + message.substring(message.indexOf(">") + 1);
                    }
                }
            }
        }
        return message;
    }
    
    /**
     * Fills the arguments of a string.
     * 
     * @param format
     * @param args
     * @return 
     */
    public static String fillArgs(String format, String... args) {
        String message = format;
        for (int i = 1; message.indexOf("%" + i) > 0; i++) {
            message = message.replaceAll("%" + i, args[i - 1]);
        }
        return message;
    }
    
    /**
     * Fills the arguments of a message.
     * 
     * @param format
     * @param args
     * @return 
     */
    public static String fillArgs(MessageTemplate format, String... args) {
        return fillArgs(format.getMessage(), args);
    }
    
    public static enum JSMessage implements MessageTemplate {
        
        INVALID_LOCATION (MessageLevel.ERROR, "That is not a valid location."),
        INVALID_PARAMTER (MessageLevel.ERROR, "Paramter <i>%1</i> cannot be <i>%2</i>."),
        INVALID_PLAYER (MessageLevel.ERROR, "Player <i>%1</i> not found."),
        INVALID_WORLD (MessageLevel.ERROR, "There is no world named <i>%1</i>."),
        LACKS_PERMISSION (MessageLevel.ERROR, "You don't have permission to do that! (%1)"),
        MISSING_PARAMETER (MessageLevel.ERROR, "You must provide the <i>%1</i> parameter."),
        ONLY_PLAYERS (MessageLevel.ERROR, "Only players can use that command.");
        
        private MessageLevel level;
        private String format;
        
        JSMessage(MessageLevel level, String format) {
            this.level = level;
            this.format = parseStyling(level.getOpeningTag() + format + level.getClosingTag());
        }
        
        JSMessage(String format) {
            this(MessageLevel.PLAIN, format);
        }
        
        /**
         * Gets the raw message as a String.
         * 
         * @return message
         */
        @Override
        public String getMessage() {
            return this.format;
        }
        
        /**
         * Gets the message's level.
         * 
         * @return level
         */
        @Override
        public MessageLevel getLevel() {
            return this.level;
        }
        
    }
    
    public static enum MessageLevel {
        ERROR (ChatColor.DARK_RED),
        SUCCESS (ChatColor.GREEN),
        PLAIN (ChatColor.WHITE),
        PRIVATE (ChatColor.GRAY),
        INIT (ChatColor.DARK_GREEN),
        COMPLETE (ChatColor.DARK_AQUA);
        
        private ChatColor prefix;
        
        MessageLevel(ChatColor prefix) {
            this.prefix = prefix;
        }
        
        public ChatColor getPrefix() {
            return this.prefix;
        }
        
        public String getOpeningTag() {
            return "<" + getPrefix().name().toLowerCase() + ">";
        }
        
        public String getClosingTag() {
            return "</" + getPrefix().name().toLowerCase() + ">";
        }
    }
    
}
