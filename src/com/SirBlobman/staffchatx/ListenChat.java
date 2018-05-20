package com.SirBlobman.staffchatx;

import com.google.common.collect.Lists;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ListenChat implements Listener {
    protected static List<Player> STAFF_CHAT = Lists.newArrayList();

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String perm = Config.get("options.permissions.send", String.class);
        if(STAFF_CHAT.contains(p) && p.hasPermission(perm)) {
            e.setCancelled(true);
            String msg = e.getMessage();
            StaffChatX.sendStaffMessage(p, msg);
        }
    }
}
