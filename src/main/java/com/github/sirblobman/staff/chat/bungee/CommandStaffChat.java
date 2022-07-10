package com.github.sirblobman.staff.chat.bungee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import com.github.sirblobman.staff.chat.common.ChatHandler;
import com.github.sirblobman.staff.chat.common.StaffChatChannel;
import com.github.sirblobman.staff.chat.common.StaffChatStatus;

public final class CommandStaffChat extends Command implements TabExecutor {
    private final StaffChatBungee plugin;

    public CommandStaffChat(StaffChatBungee plugin) {
        super("staffchatx", "", "sc", "scx", "staffchat");
        this.plugin = plugin;
    }
    
    private Configuration getConfig() {
        return this.plugin.getConfig();
    }
    
    private String getConfigMessage(String messageId) {
        Configuration config = getConfig();
        String path = "messages." + messageId;
        String message = config.getString(path, path);
        return color(message);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {        
        if(args.length == 1) {
            List<String> valid = Arrays.asList("reload", "togglesend", "ts", "toggleview", "tv", "send");
            return valid.stream().filter(item -> item.startsWith(args[0])).collect(Collectors.toList());
        }
        
        if(args.length == 2) {
            String sub = args[0];
            if(sub.equals("togglesend") || sub.equals("ts") || sub.equals("toggleview") || sub.equals("tv")
                    || sub.equals("send")) {
                List<String> valid = ChatHandlerBungee.getChannelNameList();
                return valid.stream().filter(item -> item.startsWith(args[1])).collect(Collectors.toList());
            }
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        boolean success = run(sender, args);
        if(!success) {
            String useMessage = getConfigMessage("command.usage").replace("<command>", "staffchatx");
            BaseComponent[] message = TextComponent.fromLegacyText(useMessage);
            sender.sendMessage(message);
        }
    }
    
    public boolean run(CommandSender sender, String[] args) {
        if(args.length < 1) {
            return false;
        }
        
        String sub = args[0].toLowerCase();
        switch(sub) {
        case "reload": return reload(sender);
        
        case "togglesend": 
        case "ts":
            return toggleSend(sender, args);
            
        case "toggleview": 
        case "tv":
            return toggleView(sender, args);
            
        case "send": return send(sender, args);
        
        default: return false;
        }
    }
    
    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    private boolean checkReloadPermission(CommandSender sender) {
        if(sender.hasPermission("staffchatx.*") || sender.hasPermission("staffchatx.reload")) {
            return true;
        }
        
        String message = getConfigMessage("no permission");
        sender.sendMessage(TextComponent.fromLegacyText(message));
        return false;
    }
    
    private boolean reload(CommandSender sender) {
        if(!checkReloadPermission(sender)) {
            return true;
        }
        
        this.plugin.getConfig();
        String message = getConfigMessage("command.reloaded");
        sender.sendMessage(TextComponent.fromLegacyText(message));
        return true;
    }
    
    private boolean toggleSend(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            String message = getConfigMessage("not player");
            sender.sendMessage(TextComponent.fromLegacyText(message));
            return true;
        }
        
        ProxiedPlayer ProxiedPlayer = (ProxiedPlayer) sender;
        UUID uuid = ProxiedPlayer.getUniqueId();
        StaffChatStatus status = ChatHandler.getStatus(uuid);
        
        String channelName = (args.length < 2 ? "default" : args[1]);
        if(channelName.equals("off") || (status.isEnabled() && channelName.equals(status.getChannel().getName()))) {
            status.setStatus(false);
            ChatHandler.setStatus(uuid, status);
            
            String message = getConfigMessage("command.togglesend.disabled");
            ProxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
            return true;
        }
        
        StaffChatChannel channel = ChatHandlerBungee.getChannel(channelName, false);
        if(channelName.equals("default")) channel = StaffChatChannelBungee.getDefaultChannel();
        
        if(channel == null) {
            String message = getConfigMessage("invalid channel")
                    .replace("{channelName}", channelName);
            ProxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
            return true;
        }
        
        if(!channel.hasPermission(uuid)) {
            String message = getConfigMessage("no permission");
            ProxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
            return true;
        }
        
        status.setChannel(channel);
        status.setStatus(true);
        ChatHandler.setStatus(uuid, status);
        
        String message = getConfigMessage("command.togglesend.channel")
                .replace("{channelName}", channel.getName());
        ProxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
        return true;
    }
    
    private boolean toggleView(CommandSender sender, String[] args) {
        String channelName = (args.length < 2 ? "default" : args[1]);
        StaffChatChannel channel = ChatHandlerBungee.getChannel(channelName, false);
        if(channelName.equals("default")) channel = StaffChatChannelBungee.getDefaultChannel();
        
        if(channel == null) {
            String message = getConfigMessage("invalid channel").replace("{channelName}", channelName);
            sender.sendMessage(TextComponent.fromLegacyText(message));
            return true;
        }
        
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer ProxiedPlayer = (ProxiedPlayer) sender;
            UUID uuid = ProxiedPlayer.getUniqueId();
            
            if(!channel.hasPermission(uuid)) {
                String message = getConfigMessage("no permission");
                ProxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
                return true;
            }
             
            channel.toggle(uuid);
            
            boolean disabled = channel.isDisabled(uuid);
            String message = getConfigMessage("command.toggleview."
                    + (disabled ? "disabled" : "enabled")).replace("{channelName}", channel.getName());
            ProxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
            return true;
        }
        
        Configuration config = getConfig();
        config.set("options.console channel", channel.getName().equals("default") ? "" : channel.getName());
        this.plugin.saveConfig(config);
        
        String message = getConfigMessage("command.toggleview."
                + (channel.getName().equals("default") ? "console default" : "enabled"))
                .replace("{channelName}", channel.getName());
        sender.sendMessage(TextComponent.fromLegacyText(message));
        return true;
    }
    
    private boolean send(CommandSender sender, String[] args) {
        String channelName = (args.length < 2 ? "default" : args[1]);
        StaffChatChannel channel = ChatHandlerBungee.getChannel(channelName, false);
        if(channelName.equals("default")) channel = StaffChatChannelBungee.getDefaultChannel();
        
        if(channel == null) {
            String message = getConfigMessage("invalid channel")
                    .replace("{channelName}", channelName);
            sender.sendMessage(TextComponent.fromLegacyText(message));
            return true;
        }
        
        boolean hasPermission = (!(sender instanceof ProxiedPlayer)
                || channel.hasPermission(((ProxiedPlayer) sender).getUniqueId()));
        if(!hasPermission) {
            String message = getConfigMessage("no permission");
            sender.sendMessage(TextComponent.fromLegacyText(message));
            return true;
        }
        
        if(args.length < 3) {
            return false;
        }
        
        String toSend = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        this.plugin.getChatHandler().sendMessage(new StaffChatSenderBungee(sender), channel, toSend);
        return true;
    }
}
