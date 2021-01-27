package com.github.sirblobman.staff.chat.bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
public class CommandStaffChat implements TabExecutor {
    private final StaffChatBukkit plugin;
    public CommandStaffChat(StaffChatBukkit plugin) {
        this.plugin = plugin;
    }
    
    private FileConfiguration getConfig() {
        return this.plugin.getConfig();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if(!cmd.equals("staffchatx")) return false;
        
        boolean success = run(sender, args);
        if(!success) {
            String useMessage = getConfigMessage("command.usage").replace("<command>", label);
            sender.sendMessage(useMessage);
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if(!cmd.equals("staffchatx")) return null;
        
        if(args.length == 1) {
            List<String> valid = Arrays.asList("reload", "togglesend", "ts", "toggleview", "tv", "send");
            return valid.stream().filter(item -> item.startsWith(args[0])).collect(Collectors.toList());
        }
        
        if(args.length == 2) {
            String sub = args[0];
            if(sub.equals("togglesend") || sub.equals("ts") || sub.equals("toggleview") || sub.equals("tv") || sub.equals("send")) {
                List<String> valid = ChatHandlerBukkit.getChannelNameList();
                return valid.stream().filter(item -> item.startsWith(args[1])).collect(Collectors.toList());
            }
        }
        
        return null;
    }
    
    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String getConfigMessage(String messageId) {
        FileConfiguration config = getConfig();
        String path = "messages." + messageId;
        String message = config.getString(path, path);
        return color(message);
    }

    private boolean run(CommandSender sender, String[] args) {
        if(args.length < 1) return false;

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
    
    private boolean checkPermission(CommandSender sender, String permission) {
        if(sender.hasPermission("staffchatx.*") || sender.hasPermission(permission)) return true;
        
        String message = getConfigMessage("no permission");
        sender.sendMessage(message);
        return false;
    }
    
    private boolean commandReload(CommandSender sender) {
        if(!checkPermission(sender, "staffchatx.reload")) return true;
        
        this.plugin.reloadConfig();
        String message = getConfigMessage("command.reloaded");
        sender.sendMessage(message);
        return true;
    }
    
    private boolean commandToggleSend(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            String message = getConfigMessage("not player");
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
            
            String message = getConfigMessage("command.togglesend.disabled");
            player.sendMessage(message);
            return true;
        }
        
        StaffChatChannel channel = ChatHandlerBukkit.getChannel(channelName, false);
        if(channelName.equals("default")) channel = StaffChatChannelBukkit.getDefaultChannel();
        
        if(channel == null) {
            String message = getConfigMessage("invalid channel").replace("{channelName}", channelName);
            player.sendMessage(message);
            return true;
        }
        
        if(!channel.hasPermission(uuid)) {
            String message = getConfigMessage("no permission");
            player.sendMessage(message);
            return true;
        }
        
        status.setChannel(channel);
        status.setStatus(true);
        ChatHandler.setStatus(uuid, status);
        
        String message = getConfigMessage("command.togglesend.channel").replace("{channelName}", channel.getName());
        player.sendMessage(message);
        return true;
    }
    
    private boolean commandToggleView(CommandSender sender, String[] args) {
        String channelName = (args.length < 2 ? "default" : args[1]);
        StaffChatChannel channel = ChatHandlerBukkit.getChannel(channelName, false);
        if(channelName.equals("default")) channel = StaffChatChannelBukkit.getDefaultChannel();
        
        if(channel == null) {
            String message = getConfigMessage("invalid channel").replace("{channelName}", channelName);
            sender.sendMessage(message);
            return true;
        }
        
        if(sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            
            if(!channel.hasPermission(uuid)) {
                String message = getConfigMessage("no permission");
                player.sendMessage(message);
                return true;
            }
             
            channel.toggle(uuid);
            
            boolean disabled = channel.isDisabled(uuid);
            String message = getConfigMessage("command.toggleview." + (disabled ? "disabled" : "enabled")).replace("{channelName}", channel.getName());
            player.sendMessage(message);
            return true;
        }
        
        FileConfiguration config = getConfig();
        config.set("options.console channel", channel.getName().equals("default") ? "" : channel.getName());
        this.plugin.saveConfig();
        
        String message = getConfigMessage("command.toggleview." + (channel.getName().equals("default") ? "console default" : "enabled")).replace("{channelName}", channel.getName());
        sender.sendMessage(message);
        return true;
    }
    
    private boolean commandSend(CommandSender sender, String[] args) {
        String channelName = (args.length < 2 ? "default" : args[1]);
        StaffChatChannel channel = ChatHandlerBukkit.getChannel(channelName, false);
        if(channelName.equals("default")) channel = StaffChatChannelBukkit.getDefaultChannel();
        
        if(channel == null) {
            String message = getConfigMessage("invalid channel").replace("{channelName}", channelName);
            sender.sendMessage(message);
            return true;
        }
        
        boolean hasPermission = (sender instanceof Player ? channel.hasPermission(((Player) sender).getUniqueId()) : true);
        if(!hasPermission) {
            String message = getConfigMessage("no permission");
            sender.sendMessage(message);
            return true;
        }
        
        if(args.length < 3) return false;
        
        String toSend = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        this.plugin.getChatHandler().sendMessage(new StaffChatSenderBukkit(sender), channel, toSend);
        return true;
    }
}