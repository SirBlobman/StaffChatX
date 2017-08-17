package com.SirBlobman.staffchatx;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigLang {
    private static final File FOLDER = StaffChatX.FOLDER; //Gets folder from main class
    private static final File FILE = new File(FOLDER, "config.yml"); //File inside the folder called 'config.yml'
    private static YamlConfiguration config = new YamlConfiguration(); //New config
    
    public static YamlConfiguration load() {//Loads and returns the config
        try { //Files can be blocked or have errors
            if(!FILE.exists()) save(); //If the file does not exist, save an empty one
            config.load(FILE); //Load the config from the file
            defaults(); //Set defaults if not already set
            return config; //return the config
        } catch(Throwable ex) { //If there is an error, send the message to console
            String msg = "Failed to load config.yml:"; //Error Message
            Logger log = Logger.getLogger("StaffChatX"); //Get logger
            log.log(Level.SEVERE, msg, ex); //Log an error and print the exception
            return null; //Return a null config since it could not be loaded
        }
    }
    
    public static void save() {//Saves the config
        try {
            if(!FILE.exists()) { //Check if the file does not exist
                FOLDER.mkdirs(); //Create the folders
                FILE.createNewFile(); //Create the file
            } config.save(FILE); //Save the config settings to the file
        } catch(Throwable ex) { //If there is an error, send the message to console
            String msg = "Failed to save config.yml:"; //Error Message
            Logger log = Logger.getLogger("StaffChatX"); //Get logger
            log.log(Level.SEVERE, msg, ex); //Log an error and print the exception
            return; //Stop further execution of this code
        }
    }
    
    public static String getMessage(String key) { //Get a message using a path
        load(); //Load config first
        String msg = config.getString(key); //Get the string message from the config
        String color = ChatColor.translateAlternateColorCodes('&', msg); //Color the message
        return color; //Return the colored message
    }
    
    private static void defaults() {//Write all default settings and save config
        set("messages.format", "&5&lSTAFF &f{username}&r &7\u00BB&r {message}", false);
        set("messages.command.usage", "&f/staff <toggle> &eOR &f/staff <message...>", false);
        set("messages.command.not player", "&cYou are not a Player", false);
        set("messages.command.added", "&5Staff Chat: &2on", false);
        set("messages.command.removed", "&5Staff Chat: &2off", false);
        save();
    }
    
    private static void set(String path, Object value, boolean force) { //Set a config option
        Object o = config.get(path); //Get the current object
        boolean n = (o == null); //check if its null
        if(n || force) config.set(path, value); //If it was null or 'force' is true, override the option
    }
}