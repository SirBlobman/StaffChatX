package com.github.sirblobman.staff.chat.common;

public class StaffChatStatus {
    private boolean isEnabled;
    private StaffChatChannel channel;
    
    public StaffChatStatus(boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.channel = null;
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public boolean isInChannel() {
        return (this.channel != null);
    }
    
    public StaffChatChannel getChannel() {
        return this.channel;
    }
    
    public void setChannel(StaffChatChannel channel) {
        this.channel = channel;
    }
    
    public void setStatus(boolean enabled) {
        this.isEnabled = enabled;
    }
}