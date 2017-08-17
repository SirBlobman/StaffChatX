package com.SirBlobman.staffchatx;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that listens when people chat
 * @author SirBlobman
 */
public class ListenChat implements Listener {
    protected static List<Player> STAFF_CHAT = new ArrayList<Player>(); //People who have toggled staff chat

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer(); //Get the player who chatted
        if(STAFF_CHAT.contains(p) && p.hasPermission("staffchat.send")) { //Check if they are toggled and have perms
            e.setCancelled(true); //Prevent the message from showing in actual chat
            String msg = e.getMessage(); //Get the message they sent
            String f = ConfigLang.getMessage("messages.format"); //Get format from config
            f = f.replace("{message}", msg); //Replace variable
            f = f.replace("{username}", p.getName()); //Replace variable
            f = f.replace("{displayname}", p.getDisplayName()); //Replace variable
            String color = ChatColor.translateAlternateColorCodes('&', f); //replace & with \u00a7
            for(Player o : Bukkit.getOnlinePlayers()) { //Do this for all online players
                String perm = "staffchat.read";
                if(o.hasPermission(perm)) o.sendMessage(color); //Only send message if player has permission
            }
        }
    }
}
