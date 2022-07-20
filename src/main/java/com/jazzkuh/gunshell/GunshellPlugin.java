package com.jazzkuh.gunshell;

import com.jazzkuh.gunshell.common.WeaponRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

public final class GunshellPlugin extends JavaPlugin {

    private static @Getter @Setter(AccessLevel.PRIVATE) GunshellPlugin instance;
    private @Getter @Setter(AccessLevel.PRIVATE) WeaponRegistry weaponRegistry;

    @Override
    public void onEnable() {
        setInstance(this);

        setWeaponRegistry(new WeaponRegistry(this));
        this.weaponRegistry.registerFireables("weapons", "builtin.yml");

        this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
    }
}
