package com.SirBlobman.staffchatx;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    private static final File FOLDER = StaffChatX.FOLDER;
    private static final File FILE = new File(FOLDER, "config.yml");
    private static YamlConfiguration config = new YamlConfiguration();
    
    public static YamlConfiguration load() {
        try {
            if(!FILE.exists()) save();
            config.load(FILE);
            defaults();
            return config;
        } catch(Throwable ex) {
            String msg = "Failed to load config.yml:";
            Logger log = Logger.getLogger("StaffChatX");
            log.log(Level.SEVERE, msg, ex);
            return null;
        }
    }
    
    public static void save() {
        try {
            if(!FILE.exists()) {
                FOLDER.mkdirs();
                FILE.createNewFile();
            } config.save(FILE);
        } catch(Throwable ex) {
            String msg = "Failed to save config.yml:";
            Logger log = Logger.getLogger("StaffChatX");
            log.log(Level.SEVERE, msg, ex);
            return;
        }
    }
    
    public static String getMessage(String key) {
        load();
        String msg = config.getString(key);
        String color = ChatColor.translateAlternateColorCodes('&', msg);
        return color;
    }
    
    public static <T> T get(String path, Class<T> clazz) {
        load();
        Object o = config.get(path);
        if(clazz.isInstance(o)) {
            T t = clazz.cast(o);
            return t;
        } else return null;
    }
    
    private static void defaults() {
        set("options.log to console", true, false);
        set("options.permissions.read", "staffchat.read", false);
        set("options.permissions.send", "staffchat.send", false);
        set("options.permissions.toggle", "staffchat.toggle", false);
        set("options.permissions.reload", "staffchat.reload", false);
        set("messages.format", "&5&lSTAFF &f{username}&r &7\u00BB&r {message}", false);
        set("messages.no permission", "You are not a staff member!", false);
        set("messages.command.usage", "&f/<command> <toggle> &eOR &f/<command> <message...>", false);
        set("messages.command.not player", "&cYou are not a Player", false);
        set("messages.command.added", "&5Staff Chat: &2on", false);
        set("messages.command.removed", "&5Staff Chat: &2off", false);
        set("messages.command.reload", "&5&l[&9StaffChatX&5&l]&r Reloaded configs", false);
        save();
    }
    
    public static void set(String path, Object value, boolean force) {
        Object o = config.get(path);
        boolean n = (o == null);
        if(n || force) config.set(path, value);
    }
}
