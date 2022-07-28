package com.jazzkuh.gunshell.compatibility;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.util.Optional;

public class CompatibilityManager {
    private static final String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName();
    public static final @Getter String version = bukkitVersion.substring(bukkitVersion.lastIndexOf('.') + 1);

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
            };
        }
    }
}