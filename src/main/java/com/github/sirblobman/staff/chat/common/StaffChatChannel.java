package com.github.sirblobman.staff.chat.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class StaffChatChannel {    
    private final String channelName, permission, format;
    public StaffChatChannel(String channelName, String permission, String format) {
        this.channelName = channelName;
        this.permission = permission;
        this.format = format;
    }
    
    public abstract boolean hasPermission(UUID uuid);
    
    public final String getName() {
        return this.channelName;
    }
    
    public final String getPermission() {
        return this.permission;
    }
    
    public final String format(String username, String message) {
        String replace = this.format.replace("{username}", username).replace("{message}", message);
        return replace;
    }
    
    @Override
    public final boolean equals(Object o2) {
        if(super.equals(o2)) return true;
        if(!(o2 instanceof StaffChatChannel)) return false;
        
        StaffChatChannel channel01 = this;
        StaffChatChannel channel02 = (StaffChatChannel) o2;
        return (
                channel01.getName().equals(channel02.getName()) && 
                channel01.getPermission().equals(channel02.getPermission()) && 
                channel01.format.equals(channel02.format)
                );
    }
    
    private final List<UUID> disabledPlayers = new ArrayList<>();
    public final void toggle(UUID uuid) {
        if(disabledPlayers.contains(uuid)) disabledPlayers.remove(uuid);
        else disabledPlayers.add(uuid);
    }
    
    public final boolean isDisabled(UUID uuid) {
        return disabledPlayers.contains(uuid);
    }
}