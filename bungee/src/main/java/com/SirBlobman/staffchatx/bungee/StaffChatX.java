package com.SirBlobman.staffchatx.bungee;

import com.SirBlobman.staffchatx.bungee.command.CommandStaffChat;
import com.SirBlobman.staffchatx.bungee.configuration.ConfigOptions;

import java.io.File;
import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class StaffChatX extends Plugin {
    public static StaffChatX INSTANCE;
    public static File FOLDER;
    public static Logger LOG;
    
    @Override
    public void onEnable() {
        INSTANCE = this;
        LOG = getLogger();
        FOLDER = getDataFolder();
        
        ConfigOptions.load();
        
        ProxyServer server = getProxy();
        PluginManager manager = server.getPluginManager();
        manager.registerCommand(this, new CommandStaffChat());
        
        LOG.info("Successfully enabled StaffChatX for all servers!");
    }
}