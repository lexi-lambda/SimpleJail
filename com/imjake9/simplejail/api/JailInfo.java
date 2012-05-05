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
    
    public Map<String, Object> getProperties() {
        return jailData;
    }
    
    public Object getProperty(String key) {
        return jailData.get(key);
    }
    
    public void addProperties(Map<String, Object> properties) {
        jailData.putAll(properties);
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

}
