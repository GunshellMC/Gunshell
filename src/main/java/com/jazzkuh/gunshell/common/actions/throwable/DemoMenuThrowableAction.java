package com.jazzkuh.gunshell.common.actions.throwable;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.AbstractThrowableAction;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.utils.PluginUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class DemoMenuThrowableAction extends AbstractThrowableAction {
    public DemoMenuThrowableAction(GunshellThrowable throwable) {
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

        for (LivingEntity livingEntity : livingEntities) {
            if (livingEntity instanceof Player) {
                Player playerTarget = (Player) livingEntity;
                if (playerTarget.getGameMode() == GameMode.SPECTATOR
                        || playerTarget.getGameMode() == GameMode.CREATIVE) return;

                CompatibilityLayer compatibilityLayer = GunshellPlugin.getInstance().getCompatibilityManager().getCompatibilityLayer();
                compatibilityLayer.showDemoMenu(playerTarget);
            }

            if (livingEntity.getLocation().getWorld() != null) {
                livingEntity.getLocation().getWorld().playEffect(livingEntity.getEyeLocation(),
                        Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
            }

            PluginUtils.getInstance().performRecoil(livingEntity, 0F, this.getThrowable().getKnockbackAmount());
        }
    }
}
