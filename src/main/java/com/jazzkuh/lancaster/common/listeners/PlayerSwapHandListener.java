package com.jazzkuh.lancaster.common.listeners;

import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.api.objects.LancasterAmmunition;
import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import com.jazzkuh.lancaster.utils.PluginUtils;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerSwapHandListener implements Listener {
    private final String GUN_AMMO_KEY = "lancaster_weapon_ammo";
    private final String GUN_AMMOTYPE_KEY = "lancaster_weapon_ammotype";
    private final String AMMUNITION_KEY = "lancaster_ammunition_key";
    private final String AMMUNITION_AMMO_KEY = "lancaster_ammunition_ammo";

    @EventHandler
    public void onPlayerSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (!NBTEditor.contains(itemStack, "lancaster_weapon_key")) return;
        event.setCancelled(true);

        if (LancasterPlugin.getInstance().getReloadingSet().contains(player.getUniqueId())) return;

        String weaponKey = NBTEditor.getString(itemStack, "lancaster_weapon_key");
        LancasterFireable fireable = LancasterPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

        int ammo = NBTEditor.getInt(itemStack, GUN_AMMO_KEY);

        List<String> ammunitionKeys = fireable.getAmmunitionKeys();
        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTags(player, AMMUNITION_KEY, ammunitionKeys).isEmpty()) {
            player.playSound(player.getLocation(), fireable.getEmptySound(), 100, 1F);
            return;
        }

        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTags(player, AMMUNITION_KEY, ammunitionKeys).isPresent()) {
            ItemStack ammoItem = PluginUtils.getInstance().getItemWithNBTTags(player, AMMUNITION_KEY, ammunitionKeys).get();
            int ammoAmount = NBTEditor.getInt(ammoItem, AMMUNITION_AMMO_KEY);
            LancasterPlugin.getInstance().getReloadingSet().add(player.getUniqueId());

            for (Player target : player.getLocation().getWorld().getPlayers()) {
                if (target.getLocation().distance(player.getLocation()) <= (fireable.getRange() + 2D)) {
                    target.playSound(player.getLocation(), fireable.getReloadSound(), 100, 1F);
                }
            }

            // Start reloading

            if (ammoItem.getAmount() > 1) {
                ammoItem.setAmount(ammoItem.getAmount() - 1);
            } else {
                player.getInventory().removeItem(ammoItem);
            }

            Bukkit.getScheduler().runTaskLater(LancasterPlugin.getInstance(), () -> {
                int finalAmmoAmount = ammoAmount > fireable.getMaxAmmo() ? fireable.getMaxAmmo() : ammoAmount;
                PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, finalAmmoAmount);
                PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMOTYPE_KEY, NBTEditor.getString(ammoItem, AMMUNITION_KEY));
                fireable.updateItemMeta(itemStack, finalAmmoAmount);

                LancasterPlugin.getInstance().getReloadingSet().remove(player.getUniqueId());
            }, fireable.getReloadTime());
        } else {
            LancasterAmmunition newAmmunition = LancasterPlugin.getInstance().getWeaponRegistry().getAmmunition()
                    .get(NBTEditor.getString(itemStack, GUN_AMMOTYPE_KEY));
            int ammoAmount = NBTEditor.getInt(itemStack, GUN_AMMO_KEY);
            ItemStack ammoItem = newAmmunition.getItemStack(ammoAmount);

            // Weapon has been unloaded so set the ammo to 0
            PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, 0);
            fireable.updateItemMeta(itemStack, 0);

            // Add the ammo to the player's inventory
            player.getInventory().addItem(ammoItem);
        }
    }
}