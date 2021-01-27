package com.github.sirblobman.staff.chat.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ChatHandler {
    private static final Map<UUID, StaffChatStatus> STAFF_CHAT_STATUS = new HashMap<>();
    
    public static void setStatus(UUID uuid, StaffChatStatus status) {
        if(uuid == null || status == null) return;
        
        STAFF_CHAT_STATUS.put(uuid, status);
    }
    
    public static StaffChatStatus getStatus(UUID uuid) {
        if(uuid == null) return null;
        
        StaffChatStatus defaultStatus = new StaffChatStatus(false);        
        return STAFF_CHAT_STATUS.getOrDefault(uuid, defaultStatus);
    }
    
    public abstract StaffChatStatus getConsoleStatus();
    public abstract void sendMessage(StaffChatSender sender, StaffChatChannel channel, String message);
}