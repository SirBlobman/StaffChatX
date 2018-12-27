package com.SirBlobman.staffchatx.bungee.command;

import com.SirBlobman.staffchatx.bungee.configuration.ConfigOptions;
import com.SirBlobman.staffchatx.bungee.listener.ListenStaffChat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandStaffChat extends Command {
    public static final String[] aliases = {"sc", "scx", "staffchatx"};
    public CommandStaffChat() {super("staffchat", "", aliases);}
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length > 0) {
            String sub = args[0].toLowerCase();
            switch(sub) {
            case "toggle": toggle(sender); return;
            case "toggleview": toggleView(sender); return;
            case "reload": reload(sender); return;
            
            default: 
                String message = String.join(" ", args);
                sendMessage(sender, message);
                return;
            }
        }
    }
    
    private boolean toggleView(CommandSender sender) {
        ProxyServer server = ProxyServer.getInstance();
        CommandSender console = server.getConsole();
        if(sender.equals(console)) {
            ConfigOptions.load();
            boolean logToConsole = ConfigOptions.getOption("log to console", true);
            if(logToConsole) {
                ConfigOptions.force("options.log to console", false);
                ConfigOptions.sendMessage(sender, "command.viewing.removed");
            } else {
                ConfigOptions.force("options.log to console", true);
                ConfigOptions.sendMessage(sender, "command.viewing.added");
            }
        } else if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            String permission = ConfigOptions.getOption("permissions.toggleview", "staffchatx.toggle.view");
            if(player.hasPermission(permission)) {
                ListenStaffChat.toggleViewingStaffChat(player);
                
                if(ListenStaffChat.canViewStaffChat(player)) ConfigOptions.sendMessage(player, "command.viewing.removed");
                else ConfigOptions.sendMessage(player, "command.viewing.added");
            }
        }
        
        return true;
    }
    
    private boolean toggle(CommandSender sender) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            String permission = ConfigOptions.getOption("permissions.toggle", "staffchatx.toggle");
            if(player.hasPermission(permission)) {
                if(ListenStaffChat.isAutoStaffChat(player)) {
                    ListenStaffChat.toggleAutoStaffChat(player, false);
                    ConfigOptions.sendMessage(player, "command.removed");
                } else {
                    ListenStaffChat.toggleAutoStaffChat(player, true);
                    ConfigOptions.sendMessage(player, "command.added");
                }
            } else ConfigOptions.sendMessage(sender, "no permission");
        } else ConfigOptions.sendMessage(sender, "command.not player");
        
        return true;
    }
    
    private boolean reload(CommandSender sender) {
        String permission = ConfigOptions.getOption("permissions.reload", "staffchatx.reload");
        if(sender.hasPermission(permission)) {
            ConfigOptions.load();
            ConfigOptions.sendMessage(sender, "command.reload");
        } else ConfigOptions.sendMessage(sender, "no permission");
        
        return true;
    }
    
    private boolean sendMessage(CommandSender sender, String message) {
        String permission = ConfigOptions.getOption("permissions.send", "staffchatx.send");
        if(sender.hasPermission(permission)) ListenStaffChat.sendStaffChat(sender, message);
        else ConfigOptions.sendMessage(sender, "no permission");
        
        return true;
    }
}
