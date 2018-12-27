package com.SirBlobman.staffchatx.bungee.configuration;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

public class ConfigOptions extends Config {
	private static Configuration config;
	public static void save() {saveConfig(config, "config.yml");}
	public static void load() {
		copyFromJar("config.yml");
		config = loadConfig("config.yml");
	}
	
	public static void force(String path, Object value) {
	    load();
	    config.set(path, value);
	    save();
	}
	
	public static <T> T getOption(String path, T defaultValue) {
		load();
		return config.get(path, defaultValue);
	}
	
	public static String getMessage(String path) {
	    load();
        String newPath = "messages." + path;
        return config.getString(newPath, newPath);
	}
    
    public static void sendMessage(CommandSender sender, String path) {
        String msg = getMessage(path);
        if(msg != null && !msg.isEmpty()) {
            String color = ChatColor.translateAlternateColorCodes('&', msg);
            BaseComponent[] asText = TextComponent.fromLegacyText(color);
            sender.sendMessage(asText);
        }
    }
}