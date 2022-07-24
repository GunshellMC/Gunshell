package com.jazzkuh.gunshell;

import com.jazzkuh.gunshell.common.WeaponRegistry;
import com.jazzkuh.gunshell.common.commands.GunshellCMD;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.common.listeners.*;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.utils.PluginUtils;
import com.jazzkuh.gunshell.utils.config.ConfigurationFile;
import de.slikey.effectlib.EffectManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class GunshellPlugin extends JavaPlugin {

    private static @Getter @Setter(AccessLevel.PRIVATE) GunshellPlugin instance;
    private static @Getter ConfigurationFile messages;
    private @Getter @Setter(AccessLevel.PRIVATE) EffectManager effectManager;
    private @Getter @Setter(AccessLevel.PRIVATE) WeaponRegistry weaponRegistry;
    private @Getter @Setter(AccessLevel.PRIVATE) CompatibilityLayer compatibilityLayer;
    private @Getter @Setter HashMap<String, Long> weaponCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, Long> grabCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, Long> throwableCooldownMap = new HashMap<>();
    private @Getter @Setter Set<UUID> reloadingSet = new HashSet<>();
    private @Getter @Setter HashMap<ArmorStand, Integer> activeThrowables = new HashMap<>();

    @Override
    public void onEnable() {
        setInstance(this);
        setEffectManager(new EffectManager(this));
        setCompatibilityLayer(new CompatibilityManager().getCompatibilityLayer());
        new PluginUtils();

        setWeaponRegistry(new WeaponRegistry(this));
        this.weaponRegistry.registerFireables("weapons", "builtin.yml");
        this.weaponRegistry.registerAmmunition("ammunition", "builtin.yml");
        this.weaponRegistry.registerThrowables("throwables", "builtin.yml");

        DefaultConfig.init();

        messages = new ConfigurationFile(this, "messages.yml", false);
        MessagesConfig.init();
        messages.saveConfig();

        new GunshellCMD().register(this, true);

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireablePreFireListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerItemHeldListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSwapHandListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileHitListener(), this);
        Bukkit.getPluginManager().registerEvents(new ThrowablePreFireListener(), this);

        this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        for (ArmorStand armorStand : this.activeThrowables.keySet()) {
            Bukkit.getScheduler().cancelTask(this.activeThrowables.get(armorStand));
            armorStand.remove();
        }

        this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been disabled!");
    }
}
