package com.github.sirblobman.staff.chat.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class StaffChatChannel {
    private final List<UUID> disabledPlayerList;
    private final String channelName;
    private final String permission;
    private final String format;

    public StaffChatChannel(String channelName, String permission, String format) {
        this.disabledPlayerList = new ArrayList<>();
        this.channelName = channelName;
        this.permission = permission;
        this.format = format;
    }
    
    public abstract boolean hasPermission(UUID playerId);
    
    public final String getName() {
        return this.channelName;
    }
    
    public final String getPermission() {
        return this.permission;
    }
    
    public final String format(String username, String message) {
        return this.format.replace("{username}", username).replace("{message}", message);
    }
    
    @Override
    public final boolean equals(Object o2) {
        if(super.equals(o2)) {
            return true;
        }

        if(!(o2 instanceof StaffChatChannel)) {
            return false;
        }

        StaffChatChannel other = (StaffChatChannel) o2;

        String name1 = getName();
        String name2 = other.getName();
        boolean nameEquals = Objects.equals(name1, name2);

        String permission1 = getPermission();
        String permission2 = other.getPermission();
        boolean permissionEquals = Objects.equals(permission1, permission2);

        return (nameEquals && permissionEquals && Objects.equals(this.format, other.format));
    }

    public final void toggle(UUID playerId) {
        if(this.disabledPlayerList.contains(playerId)) {
            this.disabledPlayerList.remove(playerId);
            return;
        }

        disabledPlayerList.add(playerId);
    }
    
    public final boolean isDisabled(UUID playerId) {
        return this.disabledPlayerList.contains(playerId);
    }
}
