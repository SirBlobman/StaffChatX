package com.github.sirblobman.staff.chat.bungee;

import java.util.Arrays;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import com.github.sirblobman.staff.chat.common.StaffChatSender;

public final class StaffChatSenderBungee extends StaffChatSender {
    private final CommandSender sender;
    public StaffChatSenderBungee(CommandSender sender) {
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
        BaseComponent[] text = TextComponent.fromLegacyText(message);
        this.sender.sendMessage(text);
    }

    @Override
    public void sendMessage(String... messages) {
        Arrays.stream(messages).forEach(this::sendMessage);
    }
}
