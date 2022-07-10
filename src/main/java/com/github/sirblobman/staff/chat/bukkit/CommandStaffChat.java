package com.github.sirblobman.staff.chat.bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.github.sirblobman.staff.chat.common.ChatHandler;
import com.github.sirblobman.staff.chat.common.StaffChatChannel;
import com.github.sirblobman.staff.chat.common.StaffChatStatus;

/*
 * Sub Commands:
 * reload - Reload the config
 * togglesend [off/channel] - Toggle sending messages to the channel named [channel]. Defaults to "default" if the argument is missing. If the argument is "off", the player's chat will be returned to normal
 * toggleview [channel] - Toggle viewing messages in the channel named [channel]. Defaults to "default" if the argument is missing.
 * send <channel> <message...> - Send a message to a specific channel.
 */
public final class CommandStaffChat implements TabExecutor {
    private final StaffChatBukkit plugin;

    public CommandStaffChat(StaffChatBukkit plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean success = onCommand(sender, args);
        if(!success) {
            String useMessage = getMessage("command.usage").replace("<command>", label);
            sender.sendMessage(useMessage);
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            List<String> valueList = Arrays.asList("reload", "togglesend", "ts", "toggleview", "tv", "send");
            return StringUtil.copyPartialMatches(args[0], valueList, new ArrayList<>());
        }
        
        if(args.length == 2) {
            String sub = args[0].toLowerCase(Locale.US);
            List<String> validSubList = Arrays.asList("togglesend", "ts", "toggleview", "tv", "send");
            if(validSubList.contains(sub)) {
                List<String> valueList = ChatHandlerBukkit.getChannelNameList();
                return StringUtil.copyPartialMatches(args[1], valueList, new ArrayList<>());
            }
        }
        
        return Collections.emptyList();
    }

    private FileConfiguration getConfiguration() {
        return this.plugin.getConfig();
    }
    
    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String getMessage(String key) {
        FileConfiguration configuration = getConfiguration();
        String message = configuration.getString("messages." + key, "{" + key + "}");
        return color(message);
    }

    private boolean onCommand(CommandSender sender, String[] args) {
        if(args.length < 1) {
            return false;
        }

        String sub = args[0].toLowerCase();
        switch(sub) {
            case "reload": return commandReload(sender);
            case "send": return commandSend(sender, args);

            case "togglesend":
            case "ts":
                return commandToggleSend(sender, args);

            case "toggleview":
            case "tv":
                return commandToggleView(sender, args);

            default: break;
        }

        return false;
    }
    
    private boolean checkReloadPermission(CommandSender sender) {
        if(sender.hasPermission("staffchatx.*") || sender.hasPermission("staffchatx.reload")) {
            return true;
        }
        
        String message = getMessage("no permission");
        sender.sendMessage(message);
        return false;
    }
    
    private boolean commandReload(CommandSender sender) {
        if(!checkReloadPermission(sender)) {
            return true;
        }
        
        this.plugin.reloadConfig();
        String message = getMessage("command.reloaded");
        sender.sendMessage(message);
        return true;
    }
    
    private boolean commandToggleSend(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            String message = getMessage("not player");
            sender.sendMessage(message);
            return true;
        }
        
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        StaffChatStatus status = ChatHandler.getStatus(uuid);
        
        String channelName = (args.length < 2 ? "default" : args[1]);
        if(channelName.equals("off") || (status.isEnabled() && channelName.equals(status.getChannel().getName()))) {
            status.setStatus(false);
            ChatHandler.setStatus(uuid, status);
            
            String message = getMessage("command.togglesend.disabled");
            player.sendMessage(message);
            return true;
        }
        
        StaffChatChannel channel = ChatHandlerBukkit.getChannel(channelName, false);
        if(channelName.equals("default")) channel = StaffChatChannelBukkit.getDefaultChannel();
        
        if(channel == null) {
            String message = getMessage("invalid channel").replace("{channelName}", channelName);
            player.sendMessage(message);
            return true;
        }
        
        if(!channel.hasPermission(uuid)) {
            String message = getMessage("no permission");
            player.sendMessage(message);
            return true;
        }
        
        status.setChannel(channel);
        status.setStatus(true);
        ChatHandler.setStatus(uuid, status);
        
        String message = getMessage("command.togglesend.channel")
                .replace("{channelName}", channel.getName());
        player.sendMessage(message);
        return true;
    }
    
    private boolean commandToggleView(CommandSender sender, String[] args) {
        String channelName = (args.length < 2 ? "default" : args[1]);
        StaffChatChannel channel = ChatHandlerBukkit.getChannel(channelName, false);
        if(channelName.equals("default")) channel = StaffChatChannelBukkit.getDefaultChannel();
        
        if(channel == null) {
            String message = getMessage("invalid channel").replace("{channelName}", channelName);
            sender.sendMessage(message);
            return true;
        }
        
        if(sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            
            if(!channel.hasPermission(uuid)) {
                String message = getMessage("no permission");
                player.sendMessage(message);
                return true;
            }
             
            channel.toggle(uuid);
            
            boolean disabled = channel.isDisabled(uuid);
            String message = getMessage("command.toggleview." + (disabled ? "disabled" : "enabled"))
                    .replace("{channelName}", channel.getName());
            player.sendMessage(message);
            return true;
        }
        
        FileConfiguration config = getConfiguration();
        config.set("options.console channel", channel.getName().equals("default") ? "" : channel.getName());
        this.plugin.saveConfig();
        
        String message = getMessage("command.toggleview."
                + (channel.getName().equals("default") ? "console default" : "enabled"))
                .replace("{channelName}", channel.getName());
        sender.sendMessage(message);
        return true;
    }
    
    private boolean commandSend(CommandSender sender, String[] args) {
        String channelName = (args.length < 2 ? "default" : args[1]);
        StaffChatChannel channel = ChatHandlerBukkit.getChannel(channelName, false);
        if(channelName.equals("default")) {
            channel = StaffChatChannelBukkit.getDefaultChannel();
        }
        
        if(channel == null) {
            String message = getMessage("invalid channel").replace("{channelName}", channelName);
            sender.sendMessage(message);
            return true;
        }
        
        boolean hasPermission = (!(sender instanceof Player)
                || channel.hasPermission(((Player) sender).getUniqueId()));
        if(!hasPermission) {
            String message = getMessage("no permission");
            sender.sendMessage(message);
            return true;
        }
        
        if(args.length < 3) {
            return false;
        }
        
        String toSend = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        this.plugin.getChatHandler().sendMessage(new StaffChatSenderBukkit(sender), channel, toSend);
        return true;
    }
}
