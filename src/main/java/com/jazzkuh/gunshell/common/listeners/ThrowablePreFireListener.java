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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
        armorStand.setBasePlate(false);
        armorStand.setSmall(true);
        armorStand.getEquipment().setHelmet(
                throwable.getItemStack());

        AtomicInteger maxBounces = new AtomicInteger();
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunshellPlugin.getInstance(), () -> {
            GunshellRayTraceResult rayTraceResult = GunshellPlugin.getInstance().getCompatibilityLayer().performRayTrace(armorStand, 2D);
            if (rayTraceResult.getOptionalBlock().isEmpty() && rayTraceResult.getOptionalLivingEntity().isEmpty()) {
                armorStand.setVelocity(armorStand.getVelocity().multiply(1.3D));
            } else {
                if (maxBounces.get() > 2) return;
                armorStand.setVelocity(armorStand.getEyeLocation().getDirection().multiply(-1.6D).normalize());
                maxBounces.getAndIncrement();
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
