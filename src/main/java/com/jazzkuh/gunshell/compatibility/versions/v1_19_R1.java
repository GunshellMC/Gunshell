package com.jazzkuh.gunshell.compatibility.versions;

import com.cryptomorin.xseries.XMaterial;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
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
                .rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, DefaultConfig.HITBOX_INCREASE.asDouble(), entity ->
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
                .rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, DefaultConfig.HITBOX_INCREASE.asDouble(), null);
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

    @Override
    public void sendPumpkinEffect(Player player, boolean forRemoval) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        org.bukkit.inventory.ItemStack itemStack = XMaterial.AIR.parseItem();
        if (!forRemoval) {
            itemStack = XMaterial.PUMPKIN.parseItem();
        }

        craftPlayer.getHandle().b.a(new PacketPlayOutSetSlot(0, 0, 5,
                CraftItemStack.asNMSCopy(itemStack)));
    }

    @Override
    public boolean isPassable(Block block) {
        return block.isPassable();
    }
}
