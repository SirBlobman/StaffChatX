package com.github.sirblobman.staff.chat.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.staff.chat.common.StaffChatX;

public final class StaffChatBukkit extends JavaPlugin implements StaffChatX {
    public static StaffChatBukkit INSTANCE;
    private final ChatHandlerBukkit chatHandler;

    public StaffChatBukkit() {
        INSTANCE = this;
        this.chatHandler = new ChatHandlerBukkit(this);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }
    
    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ListenerStaffChat(this), this);

        PluginCommand pluginCommand = getCommand("staffchatx");
        TabExecutor commandExecutor = new CommandStaffChat(this);
        pluginCommand.setExecutor(commandExecutor);
        pluginCommand.setTabCompleter(commandExecutor);
    }
    
    @Override
    public ChatHandlerBukkit getChatHandler() {
        return this.chatHandler;
    }
}
