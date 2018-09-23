package com.SirBlobman.staffchatx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.staffchatx.command.CommandStaffChat;
import com.SirBlobman.staffchatx.configuration.ConfigOptions;

public class StaffChatX extends JavaPlugin implements Listener {
    public static final Logger LOG = Logger.getLogger("StaffChatX");
    public static StaffChatX INSTANCE;
    public static File FOLDER;

    @Override
    public void onEnable() {
        INSTANCE = this;
        FOLDER = getDataFolder();
        getCommand("staffchat").setExecutor(new CommandStaffChat());
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onChat(AsyncPlayerChatEvent e) { 
        Player player = e.getPlayer();
        String permission = ConfigOptions.getOption("permissions.send", "staffchatx.send");
        Permission perm = new Permission(permission, "Send staff chat message", PermissionDefault.OP);
        if(player.hasPermission(perm) && isInAutoStaffChat(player)) {
            e.setCancelled(true);
            String msg = e.getMessage();
            sendStaffChatMessage(player, msg);
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        setAutoStaffChat(player, false);
    }
    
    private static List<UUID> STAFF_CHAT = new ArrayList<>();
    
    public static boolean isInAutoStaffChat(Player player) {
        UUID uuid = player.getUniqueId();
        return STAFF_CHAT.contains(uuid);
    }
    
    public static void setAutoStaffChat(Player player, boolean add) {
        UUID uuid = player.getUniqueId();
        if(add) STAFF_CHAT.add(uuid);
        else STAFF_CHAT.remove(uuid);
    }
    
    public static void sendStaffChatMessage(CommandSender cs, String msg) {
        if(msg != null && !msg.isEmpty()) {
            String username = cs.getName();
            String dispname = getDisplayName(cs);
            String format = ConfigOptions.getMessage("format")
                .replace("{message}", msg)
                .replace("{username}", username)
                .replace("{displayname}", dispname);
            String color = ChatColor.translateAlternateColorCodes('&', format);
            if(ConfigOptions.getOption("log to console", true)) {
                ConsoleCommandSender console = Bukkit.getConsoleSender();
                console.sendMessage(color);
            }
            
            String permission = ConfigOptions.getOption("permissions.read", "staffchatx.read");
            Permission perm = new Permission(permission, "View Staff Chat permission", PermissionDefault.OP);
            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(perm)).forEach(player -> player.sendMessage(color));
        }
    }
    
    public static String getDisplayName(CommandSender cs) {
        if(cs instanceof Player) {
            Player player = (Player) cs;
            return player.getDisplayName();
        } else return cs.getName();
    }
}
