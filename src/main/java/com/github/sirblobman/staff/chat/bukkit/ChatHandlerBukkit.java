package com.github.sirblobman.staff.chat.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.staff.chat.common.ChatHandler;
import com.github.sirblobman.staff.chat.common.StaffChatChannel;
import com.github.sirblobman.staff.chat.common.StaffChatSender;
import com.github.sirblobman.staff.chat.common.StaffChatStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ChatHandlerBukkit extends ChatHandler {
    private final StaffChatBukkit plugin;
    protected ChatHandlerBukkit(StaffChatBukkit plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public StaffChatStatus getConsoleStatus() {
        FileConfiguration config = this.plugin.getConfig();
        boolean isEnabled = config.getBoolean("options.log to console");
        StaffChatChannel channel = getChannel(config.getString("options.console channel"), false);
        
        StaffChatStatus status = new StaffChatStatus(isEnabled);
        status.setChannel(channel);
        return status;
    }

    @Override
    public void sendMessage(StaffChatSender sender, StaffChatChannel channel, String message) {
        if(channel == null) channel = StaffChatChannelBukkit.getDefaultChannel();
        message = channel.format(sender.getName(), message);
        
        CommandSender console = Bukkit.getConsoleSender();
        if(canSeeChannel(console, channel) || console.equals(sender.getOriginalSender())) {
            String color = ChatColor.translateAlternateColorCodes('&', "[StaffChatX] [Channel: " + channel.getName() + "] " + message);
            console.sendMessage(color);
        }
        
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!canSeeChannel(player, channel)) continue;

            String color = ChatColor.translateAlternateColorCodes('&', message);
            player.sendMessage(color);
        }
    }
    
    private boolean canSeeChannel(CommandSender sender, StaffChatChannel channel) {
        if(channel == null) return false;
        
        if(sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            return (channel.hasPermission(uuid) && !channel.isDisabled(uuid));
        }
        
        StaffChatStatus consoleStatus = getConsoleStatus();
        if(consoleStatus.isEnabled()) {
            StaffChatChannel consoleChannel = consoleStatus.getChannel();
            return (channel.equals(consoleChannel) || consoleChannel.equals(StaffChatChannelBukkit.getDefaultChannel()));
        }
        
        return false;
    }
    
    private static final Map<String, StaffChatChannel> CHANNEL_MAP = new HashMap<>();
    public static StaffChatChannel getChannel(String channelName, boolean reload) {
        if(reload || CHANNEL_MAP.isEmpty()) {
            CHANNEL_MAP.clear();
            
            FileConfiguration config = StaffChatBukkit.INSTANCE.getConfig();
            ConfigurationSection channels = config.getConfigurationSection("channels");
            Set<String> channelList = channels.getKeys(false);
            for(String channelName1 : channelList) {
                if(channelName1.equals("default") || channelName1.equals("off")) {
                    StaffChatBukkit.INSTANCE.getLogger().info("Found invalid channel name '" + channelName1 + "'. Please remove it to prevent issues!");
                    continue;
                }
                
                ConfigurationSection channel = channels.getConfigurationSection(channelName1);
                String permission = channel.getString("permission");
                String format = channel.getString("format");
                StaffChatChannel actualChannel = new StaffChatChannelBukkit(channelName1, permission, format);
                CHANNEL_MAP.put(channelName1, actualChannel);
            }
        }
        
        return CHANNEL_MAP.getOrDefault(channelName, StaffChatChannelBukkit.getDefaultChannel());
    }
    
    public static List<String> getChannelNameList() {
        return new ArrayList<>(CHANNEL_MAP.keySet());
    }
}