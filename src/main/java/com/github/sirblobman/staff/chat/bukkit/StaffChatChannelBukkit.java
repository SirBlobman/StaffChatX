package com.github.sirblobman.staff.chat.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.staff.chat.common.StaffChatChannel;

import java.util.UUID;

public class StaffChatChannelBukkit extends StaffChatChannel {
    private static final StaffChatChannelBukkit DEFAULT_CHANNEL = new StaffChatChannelBukkit("default", "staffchatx.channel.default", StaffChatBukkit.INSTANCE.getConfig().getString("options.default format"));
    public static StaffChatChannelBukkit getDefaultChannel() {
        return DEFAULT_CHANNEL;
    }
    
    public StaffChatChannelBukkit(String channelName, String permission, String format) {super(channelName, permission, format);}
    
    @Override
    public boolean hasPermission(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return false;
        
        Permission permission = new Permission(this.getPermission(), "StaffChatX Channel Permission", PermissionDefault.OP);
        return player.hasPermission(permission);
    }
}