package com.jazzkuh.gunshell.compatibility.versions;

import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import org.bukkit.FluidCollisionMode;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.util.Optional;

public class v1_19_R1 implements CompatibilityLayer {
    @Override
    public GunshellRayTraceResult performRayTrace(LivingEntity player, double range) {
        RayTraceResult result = player.getWorld()
                .rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, null);
        if (result == null) {
            return new GunshellRayTraceResult(Optional.empty(), Optional.empty(), false);
        }

        if (result.getHitBlock() != null) {
            return new GunshellRayTraceResult(Optional.empty(), Optional.of(result.getHitBlock()), false);
        }

        if (result.getHitEntity() == null) {
            return new GunshellRayTraceResult(Optional.empty(), Optional.empty(), false);
        }

        Entity entity = result.getHitEntity();
        if (!(entity instanceof LivingEntity) || entity instanceof ArmorStand) {
            return new GunshellRayTraceResult(Optional.empty(), Optional.empty(), false);
        }
        boolean isHeadshot = (result.getHitPosition().getY() - entity.getLocation().getY()) > 1.35;
        LivingEntity livingEntity = (LivingEntity) entity;
        return new GunshellRayTraceResult(Optional.of(livingEntity), Optional.empty(), isHeadshot);
    }

    @Override
    public String getRayTraceResult(Player player, int range) {
        RayTraceResult result = player.getWorld()
                .rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, null);
        return result != null ? result.toString() : "No result found";
    }

    @Override
    public void showEndCreditScene(Player player) {
        PacketPlayOutGameStateChange gameStateChange = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.e, 1f);
        ((CraftPlayer) player).getHandle().b.a(gameStateChange);
    }

    @Override
    public void showDemoMenu(Player player) {
        PacketPlayOutGameStateChange gameStateChange = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.f, 0f);
        ((CraftPlayer) player).getHandle().b.a(gameStateChange);
    }
}
