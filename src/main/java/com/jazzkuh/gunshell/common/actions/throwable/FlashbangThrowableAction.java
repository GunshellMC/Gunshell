package com.jazzkuh.gunshell.common.actions.throwable;

import com.cryptomorin.xseries.XPotion;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.actions.throwable.abstraction.AbstractThrowableAction;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.compatibility.extensions.WorldGuardExtension;
import com.jazzkuh.gunshell.utils.PluginUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.flag.WrappedState;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class FlashbangThrowableAction extends AbstractThrowableAction {
    public FlashbangThrowableAction(GunshellThrowable throwable) {
        super(throwable);
    }

    @Override
    public void fireAction(Player player, Location location, ConfigurationSection configuration) {
        Set<LivingEntity> livingEntities = location.getWorld().getNearbyEntities(location, getThrowable().getRange(), getThrowable().getRange(), getThrowable().getRange())
                .stream().filter(entity -> {
                    if (entity instanceof ArmorStand || entity instanceof ItemFrame) return false;
                    return entity instanceof LivingEntity;
                }).map(entity -> (LivingEntity) entity).collect(Collectors.toSet());
        ArrayList<Block> blocks = this.getBlocksAroundCenter(location, getThrowable().getRange());

        for (Block block : blocks) {
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 1);
        }

        int duration = configuration.getInt("options.duration", 10) * 20; // Tick based
        int amplifier = configuration.getInt("options.amplifier", 1);

        for (LivingEntity livingEntity : livingEntities) {
            CompatibilityManager compatibilityManager = GunshellPlugin.getInstance().getCompatibilityManager();
            if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.WORLDGUARD)
                    && compatibilityManager.getWorldGuardExtension().isFlagState(player, livingEntity.getLocation(),
                    WorldGuardExtension.GunshellFlag.GUNSHELL_USE_WEAPONS, WrappedState.DENY)) return;

            if (livingEntity.hasMetadata("NPC")) continue;
            if (livingEntity instanceof Player) {
                Player playerTarget = (Player) livingEntity;
                if (playerTarget.getGameMode() == GameMode.SPECTATOR
                        || playerTarget.getGameMode() == GameMode.CREATIVE) return;

                ((Player) livingEntity).playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 100, 1F);
            }

            if (livingEntity.getLocation().getWorld() != null) {
                livingEntity.getLocation().getWorld().playEffect(livingEntity.getEyeLocation(),
                        Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
            }

            PluginUtils.getInstance().performRecoil(livingEntity, 0F, this.getThrowable().getKnockbackAmount());
            livingEntity.addPotionEffect(XPotion.BLINDNESS.buildPotionEffect(duration, amplifier));
        }
    }
}
