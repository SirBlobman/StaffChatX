package com.SirBlobman.staffchatx;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Staff Chat example
 * @author SirBlobman
 */
public class StaffChatX extends JavaPlugin { //This first line tells bukkit that this is a plugin coded in java
    public static StaffChatX INSTANCE; //Instance of StaffChatX
    public static File FOLDER; //Data Folder (/plugins/StaffChatX)
    
    @Override /*This overrides the enable method, which does nothing by default*/
    public void onEnable() {
        INSTANCE = this; /*Set the instance to this plugin when its enabled*/
        FOLDER = getDataFolder(); /*Not Requires, Sets the FOLDER variable to /plugins/StaffChatX/*/
        getCommand("staffchat").setExecutor(this); /*Sets the command listener to this class. If 'staffchat' is not in plugin.yml it will cause errors*/
        Bukkit.getPluginManager().registerEvents(new ListenChat(), this); //Registers chat listener
    }
    
    @Override /*This overrides what happens when someone does a command linked to StaffChatX. By default it just shows command usage*/
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
        if(cs instanceof Player) { //If you don't check you will get a ClassCastException
            Player p = (Player) cs; //Changes the sender to a player
            String cmd = c.getName().toLowerCase(); //Gets the base command in lowercase mode so you don't have to check aliases
            if(cmd.equals("staffchat")) { //Checks if the command is 'staffchat'
                if(args.length > 0) {
                    String sub = args[0].toLowerCase(); //First argument forced to be lowercase
                    if(sub.equals("toggle")) {
                        String msg = "";
                        if(ListenChat.STAFF_CHAT.contains(p)) { //If the player has staff chat, remove them
                            ListenChat.STAFF_CHAT.remove(p);
                            msg = ConfigLang.getMessage("messages.command.removed");
                        } else { //Otherwise, add them to the list
                            ListenChat.STAFF_CHAT.add(p);
                            msg = ConfigLang.getMessage("messages.command.added");
                        }
                        p.sendMessage(msg); //Send message to player
                        return true; //'return true' means 'do not show usage'
                    } else {
                        StringBuilder sb = new StringBuilder(); //Make a String Builder
                        for(String s : args) sb.append(s + " "); //Add every arg and a space to the builder
                        String msg = sb.toString(); //Change the builder to an actual string
                        String trim = msg.trim(); //Remove extra spaces
                        String f = ConfigLang.getMessage("messages.format"); //Get format from config
                        f = f.replace("{message}", trim); //Replace variable
                        f = f.replace("{username}", p.getName()); //Replace variable
                        f = f.replace("{displayname}", p.getDisplayName()); //Replace variable
                        String color = ChatColor.translateAlternateColorCodes('&', f); //replace & with \u00a7
                        for(Player o : Bukkit.getOnlinePlayers()) { //Do this for all online players
                            String perm = "staffchat.read";
                            if(o.hasPermission(perm)) o.sendMessage(color); //Only send message if player has permission
                        }
                        return true; //'return true' means 'do not show usage'
                    }
                } else {
                    String msg = ConfigLang.getMessage("messages.command.usage"); //Get message from config
                    p.sendMessage(msg); //Send message to player
                    return true; //'return true' means 'do not show usage'
                }
            } else return false; //If its not staff chat just ignore it
        } else {
            String error = ConfigLang.getMessage("messages.command.not player"); //Get message from config
            cs.sendMessage(error); //Send error to the player
            return true; //'return true' means 'do not show usage'
        }
    }
}