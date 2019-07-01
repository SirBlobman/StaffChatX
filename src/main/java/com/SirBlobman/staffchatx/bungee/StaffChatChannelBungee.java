package com.SirBlobman.staffchatx.bungee;

import com.SirBlobman.staffchatx.common.StaffChatChannel;

import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffChatChannelBungee extends StaffChatChannel {
    private static final StaffChatChannelBungee DEFAULT_CHANNEL = new StaffChatChannelBungee("default", "staffchatx.channel.default", StaffChatBungee.INSTANCE.getConfig().getString("options.default format"));
    public static StaffChatChannelBungee getDefaultChannel() {
        return DEFAULT_CHANNEL;
    }
    
    public StaffChatChannelBungee(String channelName, String permission, String format) {super(channelName, permission, format);}
    
    @Override
    public boolean hasPermission(UUID uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if(player == null) return false;
        
        return player.hasPermission(getPermission());
    }
}