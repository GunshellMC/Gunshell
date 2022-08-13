package com.jazzkuh.lancaster.utils.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigurationFile {

    private FileConfiguration config;
    private final File file;

    public ConfigurationFile(Plugin plugin, String name, boolean hasEmbeddedFile) {
        this.file = new File(plugin.getDataFolder(), name);

        if (!this.file.exists())
            try {
                if (hasEmbeddedFile) {
                    plugin.saveResource(name, true);
                }
                this.file.createNewFile();
            } catch (IOException ignored) {
            }

        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }
}
