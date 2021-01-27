package com.github.sirblobman.staff.chat.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.staff.chat.common.ChatHandler;
import com.github.sirblobman.staff.chat.common.StaffChatX;

public class StaffChatBukkit extends JavaPlugin implements StaffChatX {
    public static StaffChatBukkit INSTANCE;
    private ChatHandlerBukkit CHAT_HANDLER;
    
    @Override
    public void onEnable() {
        INSTANCE = this;
        CHAT_HANDLER = new ChatHandlerBukkit(this);
        saveDefaultConfig();
        
        Bukkit.getPluginManager().registerEvents(new ListenStaffChat(), this);
        
        getCommand("staffchatx").setExecutor(new CommandStaffChat(this));
    }
    
    @Override
    public ChatHandler getChatHandler() {
        return CHAT_HANDLER;
    }
}