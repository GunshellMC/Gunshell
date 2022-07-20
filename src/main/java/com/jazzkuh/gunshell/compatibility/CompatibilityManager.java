package com.jazzkuh.gunshell.compatibility;

import com.jazzkuh.gunshell.GunshellPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;

public class CompatibilityManager {

    private final String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName();
    private final @Getter String version = bukkitVersion.substring(bukkitVersion.lastIndexOf('.') + 1);

    public CompatibilityLayer getCompatibilityLayer() {
        try {
            Class<?> nmsClass = Class.forName("com.jazzkuh.gunshell.compatibility.versions." + version);
            return (CompatibilityLayer) nmsClass.getConstructors()[0].newInstance();
        } catch (Exception ignored) {
            GunshellPlugin.getInstance().getLogger().warning("Your server version (" + version + ") is not supported by Gunshell. Loading a fallback compatibility layer.");
            return null;
        }
    }
}