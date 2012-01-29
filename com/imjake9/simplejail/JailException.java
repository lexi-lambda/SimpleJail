package com.imjake9.simplejail;

public class JailException extends Exception {
    
    private final String formattedMessage;
    
    public JailException(String message) {
        super(message);
        formattedMessage = message;
    }
    
    public JailException(String message, String formattedMessage) {
        super(message);
        this.formattedMessage = formattedMessage;
    }
    
    /**
     * Gets a message meant to be sent to a player.
     * 
     * @return 
     */
    public String getFormattedMessage() {
        return formattedMessage;
    }
    
}
