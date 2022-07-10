package com.github.sirblobman.staff.chat.bukkit;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.sirblobman.staff.chat.common.ChatHandler;
import com.github.sirblobman.staff.chat.common.StaffChatChannel;
import com.github.sirblobman.staff.chat.common.StaffChatSender;
import com.github.sirblobman.staff.chat.common.StaffChatStatus;

public final class ListenerStaffChat implements Listener {
    private final StaffChatBukkit plugin;

    public ListenerStaffChat(StaffChatBukkit plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        
        StaffChatStatus status = ChatHandler.getStatus(uuid);
        if(!status.isEnabled()) {
            return;
        }
        
        String message = e.getMessage();
        FileConfiguration configuration = this.plugin.getConfig();
        String requiredPrefix = configuration.getString("options.prefix");

        if(requiredPrefix == null || requiredPrefix.isEmpty() || message.startsWith(requiredPrefix)) {
            e.setCancelled(true);
            StaffChatSender sender = new StaffChatSenderBukkit(player);

            StaffChatChannel channel = status.getChannel();
            ChatHandler chatHandler = this.plugin.getChatHandler();
            chatHandler.sendMessage(sender, channel, message);
        }
    }
}
