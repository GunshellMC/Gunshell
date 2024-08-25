package com.jazzkuh.gunshell.compatibility;

import com.cryptomorin.xseries.particles.XParticle;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.utils.PluginUtils;
import de.slikey.effectlib.effect.ParticleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Optional;

public interface CompatibilityLayer {
    GunshellRayTraceResult performRayTrace(LivingEntity player, double range);
    String getRayTraceResult(Player player, int range);
    void showEndCreditScene(Player player);
    void showDemoMenu(Player player);
    void sendPumpkinEffect(Player player, boolean forRemoval);
    boolean isPassable(Block block);

    void setCustomModelData(ItemStack itemStack, int customModelData);

    default void spawnParticleLine(Vector vector, LivingEntity player, int range) {
        if (!(player instanceof Player)) return;

        Location hitLocation = player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().multiply(range));
        if (vector != null) {
            hitLocation = vector.toLocation(player.getWorld());
        }

        Optional<XParticle> particle = XParticle.of(DefaultConfig.PARTICLE_TRAIL.asString());
        if (particle.isEmpty()) return;

        Location playerLocation = PluginUtils.getInstance().getRightHandLocation((Player) player);

        Vector directionVector = hitLocation.toVector().subtract(playerLocation.toVector()).normalize();
        for (double i = 0; i < playerLocation.distance(hitLocation); i += 0.2) {
            Location particleLocation = playerLocation.clone().add(directionVector.clone().multiply(i));
            ParticleEffect particleEffect = new ParticleEffect(GunshellPlugin.getInstance().getEffectManager());
            particleEffect.particle = particle.get().get();
            particleEffect.particleSize = 1;
            particleEffect.particleCount = 1;
            particleEffect.iterations = 1;
            particleEffect.particleOffsetX = 0F;
            particleEffect.particleOffsetY = 0F;
            particleEffect.particleOffsetZ = 0F;

            particleEffect.setLocation(particleLocation);
            particleEffect.start();
        }
    }
}
