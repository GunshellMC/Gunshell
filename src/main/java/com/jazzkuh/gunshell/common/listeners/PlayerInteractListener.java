package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) return;
        if (player.getInventory().getItemInMainHand().getItemMeta() == null) return;
        if (!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() != Material.STICK) return;
        CompatibilityLayer compatibilityLayer = GunshellPlugin.getInstance().getCompatibilityLayer();

        Entity entity = compatibilityLayer.performRayTrace(player, 20);
        if (!(entity instanceof LivingEntity) || entity instanceof ArmorStand) {
            ChatUtils.sendMessage(player, "&cNo entity found.");
            return;
        }

        LivingEntity livingEntity = (LivingEntity) entity;
        if (livingEntity.isDead()) return;
        ChatUtils.sendMessage(player, "&aEntity found: " + livingEntity.getType().name());
        if (livingEntity.getLocation().getWorld() != null) {
            livingEntity.getLocation().getWorld().playEffect(livingEntity.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }

        double damage = 5;
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
