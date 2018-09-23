package com.SirBlobman.staffchatx.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.SirBlobman.staffchatx.StaffChatX;
import com.SirBlobman.staffchatx.configuration.ConfigOptions;

public class CommandStaffChat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        String cmd = c.getName().toLowerCase();
        if(cmd.equals("staffchat")) {
            if(args.length > 0) {
                String sub = args[0].toLowerCase();
                if(sub.equals("toggle")) return toggle(cs);
                else if(sub.equals("reload")) return reload(cs);
                else return sendMessage(cs, String.join(" ", args));
            } else return false;
        } else return false;
    }
    
    private boolean toggle(CommandSender cs) {
        if(cs instanceof Player) {
            Player player = (Player) cs;
            String permission = ConfigOptions.getOption("permissions.toggle", "staffchatx.toggle");
            Permission perm = new Permission(permission, "Toggle your ability to automatically talk in the staff chat.", PermissionDefault.OP);
            if(player.hasPermission(perm)) {
                if(StaffChatX.isInAutoStaffChat(player)) {
                    StaffChatX.setAutoStaffChat(player, false);
                    ConfigOptions.sendMessage(player, "command.removed");
                } else {
                    StaffChatX.setAutoStaffChat(player, true);
                    ConfigOptions.sendMessage(player, "command.added");
                }
                return true;
            } else {
                ConfigOptions.sendMessage(cs, "no permission");
                return true;
            }
        } else if(cs instanceof ConsoleCommandSender) {
            ConfigOptions.load();
            boolean logToConsole = ConfigOptions.getOption("log to console", true);
            if(logToConsole) {
                ConfigOptions.force("options.log to console", false);
                ConfigOptions.sendMessage(cs, "command.removed");
            } else {
                ConfigOptions.force("options.log to console", true);
                ConfigOptions.sendMessage(cs, "command.added");
            }
            return true;
        } else return false;
    }
    
    private boolean reload(CommandSender cs) {
        String permission = ConfigOptions.getOption("permissions.reload", "staffchatx.reload");
        Permission perm = new Permission(permission, "Reload StaffChatX", PermissionDefault.OP);
        if(cs.hasPermission(perm)) {
            ConfigOptions.load();
            ConfigOptions.sendMessage(cs, "command.reload");
            return true;
        } else {
            ConfigOptions.sendMessage(cs, "no permission");
            return true;
        }
    }
    
    private boolean sendMessage(CommandSender cs, String msg) {
        String permission = ConfigOptions.getOption("permissions.send", "staffchatx.send");
        Permission perm = new Permission(permission, "Send staff chat message", PermissionDefault.OP);
        if(cs.hasPermission(perm)) {
            StaffChatX.sendStaffChatMessage(cs, msg);
            return true;
        } else {
            ConfigOptions.sendMessage(cs, "no permission");
            return true;
        }
    }
}