package com.jazzkuh.lancaster.common.configuration;

import com.jazzkuh.lancaster.LancasterPlugin;
import lombok.Getter;
import org.bukkit.Location;

import java.util.List;

public enum DefaultConfig {
    CONFIG_VERSION("config-version", "1.0"),
    PER_WEAPON_COOLDOWN("per-weapon-cooldown", true),
    HITBOX_INCREASE("hitbox-increase", 0.2);

    private final @Getter String path;
    private final @Getter Object value;

    DefaultConfig(String path, Object value) {
        this.path = path;
        this.value = value;
    }

    public String asString() {
        return LancasterPlugin.getInstance().getConfig().getString(this.getPath());
    }

    public Integer asInteger() {
        return LancasterPlugin.getInstance().getConfig().getInt(this.getPath());
    }

    public Boolean asBoolean() {
        return LancasterPlugin.getInstance().getConfig().getBoolean(this.getPath());
    }

    public Double asDouble() {
        return LancasterPlugin.getInstance().getConfig().getDouble(this.getPath());
    }

    public Location asLocation() {
        return LancasterPlugin.getInstance().getConfig().getLocation(this.getPath());
    }

    public List<String> asList() {
        return LancasterPlugin.getInstance().getConfig().getStringList(this.getPath());
    }

    public static void init() {
        for (DefaultConfig configValue : DefaultConfig.values()) {
            if (LancasterPlugin.getInstance().getConfig().get(configValue.getPath()) == null) {
                LancasterPlugin.getInstance().getConfig().set(configValue.getPath(), configValue.getValue());
            }
        }

        LancasterPlugin.getInstance().saveConfig();
    }
}
