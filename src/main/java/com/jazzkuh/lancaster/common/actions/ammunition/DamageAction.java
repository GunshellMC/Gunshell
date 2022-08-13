package com.jazzkuh.lancaster.common.actions.ammunition;

import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.api.objects.LancasterAmmunition;
import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import com.jazzkuh.lancaster.api.objects.LancasterRayTraceResult;
import com.jazzkuh.lancaster.common.actions.ammunition.abstraction.AbstractAmmunitionAction;
import com.jazzkuh.lancaster.compatibility.CompatibilityManager;
import com.jazzkuh.lancaster.compatibility.extensions.WorldGuardExtension;
import com.jazzkuh.lancaster.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.codemc.worldguardwrapper.flag.WrappedState;

public class DamageAction extends AbstractAmmunitionAction {
    public DamageAction(LancasterFireable fireable, LancasterAmmunition ammunition) {
        super(fireable, ammunition);
    }

    @Override
    public void fireAction(Player player, LancasterRayTraceResult rayTraceResult, ConfigurationSection configuration) {
        if (rayTraceResult.getOptionalLivingEntity().isEmpty()) return;
        LivingEntity livingEntity = rayTraceResult.getOptionalLivingEntity().get();
        if (livingEntity.isDead()) return;
        if (livingEntity.hasMetadata("NPC")) return;

        if (!this.isInMinimumRange(livingEntity, player, getFireable())) return;

        CompatibilityManager compatibilityManager = LancasterPlugin.getInstance().getCompatibilityManager();
        if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.WORLDGUARD)
                && compatibilityManager.getWorldGuardExtension().isFlagState(player, livingEntity.getLocation(),
                WorldGuardExtension.LancasterFlag.USE_WEAPONS, WrappedState.DENY)) return;

        if (livingEntity instanceof Player) {
            Player playerTarget = (Player) livingEntity;
            if (playerTarget.getGameMode() == GameMode.SPECTATOR
                    || playerTarget.getGameMode() == GameMode.CREATIVE) return;
        }

        if (livingEntity.getLocation().getWorld() != null) {
            livingEntity.getLocation().getWorld().playEffect(livingEntity.getEyeLocation(),
                    Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }

        double damage = this.getFireable().getDamage();
        if (rayTraceResult.isHeadshot()) {
            damage = this.getFireable().getHeadshotDamage();
        }

        PluginUtils.getInstance().performRecoil(livingEntity, 0F, this.getFireable().getKnockbackAmount());

        if (damage > livingEntity.getHealth()) {
            livingEntity.setHealth(0D);
        } else {
            EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, livingEntity,
                    EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK, damage);
            livingEntity.setHealth(livingEntity.getHealth() - damage);

            livingEntity.setLastDamageCause(entityDamageByEntityEvent);
            Bukkit.getPluginManager().callEvent(entityDamageByEntityEvent);
        }
    }
}
