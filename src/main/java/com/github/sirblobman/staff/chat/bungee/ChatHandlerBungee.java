package com.github.sirblobman.staff.chat.bungee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import com.github.sirblobman.staff.chat.common.ChatHandler;
import com.github.sirblobman.staff.chat.common.StaffChatChannel;
import com.github.sirblobman.staff.chat.common.StaffChatSender;
import com.github.sirblobman.staff.chat.common.StaffChatStatus;

public final class ChatHandlerBungee extends ChatHandler {
    @Override
    public StaffChatStatus getConsoleStatus() {
        Configuration config = StaffChatBungee.INSTANCE.getConfig();
        boolean isEnabled = config.getBoolean("options.log to console");
        StaffChatChannel channel = getChannel(config.getString("options.console channel"), false);
        
        StaffChatStatus status = new StaffChatStatus(isEnabled);
        status.setChannel(channel);
        return status;
    }

    @Override
    public void sendMessage(StaffChatSender sender, StaffChatChannel channel, String message) {
        if(channel == null) {
            channel = StaffChatChannelBungee.getDefaultChannel();
        }

        message = channel.format(sender.getName(), message);
        CommandSender console = ProxyServer.getInstance().getConsole();
        if(canSeeChannel(console, channel) || console.equals(sender.getOriginalSender())) {
            String color = ChatColor.translateAlternateColorCodes('&',
                    "[StaffChatX] [Channel: " + channel.getName() + "] " + message);
            console.sendMessage(TextComponent.fromLegacyText(color));
        }
        
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if(!canSeeChannel(player, channel)) {
                continue;
            }

            String color = ChatColor.translateAlternateColorCodes('&', message);
            player.sendMessage(TextComponent.fromLegacyText(color));
        }
    }
    
    private boolean canSeeChannel(CommandSender sender, StaffChatChannel channel) {
        if(channel == null) {
            return false;
        }
        
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            UUID uuid = player.getUniqueId();
            return (channel.hasPermission(uuid) && !channel.isDisabled(uuid));
        }
        
        StaffChatStatus consoleStatus = getConsoleStatus();
        if(consoleStatus.isEnabled()) {
            StaffChatChannel consoleChannel = consoleStatus.getChannel();
            return (channel.equals(consoleChannel)
                    || consoleChannel.equals(StaffChatChannelBungee.getDefaultChannel()));
        }
        
        return false;
    }
    
    private static final Map<String, StaffChatChannel> CHANNEL_MAP = new HashMap<>();
    public static StaffChatChannel getChannel(String channelName, boolean reload) {
        if(reload || CHANNEL_MAP.isEmpty()) {
            CHANNEL_MAP.clear();
            
            Configuration config = StaffChatBungee.INSTANCE.getConfig();
            Configuration channels = config.getSection("channels");
            Collection<String> channelList = channels.getKeys();
            for(String channelName1 : channelList) {
                if(channelName1.equals("default") || channelName1.equals("off")) {
                    StaffChatBungee.INSTANCE.getLogger().info("Found invalid channel name '"
                            + channelName1 + "'. Please remove it to prevent issues!");
                    continue;
                }
                
                Configuration channel = channels.getSection(channelName1);
                String permission = channel.getString("permission");
                String format = channel.getString("format");
                StaffChatChannel actualChannel = new StaffChatChannelBungee(channelName1, permission, format);
                CHANNEL_MAP.put(channelName1, actualChannel);
            }
        }
        
        return CHANNEL_MAP.getOrDefault(channelName, StaffChatChannelBungee.getDefaultChannel());
    }
    
    public static List<String> getChannelNameList() {
        return new ArrayList<>(CHANNEL_MAP.keySet());
    }
}
