package com.jazzkuh.gunshell.common.configuration;

import com.jazzkuh.gunshell.GunshellPlugin;
import lombok.Getter;
import org.bukkit.Location;

import java.util.List;

public enum DefaultConfig {
    CONFIG_VERSION("config_version", "1.0"),
    PER_WEAPON_COOLDOWN("per_weapon_cooldown", true);

    private final @Getter String path;
    private final @Getter Object value;

    DefaultConfig(String path, Object value) {
        this.path = path;
        this.value = value;
    }

    public String asString() {
        return GunshellPlugin.getInstance().getConfig().getString(this.getPath());
    }

    public Integer asInteger() {
        return GunshellPlugin.getInstance().getConfig().getInt(this.getPath());
    }

    public Boolean asBoolean() {
        return GunshellPlugin.getInstance().getConfig().getBoolean(this.getPath());
    }

    public Location asLocation() {
        return GunshellPlugin.getInstance().getConfig().getLocation(this.getPath());
    }

    public List<String> asList() {
        return GunshellPlugin.getInstance().getConfig().getStringList(this.getPath());
    }

    public static void init() {
        for (DefaultConfig configValue : DefaultConfig.values()) {
            if (GunshellPlugin.getInstance().getConfig().get(configValue.getPath()) == null) {
                GunshellPlugin.getInstance().getConfig().set(configValue.getPath(), configValue.getValue());
            }
        }

        GunshellPlugin.getInstance().saveConfig();
    }
}
