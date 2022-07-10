package com.github.sirblobman.staff.chat.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import com.github.sirblobman.staff.chat.common.ChatHandler;
import com.github.sirblobman.staff.chat.common.StaffChatX;

public final class StaffChatBungee extends Plugin implements StaffChatX {
    public static StaffChatBungee INSTANCE;
    public static ChatHandlerBungee CHAT_HANDLER;
    
    @Override
    public void onEnable() {
        INSTANCE = this;
        CHAT_HANDLER = new ChatHandlerBungee();
        getConfig();
        
        PluginManager manager = getProxy().getPluginManager();
        manager.registerCommand(this, new CommandStaffChat(this));
        manager.registerListener(this, new ListenStaffChat());
    }
    
    @Override
    public ChatHandler getChatHandler() {
        return CHAT_HANDLER;    
    }
    
    public Configuration getConfig() {
        try {
            File folder = getDataFolder();
            if(!folder.exists() && !folder.mkdirs()) {
                throw new IOException("Failed to create data folder.");
            }
            
            File file = new File(folder, "config.yml");
            if(!file.exists()) {
                InputStream internalConfig = getResourceAsStream("config.yml");
                if(internalConfig == null) {
                    getLogger().info("Could not find config.yml in jar, please contact SirBlobman!");
                    return null;
                }
                
                Path path = file.toPath();
                Files.copy(internalConfig, path, StandardCopyOption.REPLACE_EXISTING);
                internalConfig.close();
            }

            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch(Exception ex) {
            getLogger().info("An error occurred while loading config.yml!");
            ex.printStackTrace();
            return null;
        }
    }
    
    public void saveConfig(Configuration config) {
        try {
            File folder = getDataFolder();
            if(!folder.exists() && !folder.mkdirs()) {
                throw new IOException("Failed to create data folder.");
            }
            
            File file = new File(folder, "config.yml");
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch(Exception ex) {
            getLogger().info("An error occurred while saving config.yml!");
            ex.printStackTrace();
        }
    }
}
