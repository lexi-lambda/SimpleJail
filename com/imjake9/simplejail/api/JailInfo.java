package com.imjake9.simplejail.api;


public class JailInfo {
    
    protected String jailer;
    protected String jailee;
    protected JailType type;
    
    public JailInfo(String jailer, String jailee, JailType type) {
        this.jailer = jailer;
        this.jailee = jailee;
        this.type = type;
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
    
    public static enum SimpleJailType implements JailType {
        JAIL,
        UNJAIL;
        
        @Override
        public String getName() {
            return this.name();
        }
    }

}
