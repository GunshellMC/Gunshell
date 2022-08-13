package com.jazzkuh.lancaster;

import com.jazzkuh.lancaster.api.enums.PlayerTempModification;
import com.jazzkuh.lancaster.common.WeaponRegistry;
import com.jazzkuh.lancaster.common.commands.LancasterCMD;
import com.jazzkuh.lancaster.common.configuration.DefaultConfig;
import com.jazzkuh.lancaster.common.listeners.*;
import com.jazzkuh.lancaster.common.tasks.PlayerActionbarTask;
import com.jazzkuh.lancaster.compatibility.CompatibilityLayer;
import com.jazzkuh.lancaster.compatibility.CompatibilityManager;
import com.jazzkuh.lancaster.utils.PluginUtils;
import de.slikey.effectlib.EffectManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class LancasterPlugin extends JavaPlugin {

    private static @Getter @Setter(AccessLevel.PRIVATE) LancasterPlugin instance;
    private @Getter @Setter(AccessLevel.PRIVATE) EffectManager effectManager;
    private @Getter @Setter(AccessLevel.PRIVATE) WeaponRegistry weaponRegistry;
    private @Getter @Setter(AccessLevel.PRIVATE) CompatibilityManager compatibilityManager;
    private @Getter @Setter(AccessLevel.PRIVATE) CompatibilityLayer compatibilityLayer;
    private @Getter @Setter HashMap<String, Long> weaponCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, Long> grabCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<String, Long> meleeCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, Long> meleeGrabCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, Long> throwableCooldownMap = new HashMap<>();
    private @Getter @Setter HashMap<UUID, PlayerTempModification> modifiedPlayerMap = new HashMap<>();
    private @Getter @Setter Set<UUID> reloadingSet = new HashSet<>();
    private @Getter @Setter Set<Block> undoList = new HashSet<>();
    private @Getter @Setter HashMap<ArmorStand, Integer> activeThrowables = new HashMap<>();

    @Override
    public void onLoad() {
        setCompatibilityManager(new CompatibilityManager());
        this.getCompatibilityManager().registerExtensions();
        this.getCompatibilityManager().loadExtensions();
    }

    @Override
    public void onEnable() {
        setInstance(this);
        setEffectManager(new EffectManager(this));
        setCompatibilityLayer(this.getCompatibilityManager().getCompatibilityLayer());
        new PluginUtils();

        this.getCompatibilityManager().enableExtensions();

        setWeaponRegistry(new WeaponRegistry(this));
        this.weaponRegistry.registerFireables("weapons", "builtin.yml");
        this.weaponRegistry.registerAmmunition("ammunition", "builtin.yml");
        this.weaponRegistry.registerThrowables("throwables", "builtin.yml");

        DefaultConfig.init();

        new LancasterCMD().register(this);

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireablePreFireListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerItemHeldListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerSwapHandListener(), this);
        Bukkit.getPluginManager().registerEvents(new ThrowablePreFireListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireableToggleScopeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRestoreModifiedListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerArmorStandManipulateListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, new PlayerActionbarTask(), 0, 1L);

        this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        this.getCompatibilityManager().disableExtensions();
        for (ArmorStand armorStand : this.activeThrowables.keySet()) {
            Bukkit.getScheduler().cancelTask(this.activeThrowables.get(armorStand));
            armorStand.remove();
        }

        for (Block block : this.undoList) {
            block.setType(Material.AIR);
        }

        this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been disabled!");
    }
}
