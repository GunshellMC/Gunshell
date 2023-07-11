package com.jazzkuh.gunshell.common.actions.ammunition;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.common.actions.ammunition.abstraction.AbstractAmmunitionAction;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.compatibility.extensions.WorldGuardExtension;
import com.jazzkuh.gunshell.utils.PluginUtils;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.flag.WrappedState;

public class DemoMenuAction extends AbstractAmmunitionAction {
    public DemoMenuAction(GunshellFireable fireable, GunshellAmmunition ammunition) {
        super(fireable, ammunition);
    }

    @Override
    public void fireAction(Player player, GunshellRayTraceResult rayTraceResult, ConfigurationSection configuration) {
        if (rayTraceResult.getOptionalLivingEntity().isEmpty()) return;
        LivingEntity livingEntity = rayTraceResult.getOptionalLivingEntity().get();
        if (livingEntity.isDead()) return;
        if (livingEntity.hasMetadata("NPC")) return;

        if (!this.isInMinimumRange(livingEntity, player, getFireable())) return;

        CompatibilityManager compatibilityManager = GunshellPlugin.getInstance().getCompatibilityManager();
        if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.WORLDGUARD)
                && compatibilityManager.getWorldGuardExtension().isFlagState(player, livingEntity.getLocation(),
                WorldGuardExtension.GunshellFlag.GUNSHELL_USE_WEAPONS, WrappedState.DENY)) return;

        if (livingEntity instanceof Player) {
            Player playerTarget = (Player) livingEntity;
            if (playerTarget.getGameMode() == GameMode.SPECTATOR
                    || playerTarget.getGameMode() == GameMode.CREATIVE) return;

            if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.COMBATTAGPLUS)) {
                compatibilityManager.getCombatTagPlusExtension().getTagManager().tag(playerTarget, player);
            }

            MessagesConfig.BULLET_HIT_BY_PLAYER.get(playerTarget,
                    new PlaceHolder("Name", player.getName()));

            CompatibilityLayer compatibilityLayer = GunshellPlugin.getInstance().getCompatibilityLayer();
            compatibilityLayer.showDemoMenu(playerTarget);
        }

        if (livingEntity.getLocation().getWorld() != null) {
            livingEntity.getLocation().getWorld().playEffect(livingEntity.getEyeLocation(),
                    Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }

        MessagesConfig.BULLET_HIT_OTHER.get(player,
                new PlaceHolder("Name", livingEntity.getName()));

        PluginUtils.getInstance().performRecoil(livingEntity, player, 0F, this.getFireable().getKnockbackAmount());
    }
}
