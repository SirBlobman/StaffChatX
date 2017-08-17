package com.SirBlobman.staffchatx;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class StaffChatX extends JavaPlugin {
    public static StaffChatX INSTANCE;
    public static File FOLDER;

    @Override
    public void onEnable() {
        INSTANCE = this;
        FOLDER = getDataFolder();
        getCommand("staffchat").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(new ListenChat(), this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        final String cmd = c.getName().toLowerCase();
        if(cmd.equals("staffchat")) {
            if(args.length > 0) {
                final String sub = args[0].toLowerCase();
                if(sub.equals("toggle")) {
                    if(cs instanceof Player) {
                        Player p = (Player) cs;
                        String msg = "";
                        if(ListenChat.STAFF_CHAT.contains(p)) {
                            ListenChat.STAFF_CHAT.remove(p);
                            msg = Config.getMessage("messages.command.removed");
                        } else {
                            ListenChat.STAFF_CHAT.add(p);
                            msg = Config.getMessage("messages.command.added");
                        }
                        cs.sendMessage(msg);
                        return true;
                    } else if(cs instanceof ConsoleCommandSender) {
                        Config.load();
                        boolean log = Config.get("options.log to console", Boolean.class);
                        String msg = "";
                        if(log) {
                            Config.set("options.log to console", false, true);
                            Config.save();
                            msg = Config.getMessage("messages.command.removed");
                        } else {
                            Config.set("options.log to console", true, true);
                            Config.save();
                            msg = Config.getMessage("messages.command.added");
                        }
                        cs.sendMessage(msg);
                        return true;
                    } else return false;
                } else {
                    final StringBuilder sb = new StringBuilder();
                    for(final String s : args) sb.append(" " + s);
                    final String msg = sb.toString();
                    final String trim = msg.trim();
                    sendStaffMessage(cs, trim);
                    return true;
                }
            } else {
                final String msg = Config.getMessage("messages.command.usage");
                cs.sendMessage(msg);
                return true;
            }
        } else return false;
    }

    public static void sendStaffMessage(CommandSender cs, String msg) {
        String username = cs.getName();
        String dispname = ((cs instanceof Player) ? ((Player) cs).getDisplayName() : username);
        String f = Config.getMessage("messages.format");
        f = f.replace("{message}", msg);
        f = f.replace("{username}", username);
        f = f.replace("{displayname}", dispname);
        String color = ChatColor.translateAlternateColorCodes('&', f);
        if(Config.get("options.log to console", Boolean.class)) {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            console.sendMessage(color);
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            final String perm = "staffchat.read";
            if(p.hasPermission(perm)) p.sendMessage(color);
        }
    }
}