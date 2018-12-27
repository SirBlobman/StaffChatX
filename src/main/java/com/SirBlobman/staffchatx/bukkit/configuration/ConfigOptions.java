package com.SirBlobman.staffchatx.bukkit.configuration;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigOptions extends Config {
	private static YamlConfiguration config = new YamlConfiguration();
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
	
	@SuppressWarnings("unchecked")
	public static <T> T getOption(String path, T defaultValue) {
		load();
		if(config.isSet("options." + path)) {
			Object obj = config.get("options." + path);
			Class<?> clazz = defaultValue.getClass();
			if(clazz.isInstance(obj)) return (T) obj;
			else return defaultValue;
		} else return defaultValue;
	}
	
	public static String getMessage(String path) {
        String newPath = "messages." + path;
        if(config == null) load();
        if(config.isSet(newPath)) {
            String msg = config.getString(newPath);
            return msg;
        } else return newPath;
	}
    
    public static void sendMessage(CommandSender cs, String path) {
        String msg = getMessage(path);
        if(msg != null && !msg.isEmpty()) {
            String color = ChatColor.translateAlternateColorCodes('&', msg);
            cs.sendMessage(color);
        }
    }
}