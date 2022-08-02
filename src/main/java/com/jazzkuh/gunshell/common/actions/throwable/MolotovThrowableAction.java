package com.jazzkuh.gunshell.common.actions.throwable;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.actions.throwable.abstraction.AbstractThrowableAction;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import com.jazzkuh.gunshell.compatibility.extensions.WorldGuardExtension;
import com.jazzkuh.gunshell.utils.PluginUtils;
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
    public MolotovThrowableAction(GunshellThrowable throwable) {
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

        CompatibilityManager compatibilityManager = GunshellPlugin.getInstance().getCompatibilityManager();
        Set<Block> tempUndoList = new HashSet<>();
        for (Block block : blocks) {
            if (compatibilityManager.getWorldGuardExtension().isFlagState(player, block.getLocation(), WorldGuardExtension.GunshellFlag.GUNSHELL_USE_WEAPONS, WrappedState.ALLOW)) continue;

            if (block.getType() == Material.AIR) {
                GunshellPlugin.getInstance().getUndoList().add(block);
                tempUndoList.add(block);
                block.setType(XMaterial.FIRE.parseMaterial());
            } else {
                if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                    GunshellPlugin.getInstance().getUndoList().add(block.getRelative(BlockFace.UP));
                    tempUndoList.add(block.getRelative(BlockFace.UP));
                    block.getRelative(BlockFace.UP).setType(XMaterial.FIRE.parseMaterial());
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(GunshellPlugin.getInstance(), () -> {
            for (Block block : tempUndoList) {
                block.setType(Material.AIR);
                GunshellPlugin.getInstance().getUndoList().remove(block);
            }
            tempUndoList.clear();
        }, configuration.getInt("options.duration", 3) * 20L);
    }
}
