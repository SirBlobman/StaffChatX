package com.SirBlobman.staffchatx.common;

public abstract class StaffChatSender {
    public abstract String getName();
    public abstract Object getOriginalSender();
    
    public abstract void sendMessage(String message);
    public abstract void sendMessage(String... messages);
}