package com.jazzkuh.gunshell;

import com.jazzkuh.gunshell.common.WeaponRegistry;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.common.listeners.PlayerInteractListener;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.utils.config.ConfigurationFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GunshellPlugin extends JavaPlugin {

    private static @Getter @Setter(AccessLevel.PRIVATE) GunshellPlugin instance;
    private static @Getter ConfigurationFile messages;
    private @Getter @Setter(AccessLevel.PRIVATE) WeaponRegistry weaponRegistry;
    private @Getter @Setter(AccessLevel.PRIVATE) CompatibilityLayer compatibilityLayer;

    @Override
    public void onEnable() {
        setInstance(this);
        setCompatibilityLayer(new CompatibilityManager().getCompatibilityLayer());

        setWeaponRegistry(new WeaponRegistry(this));
        this.weaponRegistry.registerFireables("weapons", "builtin.yml");
        this.weaponRegistry.registerAmmunition("ammunition", "builtin.yml");

        DefaultConfig.init();

        messages = new ConfigurationFile(this, "messages.yml", false);
        MessagesConfig.init();
        messages.saveConfig();

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);

        this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
    }
}
