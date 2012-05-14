package com.imjake9.simplejail.api;

import java.util.HashMap;
import java.util.Map;


public class JailInfo {
    
    protected String jailer;
    protected String jailee;
    
    protected Map<String, Object> jailData;
    
    public JailInfo(String jailer, String jailee) {
        this(jailer, jailee, new HashMap<String, Object>());
    }
    
    public JailInfo(String jailer, String jailee, Map<String,Object> jailData) {
        this.jailer = jailer;
        this.jailee = jailee;
        this.jailData = jailData;
    }
    
    /**
     * Gets the name of the player who issued the jail command.
     * 
     * @return jailer
     */
    public String getJailer() {
        return jailer;
    }
    
    /**
     * Sets the name of the player who issued the jail command.
     * 
     * @param jailer 
     */
    public void setJailer(String jailer) {
        this.jailer = jailer;
    }
    
    /**
     * Gets the name of the player who was jailed.
     * 
     * @return jailee
     */
    public String getJailee() {
        return jailee;
    }
    
    /**
     * Sets the name of the player who was jailed.
     * 
     * Changing this does not change who will actually be jailed.
     * 
     * @param jailee 
     */
    public void setJailee(String jailee) {
        this.jailee = jailee;
    }
    
    /**
     * Gets a map of all custom jail properties.
     * 
     * @return values
     */
    public Map<String, Object> getProperties() {
        return jailData;
    }
    
    /**
     * Gets a custom jail property.
     * 
     * @param key
     * @return value
     */
    public Object getProperty(String key) {
        return jailData.get(key);
    }
    
    /**
     * Adds all passed properties to the custom jail properties.
     * 
     * @param properties 
     */
    public void addProperties(Map<String, Object> properties) {
        jailData.putAll(properties);
    }
    
    /**
     * Gets a custom jail property as a String.
     * 
     * @param key
     * @return value
     */
    public String getString(String key) {
        return (String) jailData.get(key);
    }
    
    /**
     * Gets a custom jail property as an Integer.
     * 
     * @param key
     * @return value
     */
    public Integer getInteger(String key) {
        return (Integer) jailData.get(key);
    }
    
    /**
     * Sets a custom jail property.
     * 
     * @param key
     * @param value 
     */
    public void setProperty(String key, Object value) {
        jailData.put(key, value);
    }

}
