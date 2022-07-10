package com.github.sirblobman.staff.chat.bukkit;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.staff.chat.common.StaffChatSender;

public final class StaffChatSenderBukkit extends StaffChatSender {
    private final CommandSender sender;

    public StaffChatSenderBukkit(CommandSender sender) {
        this.sender = sender;
    }
    
    @Override
    public CommandSender getOriginalSender() {
        return this.sender;
    }
    
    @Override
    public String getName() {
        return this.sender.getName();
    }

    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

    @Override
    public void sendMessage(String... messages) {
        for(String message : messages) {
            sendMessage(message);
        }
    }
}
