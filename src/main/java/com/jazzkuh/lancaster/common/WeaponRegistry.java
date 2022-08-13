package com.jazzkuh.lancaster.common;

import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.api.objects.LancasterAmmunition;
import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import com.jazzkuh.lancaster.api.objects.LancasterThrowable;
import com.jazzkuh.lancaster.utils.config.ConfigurationFile;
import com.jazzkuh.lancaster.utils.config.KeyDirectoryConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class WeaponRegistry {
    private final LancasterPlugin plugin;
    private final String FILE_SEPARATOR = "/";

    private @Getter @Setter HashMap<String, LancasterFireable> weapons = new HashMap<>();
    private @Getter @Setter HashMap<String, LancasterAmmunition> ammunition = new HashMap<>();
    private @Getter @Setter HashMap<String, LancasterThrowable> throwables = new HashMap<>();

    private @Getter KeyDirectoryConfiguration weaponConfigurations;
    private @Getter KeyDirectoryConfiguration ammoConfigurations;
    private @Getter KeyDirectoryConfiguration throwableConfigurations;

    public WeaponRegistry(LancasterPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerFireables(String directory, String defaultFile) {
        ConfigurationFile configurationFile = new ConfigurationFile(plugin, directory + FILE_SEPARATOR + defaultFile, true);
        configurationFile.saveConfig();

        weaponConfigurations = new KeyDirectoryConfiguration(plugin, directory);

        HashMap<String, LancasterFireable> fireableRegistry = new HashMap<>();
        for (String key : getWeaponConfigurations().getConfigurations().keySet()) {
            FileConfiguration fileConfiguration = getWeaponConfigurations().getConfigurations().get(key);
            if (fileConfiguration == null) {
                plugin.getLogger().warning("Weapon configuration for " + key + " could not be loaded.");
                continue;
            }
            ConfigurationSection configuration = fileConfiguration.getConfigurationSection(key);
            if (configuration == null) {
                plugin.getLogger().warning("Weapon configuration for " + key + " could not be loaded.");
                continue;
            }

            LancasterFireable lancasterFireable = new LancasterFireable(key, configuration);
            fireableRegistry.put(key, lancasterFireable);
        }
        setWeapons(fireableRegistry);
        plugin.getLogger().info(getWeapons().size() + " fireables have been loaded into memory.");
    }

    public void registerAmmunition(String directory, String defaultFile) {
        ConfigurationFile configurationFile = new ConfigurationFile(plugin, directory + FILE_SEPARATOR + defaultFile, true);
        configurationFile.saveConfig();

        ammoConfigurations = new KeyDirectoryConfiguration(plugin, directory);

        HashMap<String, LancasterAmmunition> ammoRegistry = new HashMap<>();
        for (String key : getAmmoConfigurations().getConfigurations().keySet()) {
            FileConfiguration fileConfiguration = getAmmoConfigurations().getConfigurations().get(key);
            if (fileConfiguration == null) {
                plugin.getLogger().warning("Ammo configuration for " + key + " could not be loaded.");
                continue;
            }
            ConfigurationSection configuration = fileConfiguration.getConfigurationSection(key);
            if (configuration == null) {
                plugin.getLogger().warning("Ammo configuration for " + key + " could not be loaded.");
                continue;
            }

            LancasterAmmunition lancasterAmmunition = new LancasterAmmunition(key, configuration);
            ammoRegistry.put(key, lancasterAmmunition);
        }
        setAmmunition(ammoRegistry);
        plugin.getLogger().info(getAmmunition().size() + " ammunition types have been loaded into memory.");
    }

    public void registerThrowables(String directory, String defaultFile) {
        ConfigurationFile configurationFile = new ConfigurationFile(plugin, directory + FILE_SEPARATOR + defaultFile, true);
        configurationFile.saveConfig();

        throwableConfigurations = new KeyDirectoryConfiguration(plugin, directory);

        HashMap<String, LancasterThrowable> throwableRegistry = new HashMap<>();
        for (String key : getThrowableConfigurations().getConfigurations().keySet()) {
            FileConfiguration fileConfiguration = getThrowableConfigurations().getConfigurations().get(key);
            if (fileConfiguration == null) {
                plugin.getLogger().warning("Throwable configuration for " + key + " could not be loaded.");
                continue;
            }
            ConfigurationSection configuration = fileConfiguration.getConfigurationSection(key);
            if (configuration == null) {
                plugin.getLogger().warning("Throwable configuration for " + key + " could not be loaded.");
                continue;
            }

            LancasterThrowable lancasterThrowable = new LancasterThrowable(key, configuration);
            throwableRegistry.put(key, lancasterThrowable);
        }
        setThrowables(throwableRegistry);
        plugin.getLogger().info(getThrowables().size() + " throwables have been loaded into memory.");
    }
}
