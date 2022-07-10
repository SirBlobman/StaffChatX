package com.github.sirblobman.staff.chat.bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.staff.chat.common.StaffChatChannel;

public final class StaffChatChannelBukkit extends StaffChatChannel {
    private static final StaffChatChannelBukkit DEFAULT_CHANNEL;

    static {
        DEFAULT_CHANNEL = new StaffChatChannelBukkit("default", "staffchatx.channel.default",
                StaffChatBukkit.INSTANCE.getConfig().getString("options.default format"));
    }

    public static StaffChatChannelBukkit getDefaultChannel() {
        return DEFAULT_CHANNEL;
    }
    
    public StaffChatChannelBukkit(String channelName, String permission, String format) {
        super(channelName, permission, format);
    }
    
    @Override
    public boolean hasPermission(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if(player == null) return false;

        String permissionName = getPermission();
        Permission permission = new Permission(permissionName, "StaffChatX Channel Permission",
                PermissionDefault.OP);
        return player.hasPermission(permission);
    }
}
