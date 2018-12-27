package com.SirBlobman.staffchatx.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.staffchatx.bukkit.command.CommandStaffChat;
import com.SirBlobman.staffchatx.bukkit.listener.ListenStaffChat;

import java.io.File;
import java.util.logging.Logger;

public class StaffChatX extends JavaPlugin {
    public static final Logger LOG = Logger.getLogger("StaffChatX");
    public static StaffChatX INSTANCE;
    public static File FOLDER;

    @Override
    public void onEnable() {
        INSTANCE = this;
        FOLDER = getDataFolder();
        getCommand("staffchat").setExecutor(new CommandStaffChat());
        Bukkit.getPluginManager().registerEvents(new ListenStaffChat(), this);
    }
}
