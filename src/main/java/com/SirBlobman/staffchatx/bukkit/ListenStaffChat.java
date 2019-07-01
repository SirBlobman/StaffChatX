package com.SirBlobman.staffchatx.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.SirBlobman.staffchatx.common.ChatHandler;
import com.SirBlobman.staffchatx.common.StaffChatSender;
import com.SirBlobman.staffchatx.common.StaffChatStatus;

import java.util.UUID;

public class ListenStaffChat implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        
        StaffChatStatus status = ChatHandler.getStatus(uuid);
        if(!status.isEnabled()) return;
        
        String message = e.getMessage();
        String requiredPrefix = StaffChatBukkit.INSTANCE.getConfig().getString("options.prefix");
        if(requiredPrefix.isEmpty() || message.startsWith(requiredPrefix)) {
            e.setCancelled(true);
            
            StaffChatSender sender = new StaffChatSenderBukkit(player);
            StaffChatBukkit.INSTANCE.getChatHandler().sendMessage(sender, status.getChannel(), message);
        }
    }
}