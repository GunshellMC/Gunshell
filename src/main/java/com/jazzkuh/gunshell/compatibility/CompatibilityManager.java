package com.jazzkuh.gunshell.compatibility;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.compatibility.extensions.combattagplus.CombatTagPlusExtension;
import com.jazzkuh.gunshell.compatibility.extensions.worldguard.WorldGuardExtension;
import com.jazzkuh.gunshell.compatibility.framework.Extension;
import com.jazzkuh.gunshell.compatibility.framework.ExtensionInfo;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CompatibilityManager {
    private static final String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName();
    public static final @Getter String version = bukkitVersion.substring(bukkitVersion.lastIndexOf('.') + 1);
    private final @Getter Set<Extension> extensions = new HashSet<>();

    public CompatibilityManager() {
        extensions.add(new CombatTagPlusExtension());
        extensions.add(new WorldGuardExtension());
    }

    public void initialize(InitializationStage stage) {
        for (Extension extension : extensions) {
            if (!extension.getClass().isAnnotationPresent(ExtensionInfo.class)) continue;
            ExtensionInfo info = extension.getClass().getAnnotation(ExtensionInfo.class);

            if (!Bukkit.getPluginManager().isPluginEnabled(info.loadPlugin())) continue;

            if (stage == InitializationStage.LOAD) {
                extension.onLoad();
                GunshellPlugin.getInstance().getLogger().info("Loaded extension " + info.name() + " for plugin " + info.loadPlugin());
            } else if (stage == InitializationStage.ENABLE) {
                extension.onEnable();
                GunshellPlugin.getInstance().getLogger().info("Enabled extension " + info.name() + " for plugin " + info.loadPlugin());
            } else if (stage == InitializationStage.DISABLE) {
                extension.onDisable();
                GunshellPlugin.getInstance().getLogger().info("Disabled extension " + info.name() + " for plugin " + info.loadPlugin());
            }
        }
    }

    public boolean isExtensionEnabled(Class<? extends Extension> extensionClass) {
        if (!extensionClass.isAnnotationPresent(ExtensionInfo.class)) return false;
        ExtensionInfo info =extensionClass.getAnnotation(ExtensionInfo.class);

        return Bukkit.getPluginManager().isPluginEnabled(info.loadPlugin());
    }

    public Extension getExtension(Class<? extends Extension> extensionClass) {
        for (Extension extension : extensions) {
            if (extension.getClass().equals(extensionClass)) return extension;
        }
        return null;
    }

    public CompatibilityLayer getCompatibilityLayer() {
        try {
            Class<?> nmsClass = Class.forName("com.jazzkuh.gunshell.compatibility.versions." + version);
            GunshellPlugin.getInstance().getLogger().info("Using compatibility layer for version " + version);
            return (CompatibilityLayer) nmsClass.getConstructors()[0].newInstance();
        } catch (Exception ignored) {
            GunshellPlugin.getInstance().getLogger().warning("Your server version (" + version + ") is not supported by Gunshell. " +
                    "Loading a fallback compatibility layer but this may cause issues so you are advised to update your server to the latest version.");
            return new CompatibilityLayer() {
                @Override
                public GunshellRayTraceResult performRayTrace(LivingEntity player, double range) {
                    RayTraceResult result = player.getWorld()
                            .rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, entity ->
                                    entity != player);
                    if (result == null) {
                        return new GunshellRayTraceResult(Optional.empty(), Optional.empty(), null, false);
                    }

                    if (result.getHitBlock() != null) {
                        return new GunshellRayTraceResult(Optional.empty(), Optional.of(result.getHitBlock()), result.getHitBlockFace(), false);
                    }

                    if (result.getHitEntity() == null) {
                        return new GunshellRayTraceResult(Optional.empty(), Optional.empty(), null, false);
                    }

                    Entity entity = result.getHitEntity();
                    if (!(entity instanceof LivingEntity) || entity instanceof ArmorStand) {
                        return new GunshellRayTraceResult(Optional.empty(), Optional.empty(), null, false);
                    }
                    LivingEntity livingEntity = (LivingEntity) entity;
                    boolean isHeadshot = (result.getHitPosition().getY() - entity.getLocation().getY()) > 1.375
                            || (livingEntity instanceof Player && ((Player) livingEntity).isSneaking() && (result.getHitPosition().getY() - entity.getLocation().getY()) >  1.1);
                    return new GunshellRayTraceResult(Optional.of(livingEntity), Optional.empty(), null, isHeadshot);
                }

                @Override
                public String getRayTraceResult(Player player, int range) {
                    RayTraceResult result = player.getWorld()
                            .rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, null);
                    return result != null ? result.toString() : "No result found";
                }

                @Override
                public void showEndCreditScene(Player player) {
                    throw new UnsupportedOperationException("This server version is not supported by Gunshell");
                }

                @Override
                public void showDemoMenu(Player player) {
                    throw new UnsupportedOperationException("This server version is not supported by Gunshell");
                }

                @Override
                public void sendPumpkinEffect(Player player, boolean forRemoval) {
                    throw new UnsupportedOperationException("This server version is not supported by Gunshell");
                }

                @Override
                public boolean isPassable(Block block) {
                    return block.isEmpty();
                }

                @Override
                public void setCustomModelData(ItemStack itemStack, int customModelData) {
                    throw new UnsupportedOperationException("This server version is not supported by Gunshell");
                }
            };
        }
    }

    public enum InitializationStage {
        LOAD,
        ENABLE,
        DISABLE
    }
}