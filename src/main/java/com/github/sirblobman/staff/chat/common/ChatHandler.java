package com.github.sirblobman.staff.chat.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ChatHandler {
    private static final Map<UUID, StaffChatStatus> STAFF_CHAT_STATUS = new HashMap<>();
    
    public static void setStatus(UUID playerId, StaffChatStatus status) {
        if(playerId == null || status == null) {
            return;
        }
        
        STAFF_CHAT_STATUS.put(playerId, status);
    }
    
    public static StaffChatStatus getStatus(UUID playerId) {
        if(playerId == null) {
            return null;
        }
        
        StaffChatStatus defaultStatus = new StaffChatStatus(false);        
        return STAFF_CHAT_STATUS.getOrDefault(playerId, defaultStatus);
    }
    
    public abstract StaffChatStatus getConsoleStatus();
    public abstract void sendMessage(StaffChatSender sender, StaffChatChannel channel, String message);
}
