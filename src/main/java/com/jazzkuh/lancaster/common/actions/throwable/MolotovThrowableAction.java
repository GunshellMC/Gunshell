package com.jazzkuh.lancaster.common.actions.throwable;

import com.cryptomorin.xseries.XMaterial;
import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.api.objects.LancasterThrowable;
import com.jazzkuh.lancaster.common.actions.throwable.abstraction.AbstractThrowableAction;
import com.jazzkuh.lancaster.compatibility.CompatibilityManager;
import com.jazzkuh.lancaster.compatibility.extensions.WorldGuardExtension;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.flag.WrappedState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MolotovThrowableAction extends AbstractThrowableAction {
    public MolotovThrowableAction(LancasterThrowable throwable) {
        super(throwable);
    }

    @Override
    public void fireAction(Player player, Location location, ConfigurationSection configuration) {
        Set<LivingEntity> livingEntities = location.getWorld().getNearbyEntities(location, getThrowable().getRange(), getThrowable().getRange(), getThrowable().getRange())
                .stream().filter(entity -> {
                    if (entity instanceof ArmorStand || entity instanceof ItemFrame) return false;
                    return entity instanceof LivingEntity;
                }).map(entity -> (LivingEntity) entity).collect(Collectors.toSet());
        ArrayList<Block> blocks = this.getGroundBlockAroundCenter(location, 2);

        for (LivingEntity livingEntity : livingEntities) {
            if (livingEntity instanceof Player) {
                ((Player) livingEntity).playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 100, 1F);
            }
        }

        CompatibilityManager compatibilityManager = LancasterPlugin.getInstance().getCompatibilityManager();
        Set<Block> tempUndoList = new HashSet<>();
        for (Block block : blocks) {
            if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.WORLDGUARD) &&
                    compatibilityManager.getWorldGuardExtension().isFlagState(player, block.getLocation(), WorldGuardExtension.LancasterFlag.USE_WEAPONS, WrappedState.DENY)) continue;

            if (block.getType() == Material.AIR) {
                LancasterPlugin.getInstance().getUndoList().add(block);
                tempUndoList.add(block);
                block.setType(XMaterial.FIRE.parseMaterial());
            } else {
                if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                    LancasterPlugin.getInstance().getUndoList().add(block.getRelative(BlockFace.UP));
                    tempUndoList.add(block.getRelative(BlockFace.UP));
                    block.getRelative(BlockFace.UP).setType(XMaterial.FIRE.parseMaterial());
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(LancasterPlugin.getInstance(), () -> {
            for (Block block : tempUndoList) {
                block.setType(Material.AIR);
                LancasterPlugin.getInstance().getUndoList().remove(block);
            }
            tempUndoList.clear();
        }, configuration.getInt("options.duration", 3) * 20L);
    }
}
