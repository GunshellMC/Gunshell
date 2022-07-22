package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.events.FireableFireEvent;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.utils.ChatUtils;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class FireableFireListener implements Listener {
    private final String AMMO_KEY = "gunshell_weapon_ammo";
    private final String DURABILITY_KEY = "gunshell_weapon_durability";

    @EventHandler
    public void onFireableFire(FireableFireEvent event) {
        Player player = event.getPlayer();
        GunshellFireable fireable = event.getFireable();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        int ammo = NBTEditor.getInt(itemStack, AMMO_KEY);
        int durability = NBTEditor.getInt(itemStack, DURABILITY_KEY);

        if (durability <= 0) {
            player.getInventory().removeItem(itemStack);
            return;
        }

        String ammunitionKey = fireable.getAmmunitionKey();
        GunshellAmmunition ammunition = GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().get(ammunitionKey);
        if (ammunition == null) {
            MessagesConfig.ERROR_AMMUNITION_NOT_FOUND.get(player, ammunitionKey);
            return;
        }

        if (ammo <= 0) {
            return;
        }
    }

    private void performRayTrace(Player player) {
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
