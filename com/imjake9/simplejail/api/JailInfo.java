package com.imjake9.simplejail.api;

import java.util.HashMap;
import java.util.Map;


public class JailInfo {
    
    protected String jailer;
    protected String jailee;
    protected JailType type;
    
    protected Map<String, Object> jailData;
    
    public JailInfo(String jailer, String jailee, JailType type) {
        this.jailer = jailer;
        this.jailee = jailee;
        this.type = type;
        jailData = new HashMap<String, Object>();
    }
    
    public String getJailer() {
        return jailer;
    }
    
    public void setJailer(String jailer) {
        this.jailer = jailer;
    }
    
    public String getJailee() {
        return jailee;
    }
    
    public void setJailee(String jailee) {
        this.jailee = jailee;
    }
    
    public JailType getType() {
        return type;
    }
    
    public void setType(JailType type) {
        this.type = type;
    }
    
    public Object getProperty(String key) {
        return jailData.get(key);
    }
    
    public String getString(String key) {
        return (String) jailData.get(key);
    }
    
    public Integer getInteger(String key) {
        return (Integer) jailData.get(key);
    }
    
    public void setProperty(String key, Object value) {
        jailData.put(key, value);
    }
    
    public static enum SimpleJailType implements JailType {
        JAIL,
        UNJAIL;
        
        @Override
        public String getName() {
            return this.name();
        }
    }

}
