package com.SirBlobman.staffchatx.bungee.listener;

import com.SirBlobman.staffchatx.bungee.configuration.ConfigOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ListenStaffChat implements Listener {
    private static final List<UUID> AUTO_STAFF_CHAT = new ArrayList<>();
    private static final List<UUID> NOT_VIEWING = new ArrayList<>();
    
    public static boolean isAutoStaffChat(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        return AUTO_STAFF_CHAT.contains(uuid);
    }
    
    public static void toggleAutoStaffChat(ProxiedPlayer player, boolean enable) {
        UUID uuid = player.getUniqueId();
        
        if(enable) AUTO_STAFF_CHAT.add(uuid);
        else AUTO_STAFF_CHAT.remove(uuid);
    }
    
    public static void toggleViewingStaffChat(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        if(NOT_VIEWING.contains(uuid)) NOT_VIEWING.remove(uuid);
        else NOT_VIEWING.add(uuid);
    }
    
    public static String getDisplayName(CommandSender sender) {
        if(sender instanceof ProxiedPlayer) return ((ProxiedPlayer) sender).getDisplayName();
        return sender.getName();
    }
    
    public static boolean canSendStaffChat(ProxiedPlayer player) {
        String permission = ConfigOptions.getOption("permissions.send", "staffchatx.send");
        return player.hasPermission(permission);
    }
    
    public static boolean canViewStaffChat(ProxiedPlayer player) {
        if(player != null) {
            String permission = ConfigOptions.getOption("permissions.read", "staffchatx.read");
            if(player.hasPermission(permission)) {
                UUID uuid = player.getUniqueId();
                return !NOT_VIEWING.contains(uuid);
            }
        }
        
        return false;
    }
    
    public static void sendStaffChat(CommandSender sender, String message) {
        if(message == null || message.isEmpty()) return;
        
        String senderName = sender.getName();
        String displayName = getDisplayName(sender);
        String chatFormat = ConfigOptions.getMessage("format").replace("{username}", senderName).replace("{displayname}", displayName).replace("{message}", message);
        String colorFormat = ChatColor.translateAlternateColorCodes('&', chatFormat);
        
        ProxyServer server = ProxyServer.getInstance();
        
        if(ConfigOptions.getOption("log to console", true)) {
            CommandSender console = server.getConsole();
            BaseComponent[] asText = TextComponent.fromLegacyText(colorFormat);
            console.sendMessage(asText);
        }
        
        server.getPlayers().stream().filter(ListenStaffChat::canViewStaffChat).forEach(staff -> {
            BaseComponent[] asText = TextComponent.fromLegacyText(colorFormat);
            staff.sendMessage(asText);
        });
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onChat(ChatEvent e) {
        if(e.isCancelled() || e.isCommand()) return;
        
        Connection sender = e.getSender();
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            String message = e.getMessage();
            
            String prefixRequired = ConfigOptions.getOption("prefix", "");
            if(!message.startsWith(prefixRequired)) return;
            
            if(isAutoStaffChat(player) && canSendStaffChat(player)) {
                e.setCancelled(true);
                sendStaffChat(player, message);
            }
        }
    }
    
    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        ProxiedPlayer player = e.getPlayer();
        toggleAutoStaffChat(player, false);
    }
}