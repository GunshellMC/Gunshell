package com.jazzkuh.gunshell;

import com.jazzkuh.gunshell.common.WeaponRegistry;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.common.listeners.FireablePreFireListener;
import com.jazzkuh.gunshell.common.listeners.PlayerInteractListener;
import com.jazzkuh.gunshell.common.listeners.PlayerItemHeldListener;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.utils.PluginUtils;
import com.jazzkuh.gunshell.utils.config.ConfigurationFile;
import de.slikey.effectlib.EffectManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    private @Getter @Setter Set<UUID> reloadingSet = new HashSet<>();

    @Override
    public void onEnable() {
        setInstance(this);
        setEffectManager(new EffectManager(this));
        setCompatibilityLayer(new CompatibilityManager().getCompatibilityLayer());
        new PluginUtils();

        setWeaponRegistry(new WeaponRegistry(this));
        this.weaponRegistry.registerFireables("weapons", "builtin.yml");
        this.weaponRegistry.registerAmmunition("ammunition", "builtin.yml");

        DefaultConfig.init();

        messages = new ConfigurationFile(this, "messages.yml", false);
        MessagesConfig.init();
        messages.saveConfig();

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireablePreFireListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerItemHeldListener(), this);

        // TODO: Remove this when commands are implemented
        // TODO: Too lazy to implement commands right now, so I'm just going to do this
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack fireable = this.weaponRegistry.getWeapons().get("revolver").getItemStack(100);
            player.getInventory().addItem(fireable);

            ItemStack ammunition = this.weaponRegistry.getAmmunition().get("revolver_ammo").getItem().toItemStack();
            player.getInventory().addItem(ammunition);
        }

        this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
    }
}
