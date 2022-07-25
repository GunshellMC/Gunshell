package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.events.ThrowablePreFireEvent;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.ThrowableActionImpl;
import com.jazzkuh.gunshell.common.ThrowableActionRegistry;
import com.jazzkuh.gunshell.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

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
        armorStand.setVelocity(player.getEyeLocation().getDirection().multiply(1.3D));
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.getEquipment().setHelmet(
                throwable.getItemStack());

        AtomicInteger bounces = new AtomicInteger(0);
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunshellPlugin.getInstance(), () -> {
            GunshellRayTraceResult rayTraceResult = GunshellPlugin.getInstance().getCompatibilityManager().getCompatibilityLayer().performRayTrace(armorStand, 2.1D);
            if (rayTraceResult.getOptionalBlock().isEmpty() && rayTraceResult.getOptionalLivingEntity().isEmpty()) {
                double velocity = (1.2 - (0.2 * bounces.get()));
                if (velocity < 0.1) velocity = 0.1;
                double finalVelocity = velocity;
                armorStand.setVelocity(armorStand.getVelocity().multiply(finalVelocity));
            } else {
                double velocity = (1.6 - (0.4 * bounces.get()));
                if (velocity < 0.1) velocity = 0.1;
                double finalVelocity = rayTraceResult.getBlockFace() == BlockFace.UP ? velocity : -velocity;
                armorStand.setVelocity(armorStand.getEyeLocation().getDirection().multiply(finalVelocity).normalize());
                bounces.getAndIncrement();
            }
        }, 0L, 5L);

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
