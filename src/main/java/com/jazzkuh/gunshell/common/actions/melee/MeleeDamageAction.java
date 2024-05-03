package com.jazzkuh.gunshell.common.actions.melee;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.events.GunshellDeathEvent;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.common.actions.melee.abstraction.AbstractMeleeAction;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.compatibility.extensions.combattagplus.CombatTagPlusExtension;
import com.jazzkuh.gunshell.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MeleeDamageAction extends AbstractMeleeAction {
    public MeleeDamageAction(GunshellMelee melee) {
        super(melee);
    }

    @Override
    public void fireAction(LivingEntity entity, Player player, ConfigurationSection configuration) {
        if (entity.isDead()) return;
        if (entity.hasMetadata("NPC")) return;

        CompatibilityManager compatibilityManager = GunshellPlugin.getInstance().getCompatibilityManager();
        if (entity instanceof Player) {
            Player playerTarget = (Player) entity;
            if (playerTarget.getGameMode() == GameMode.SPECTATOR
                    || playerTarget.getGameMode() == GameMode.CREATIVE) return;

            if (compatibilityManager.isExtensionEnabled(CombatTagPlusExtension.class)) {
                CombatTagPlusExtension combatTagPlusExtension = (CombatTagPlusExtension) compatibilityManager.getExtension(CombatTagPlusExtension.class);
                combatTagPlusExtension.getTagManager().tag(playerTarget, player);
            }

            MessagesConfig.MELEE_HIT_BY_PLAYER.get(playerTarget,
                    new PlaceHolder("Name", player.getName()));
        }

        if (entity.getLocation().getWorld() != null) {
            entity.getLocation().getWorld().playEffect(entity.getEyeLocation(),
                    Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }

        double damage = this.getMelee().getDamage();
        damage = PluginUtils.getInstance().applyProtectionModifier(damage, false, entity);

        if (damage > entity.getHealth()) {
            entity.setHealth(0D);

            GunshellDeathEvent gunshellDeathEvent = new GunshellDeathEvent(player);
            Bukkit.getPluginManager().callEvent(gunshellDeathEvent);
        } else {
            EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, entity,
                    EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK, damage);
            entity.setHealth(entity.getHealth() - damage);

            entity.setLastDamageCause(entityDamageByEntityEvent);
            Bukkit.getPluginManager().callEvent(entityDamageByEntityEvent);
        }
    }
}
