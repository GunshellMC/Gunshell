package com.jazzkuh.gunshell.common.actions.throwable;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.events.GunshellDeathEvent;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.actions.throwable.abstraction.AbstractThrowableAction;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.compatibility.extensions.combattagplus.CombatTagPlusExtension;
import com.jazzkuh.gunshell.compatibility.extensions.worldguard.WorldGuardExtension;
import com.jazzkuh.gunshell.utils.PluginUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.codemc.worldguardwrapper.flag.WrappedState;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class ExplosiveThrowableAction extends AbstractThrowableAction {
    public ExplosiveThrowableAction(GunshellThrowable throwable) {
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

        double damage = getThrowable().getDamage();

        for (Block block : blocks) {
            block.getWorld().createExplosion(block.getLocation(), 0F, false, false);
        }

        for (LivingEntity livingEntity : livingEntities) {
            CompatibilityManager compatibilityManager = GunshellPlugin.getInstance().getCompatibilityManager();
            if (compatibilityManager.isExtensionEnabled(WorldGuardExtension.class)
                    && ((WorldGuardExtension) compatibilityManager.getExtension(WorldGuardExtension.class)).isFlagState(player, livingEntity.getLocation(),
                    WorldGuardExtension.GunshellFlag.GUNSHELL_USE_WEAPONS, WrappedState.DENY)) return;

            if (livingEntity.hasMetadata("NPC")) continue;
            if (livingEntity instanceof Player) {
                Player playerTarget = (Player) livingEntity;
                if (playerTarget.getGameMode() == GameMode.SPECTATOR
                        || playerTarget.getGameMode() == GameMode.CREATIVE) return;

                if (compatibilityManager.isExtensionEnabled(CombatTagPlusExtension.class)) {
                    CombatTagPlusExtension combatTagPlusExtension = (CombatTagPlusExtension) compatibilityManager.getExtension(CombatTagPlusExtension.class);
                    combatTagPlusExtension.getTagManager().tag(playerTarget, player);
                }
            }

            if (livingEntity.getLocation().getWorld() != null) {
                livingEntity.getLocation().getWorld().playEffect(livingEntity.getEyeLocation(),
                        Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
            }

            PluginUtils.getInstance().performRecoil(livingEntity, player, 0F, this.getThrowable().getKnockbackAmount());

            if (damage > livingEntity.getHealth()) {
                livingEntity.setHealth(0D);

                GunshellDeathEvent gunshellDeathEvent = new GunshellDeathEvent(player);
                Bukkit.getPluginManager().callEvent(gunshellDeathEvent);
            } else {
                EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, livingEntity,
                        EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK, damage);
                livingEntity.setHealth(livingEntity.getHealth() - damage);

                livingEntity.setLastDamageCause(entityDamageByEntityEvent);
                Bukkit.getPluginManager().callEvent(entityDamageByEntityEvent);
            }
        }
    }
}
