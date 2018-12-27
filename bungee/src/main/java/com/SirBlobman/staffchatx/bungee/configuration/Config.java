package com.SirBlobman.staffchatx.bungee.configuration;

import com.SirBlobman.staffchatx.bungee.StaffChatX;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Config {
	private static final File FOLDER = StaffChatX.FOLDER;
	
	/**
	 * Loads a configuration file
	 * 
	 * @param fileName The name of the file which is inside of "/plugins/StaffChatX/"
	 * @return {@link YamlConfiguration} if it could be loaded, {@code null} if fileName is null or if the config failed to load
	 */
	public static Configuration loadConfig(String fileName) {
		try {
			File file = new File(FOLDER, fileName);
			if(!file.exists()) {
				FOLDER.mkdirs();
				file.createNewFile();
			}
			
			ConfigurationProvider configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
			Configuration config = configProvider.load(file);
			return config;
		} catch(Throwable ex) {
			String error = "Failed to load file '" + FOLDER.getPath() + fileName + "' as a YamlConfiguration:";
			StaffChatX.LOG.severe(error);
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Saves a configuration to a file.<br/>
	 * If you use this, all the comments will be deleted and any config options will be overriden
	 * 
	 * @param config The configuration you want to save in YAML format
	 * @param fileName The name of the file in "/plugins/StaffChatX/"
	 */
	public static void saveConfig(Configuration config, String fileName) {
		try {
			File file = new File(FOLDER, fileName);
			if(!file.exists()) {
				FOLDER.mkdirs();
				file.createNewFile();
			}

            ConfigurationProvider configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
			configProvider.save(config, file);
		} catch(Throwable ex) {
			String error = "Failed to save file '" + FOLDER.getPath() + fileName + "' as a YamlConfiguration:";
			StaffChatX.LOG.severe(error);
			ex.printStackTrace();
		}
	}
	
	/**
	 * This will copy a file in the jar file for this plugin to "/plugins/StaffChatX/"<br/>
	 * The file will only be copied IF the file does not already exist in that directory
	 * @param fileName The name of the file to copy
	 */
	public static void copyFromJar(String fileName) {
		try {
			Plugin pl = StaffChatX.INSTANCE;
			InputStream is = pl.getResourceAsStream(fileName);
			File file = new File(FOLDER, fileName);
			if(!file.exists()) {
				FOLDER.mkdirs();
				String pathName = file.getAbsolutePath();
				Path path = Paths.get(pathName);
				Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
				is.close();
			}
		} catch(Throwable ex) {
			String error = "Failed to copy '" + fileName + "' from the jar file:";
			StaffChatX.LOG.severe(error);
			ex.printStackTrace();
		}
	}
}