package com.SirBlobman.staffchatx;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class ListenChat implements Listener {
    protected static List<Player> STAFF_CHAT = new ArrayList<>();

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(STAFF_CHAT.contains(p) && p.hasPermission("staffchat.send")) {
            e.setCancelled(true);
            String msg = e.getMessage();
            StaffChatX.sendStaffMessage(p, msg);
        }
    }
}
