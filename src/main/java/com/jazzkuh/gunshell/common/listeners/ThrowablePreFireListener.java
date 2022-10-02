package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.events.ThrowablePreFireEvent;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.ThrowableActionRegistry;
import com.jazzkuh.gunshell.common.actions.throwable.abstraction.ThrowableActionImpl;
import com.jazzkuh.gunshell.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.UUID;

public class ThrowablePreFireListener implements Listener {
    @EventHandler
    public void onThrowablePreFire(ThrowablePreFireEvent event) {
        Player player = event.getPlayer();
        GunshellThrowable throwable = event.getThrowable();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (hasThrowableCooldown(player.getUniqueId(), throwable)) return;
        GunshellPlugin.getInstance().getThrowableCooldownMap().put(player.getUniqueId(), System.currentTimeMillis());

        if (itemStack.getAmount() > 1) {
            player.getInventory().getItemInMainHand().setAmount(itemStack.getAmount() - 1);
        } else {
            player.getInventory().removeItem(itemStack);
        }

        ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        armorStand.setMetadata("gunshell_throwable_armorstand", new FixedMetadataValue(GunshellPlugin.getInstance(), true));
        armorStand.setVelocity(player.getEyeLocation().getDirection().multiply(1.3D));
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setInvulnerable(true);
        armorStand.getEquipment().setHelmet(
                throwable.getItemStack());


        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunshellPlugin.getInstance(), () -> {
            int calculations = 20;

            Vector velocity = armorStand.getVelocity();
            Vector interval = velocity.clone().multiply(1.0 / calculations);
            Location tempLocation = armorStand.getLocation().clone();

            for (int i = 0; i < calculations; i++) {
                Location tempLocationX = tempLocation.clone().add(interval.getX(), 0, 0);
                Location tempLocationY = tempLocation.clone().add(0, interval.getY(), 0);
                Location tempLocationZ = tempLocation.clone().add(0, 0, interval.getZ());

                Block blockX = tempLocationX.getBlock();
                Block blockY = tempLocationY.getBlock();
                Block blockZ = tempLocationZ.getBlock();

                boolean xPassable = !GunshellPlugin.getInstance().getCompatibilityLayer().isPassable(blockX) && !blockX.isLiquid();
                boolean yPassable = !GunshellPlugin.getInstance().getCompatibilityLayer().isPassable(blockY) && !blockY.isLiquid();
                boolean zPassable = !GunshellPlugin.getInstance().getCompatibilityLayer().isPassable(blockZ) && !blockZ.isLiquid();

                boolean xCollide = xPassable;
                boolean yCollide = yPassable;
                boolean zCollide = zPassable;

                if (!xPassable && !yPassable) {
                    Block blockXY = tempLocation.clone().add(interval.getX(), interval.getY(), 0).getBlock();
                    if (!GunshellPlugin.getInstance().getCompatibilityLayer().isPassable(blockXY) && !blockXY.isLiquid()) {
                        xCollide = true;
                        yCollide = true;
                    }
                }

                if (!xPassable && !zPassable) {
                    var blockXZ = tempLocation.clone().add(interval.getX(), 0, interval.getZ()).getBlock();
                    if (!GunshellPlugin.getInstance().getCompatibilityLayer().isPassable(blockXZ) && !blockXZ.isLiquid()) {
                        xCollide = true;
                        zCollide = true;
                    }
                }

                if (!yPassable && !zPassable) {
                    var blockYZ = tempLocation.clone().add(0, interval.getY(), interval.getZ()).getBlock();
                    if (!GunshellPlugin.getInstance().getCompatibilityLayer().isPassable(blockYZ) && !blockYZ.isLiquid()) {
                        yCollide = true;
                        zCollide = true;
                    }
                }

                if (xCollide) {
                    velocity.setX(-0.6 * velocity.getX());
                    interval.setX(-0.6 * interval.getX());
                }

                if (yCollide) {
                    velocity.setY(-0.6 * velocity.getY());
                    interval.setY(-0.6 * interval.getY());
                }

                if (zCollide) {
                    velocity.setZ(-0.6 * velocity.getZ());
                    interval.setZ(-0.6 * interval.getZ());
                }

                tempLocation.add(interval);
                armorStand.setVelocity(velocity);
            }
        }, 5, 1);

        GunshellPlugin.getInstance().getActiveThrowables().put(armorStand, taskId);
        Bukkit.getScheduler().runTaskLater(GunshellPlugin.getInstance(), () -> {
            GunshellPlugin.getInstance().getActiveThrowables().remove(armorStand);
            Bukkit.getScheduler().cancelTask(taskId);

            Location location = armorStand.getLocation();
            armorStand.remove();

            ThrowableActionImpl throwableAction = ThrowableActionRegistry.getAction(throwable, throwable.getActionType());
            if (throwableAction == null) {
                ChatUtils.sendMessage(player, "&cNo throwable action found for this throwable.");
                return;
            }

            throwableAction.fireAction(player, location, throwable.getConfiguration());
        }, throwable.getFuseTime());
    }

    private boolean hasThrowableCooldown(UUID uniqueId, GunshellThrowable throwable) {
        Long lastUsed = GunshellPlugin.getInstance().getThrowableCooldownMap().getOrDefault(uniqueId, 0L);
        return System.currentTimeMillis() <= (lastUsed + (throwable.getCooldown()));
    }
}
