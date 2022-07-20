package com.jazzkuh.gunshell.utils.config;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public class KeyDirectoryConfiguration {

    private @Getter HashMap<String, FileConfiguration> configurations = new HashMap<>();
    private final File file;

    public KeyDirectoryConfiguration(Plugin plugin, String directory) {
        this.file = new File(plugin.getDataFolder(), directory);
        this.loadConfiguration();
    }

    @SuppressWarnings("unchecked")
    public void loadConfiguration() {
        HashMap<String, FileConfiguration> fileConfigurations = new HashMap<>();
        Collection<File> itemFiles = FileUtils.listFiles(this.file, new String[]{ "yml" }, true);
        for (File file : itemFiles) {
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            for (String key : fileConfiguration.getKeys(false)) {
                fileConfigurations.put(key, fileConfiguration);
            }
        }

        this.configurations = fileConfigurations;
    }
}
