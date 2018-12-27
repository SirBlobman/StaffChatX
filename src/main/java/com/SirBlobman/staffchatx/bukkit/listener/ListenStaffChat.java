package com.SirBlobman.staffchatx.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.SirBlobman.staffchatx.bukkit.configuration.ConfigOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListenStaffChat implements Listener {
    private static final List<UUID> AUTO_STAFF_CHAT = new ArrayList<>();
    private static final List<UUID> NOT_VIEWING = new ArrayList<>();
    
    public static boolean isAutoStaffChat(Player player) {
        UUID uuid = player.getUniqueId();
        return AUTO_STAFF_CHAT.contains(uuid);
    }
    
    public static void toggleAutoStaffChat(Player player, boolean enable) {
        UUID uuid = player.getUniqueId();
        
        if(enable) AUTO_STAFF_CHAT.add(uuid);
        else AUTO_STAFF_CHAT.remove(uuid);
    }
    
    public static void toggleViewingStaffChat(Player player) {
        UUID uuid = player.getUniqueId();
        if(NOT_VIEWING.contains(uuid)) NOT_VIEWING.remove(uuid);
        else NOT_VIEWING.add(uuid);
    }
    
    public static String getDisplayName(CommandSender sender) {
        if(sender instanceof Player) return ((Player) sender).getDisplayName();
        return sender.getName();
    }
    
    public static boolean canSendStaffChat(Player player) {
        String permission = ConfigOptions.getOption("permissions.send", "staffchatx.send");
        Permission perm = new Permission(permission, "Send staff chat message", PermissionDefault.OP);
        return player.hasPermission(perm);
    }
    
    public static boolean canViewStaffChat(Player player) {
        if(player != null) {
            String permission = ConfigOptions.getOption("permissions.read", "staffchatx.read");
            Permission perm = new Permission(permission, "View Staff Chat permission", PermissionDefault.OP);
            if(player.hasPermission(perm)) {
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
        
        if(ConfigOptions.getOption("log to console", true)) {
            CommandSender console = Bukkit.getConsoleSender();
            console.sendMessage(colorFormat);
        }
        
        Bukkit.getOnlinePlayers().stream().filter(ListenStaffChat::canViewStaffChat).forEach(staff -> staff.sendMessage(colorFormat));
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();
        
        String prefixRequired = ConfigOptions.getOption("prefix", "");
        if(!message.startsWith(prefixRequired)) return;
        
        if(isAutoStaffChat(player) && canSendStaffChat(player)) {
            e.setCancelled(true);
            sendStaffChat(player, message);
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        toggleAutoStaffChat(player, false);
    }
}