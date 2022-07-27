package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.utils.config.ConfigurationFile;
import com.jazzkuh.gunshell.utils.config.KeyDirectoryConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class WeaponRegistry {
    private final GunshellPlugin plugin;
    private final String FILE_SEPARATOR = "/";

    private @Getter @Setter HashMap<String, GunshellFireable> weapons = new HashMap<>();
    private @Getter @Setter HashMap<String, GunshellAmmunition> ammunition = new HashMap<>();
    private @Getter @Setter HashMap<String, GunshellThrowable> throwables = new HashMap<>();
    private @Getter @Setter HashMap<String, GunshellMelee> melees = new HashMap<>();

    private @Getter KeyDirectoryConfiguration weaponConfigurations;
    private @Getter KeyDirectoryConfiguration ammoConfigurations;
    private @Getter KeyDirectoryConfiguration throwableConfigurations;
    private @Getter KeyDirectoryConfiguration meleeConfigurations;

    public WeaponRegistry(GunshellPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerFireables(String directory, String defaultFile) {
        ConfigurationFile configurationFile = new ConfigurationFile(plugin, directory + FILE_SEPARATOR + defaultFile, true);
        configurationFile.saveConfig();

        weaponConfigurations = new KeyDirectoryConfiguration(plugin, directory);

        HashMap<String, GunshellFireable> fireableRegistry = new HashMap<>();
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

            GunshellFireable gunshellFireable = new GunshellFireable(key, configuration);
            fireableRegistry.put(key, gunshellFireable);
        }
        setWeapons(fireableRegistry);
        plugin.getLogger().info(getWeapons().size() + " fireables have been loaded into memory.");
    }

    public void registerAmmunition(String directory, String defaultFile) {
        ConfigurationFile configurationFile = new ConfigurationFile(plugin, directory + FILE_SEPARATOR + defaultFile, true);
        configurationFile.saveConfig();

        ammoConfigurations = new KeyDirectoryConfiguration(plugin, directory);

        HashMap<String, GunshellAmmunition> ammoRegistry = new HashMap<>();
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

            GunshellAmmunition gunshellAmmunition = new GunshellAmmunition(key, configuration);
            ammoRegistry.put(key, gunshellAmmunition);
        }
        setAmmunition(ammoRegistry);
        plugin.getLogger().info(getAmmunition().size() + " ammunition types have been loaded into memory.");
    }

    public void registerThrowables(String directory, String defaultFile) {
        ConfigurationFile configurationFile = new ConfigurationFile(plugin, directory + FILE_SEPARATOR + defaultFile, true);
        configurationFile.saveConfig();

        throwableConfigurations = new KeyDirectoryConfiguration(plugin, directory);

        HashMap<String, GunshellThrowable> throwableRegistry = new HashMap<>();
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

            GunshellThrowable gunshellThrowable = new GunshellThrowable(key, configuration);
            throwableRegistry.put(key, gunshellThrowable);
        }
        setThrowables(throwableRegistry);
        plugin.getLogger().info(getThrowables().size() + " throwables have been loaded into memory.");
    }

    public void registerMelees(String directory, String defaultFile) {
        ConfigurationFile configurationFile = new ConfigurationFile(plugin, directory + FILE_SEPARATOR + defaultFile, true);
        configurationFile.saveConfig();

        meleeConfigurations = new KeyDirectoryConfiguration(plugin, directory);

        HashMap<String, GunshellMelee> meleeRegistry = new HashMap<>();
        for (String key : getMeleeConfigurations().getConfigurations().keySet()) {
            FileConfiguration fileConfiguration = getMeleeConfigurations().getConfigurations().get(key);
            if (fileConfiguration == null) {
                plugin.getLogger().warning("Melee configuration for " + key + " could not be loaded.");
                continue;
            }
            ConfigurationSection configuration = fileConfiguration.getConfigurationSection(key);
            if (configuration == null) {
                plugin.getLogger().warning("Melee configuration for " + key + " could not be loaded.");
                continue;
            }

            GunshellMelee gunshellMelee = new GunshellMelee(key, configuration);
            meleeRegistry.put(key, gunshellMelee);
        }
        setMelees(meleeRegistry);
        plugin.getLogger().info(getMelees().size() + " melee weapons have been loaded into memory.");
    }
}
