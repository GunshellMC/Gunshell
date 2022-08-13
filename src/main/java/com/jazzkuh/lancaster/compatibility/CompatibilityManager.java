package com.jazzkuh.lancaster.compatibility;

import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.api.objects.LancasterRayTraceResult;
import com.jazzkuh.lancaster.compatibility.extensions.WorldGuardExtension;
import com.jazzkuh.lancaster.compatibility.extensions.abstraction.ExtensionImpl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.Optional;

public class CompatibilityManager {
    private static final String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName();
    public static final @Getter String version = bukkitVersion.substring(bukkitVersion.lastIndexOf('.') + 1);
    private final @Getter HashMap<Extension, ExtensionImpl> extensions = new HashMap<>();

    public WorldGuardExtension getWorldGuardExtension() {
        return new WorldGuardExtension();
    }

    public boolean isExtensionEnabled(Extension extension) {
        return extensions.containsKey(extension);
    }

    public void registerExtensions() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            extensions.put(Extension.WORLDGUARD, getWorldGuardExtension());
        }
    }

    public void enableExtensions() {
        for (ExtensionImpl extension : extensions.values()) {
            extension.onEnable();
        }
    }

    public void loadExtensions() {
        for (ExtensionImpl extension : extensions.values()) {
            extension.onLoad();
        }
    }

    public void disableExtensions() {
        for (ExtensionImpl extension : extensions.values()) {
            extension.onDisable();
        }
    }

    public CompatibilityLayer getCompatibilityLayer() {
        try {
            Class<?> nmsClass = Class.forName("com.jazzkuh.lancaster.compatibility.versions." + version);
            LancasterPlugin.getInstance().getLogger().info("Using compatibility layer for version " + version);
            return (CompatibilityLayer) nmsClass.getConstructors()[0].newInstance();
        } catch (Exception ignored) {
            LancasterPlugin.getInstance().getLogger().warning("Your server version (" + version + ") is not supported by Lancaster.");
            return null;
        }
    }

    public enum Extension {
        WORLDGUARD
    }
}