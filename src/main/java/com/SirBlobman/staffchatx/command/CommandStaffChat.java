package com.SirBlobman.staffchatx.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.SirBlobman.staffchatx.configuration.ConfigOptions;
import com.SirBlobman.staffchatx.listener.ListenStaffChat;

public class CommandStaffChat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if(cmd.equals("staffchat")) {
            if(args.length > 0) {
                String sub = args[0].toLowerCase();
                switch(sub) {
                case "toggle": return toggle(sender);
                case "toggleview": return toggleView(sender);
                case "reload": return reload(sender);
                
                default: 
                    String message = String.join(" ", args);
                    return sendMessage(sender, message);
                }
            } else return false;
        } else return false;
    }
    
    private boolean toggleView(CommandSender sender) {
        if(sender instanceof ConsoleCommandSender) {
            ConfigOptions.load();
            boolean logToConsole = ConfigOptions.getOption("log to console", true);
            if(logToConsole) {
                ConfigOptions.force("options.log to console", false);
                ConfigOptions.sendMessage(sender, "command.viewing.removed");
            } else {
                ConfigOptions.force("options.log to console", true);
                ConfigOptions.sendMessage(sender, "command.viewing.added");
            }
        } else if(sender instanceof Player) {
            Player player = (Player) sender;
            String permission = ConfigOptions.getOption("permissions.toggleview", "staffchatx.toggle.view");
            Permission perm = new Permission(permission, "Toggle your ability to view the staff chat.", PermissionDefault.OP);
            if(player.hasPermission(perm)) {
                ListenStaffChat.toggleViewingStaffChat(player);
                
                if(ListenStaffChat.canViewStaffChat(player)) ConfigOptions.sendMessage(player, "command.viewing.removed");
                else ConfigOptions.sendMessage(player, "command.viewing.added");
            }
        }
        
        return true;
    }
    
    private boolean toggle(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            String permission = ConfigOptions.getOption("permissions.toggle", "staffchatx.toggle");
            Permission perm = new Permission(permission, "Toggle your ability to automatically talk in the staff chat.", PermissionDefault.OP);
            if(player.hasPermission(perm)) {
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
        Permission perm = new Permission(permission, "Reload StaffChatX", PermissionDefault.OP);
        if(sender.hasPermission(perm)) {
            ConfigOptions.load();
            ConfigOptions.sendMessage(sender, "command.reload");
        } else ConfigOptions.sendMessage(sender, "no permission");
        
        return true;
    }
    
    private boolean sendMessage(CommandSender sender, String message) {
        String permission = ConfigOptions.getOption("permissions.send", "staffchatx.send");
        Permission perm = new Permission(permission, "Send staff chat message", PermissionDefault.OP);
        if(sender.hasPermission(perm)) {
            ListenStaffChat.sendStaffChat(sender, message);
            return true;
        } else {
            ConfigOptions.sendMessage(sender, "no permission");
            return true;
        }
    }
}