package com.github.sirblobman.staff.chat.bungee;

import java.util.UUID;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import com.github.sirblobman.staff.chat.common.ChatHandler;
import com.github.sirblobman.staff.chat.common.StaffChatSender;
import com.github.sirblobman.staff.chat.common.StaffChatStatus;

public final class ListenStaffChat implements Listener {
    @EventHandler(priority=EventPriority.LOWEST)
    public void onChat(ChatEvent e) {
        if(e.isCommand()) {
            return;
        }
        
        Connection senderConnection = e.getSender();
        if(!(senderConnection instanceof ProxiedPlayer)) {
            return;
        }
        
        ProxiedPlayer player = (ProxiedPlayer) senderConnection;
        UUID uuid = player.getUniqueId();
        
        StaffChatStatus status = ChatHandler.getStatus(uuid);
        if(!status.isEnabled()) {
            return;
        }
        
        String message = e.getMessage();
        String requiredPrefix = StaffChatBungee.INSTANCE.getConfig().getString("options.prefix");
        if(requiredPrefix.isEmpty() || message.startsWith(requiredPrefix)) {
            e.setCancelled(true);
            
            StaffChatSender sender = new StaffChatSenderBungee(player);
            StaffChatBungee.INSTANCE.getChatHandler().sendMessage(sender, status.getChannel(), message);
        }
    }
}
