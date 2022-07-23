package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.PluginUtils;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PlayerSwapHandListener implements Listener {
    private final String GUN_AMMO_KEY = "gunshell_weapon_ammo";
    private final String DURABILITY_KEY = "gunshell_weapon_durability";

    private final String AMMUNITION_KEY = "gunshell_ammunition_key";
    private final String AMMUNITION_AMMO_KEY = "gunshell_ammunition_ammo";

    @EventHandler
    public void onPlayerSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (!NBTEditor.contains(itemStack, "gunshell_weapon_key")) return;
        event.setCancelled(true);

        if (GunshellPlugin.getInstance().getReloadingSet().contains(player.getUniqueId())) return;

        String weaponKey = NBTEditor.getString(itemStack, "gunshell_weapon_key");
        GunshellFireable fireable = GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

        int ammo = NBTEditor.getInt(itemStack, GUN_AMMO_KEY);
        int durability = NBTEditor.getInt(itemStack, DURABILITY_KEY);

        if (durability <= 0) {
            player.getInventory().removeItem(itemStack);
            return;
        }

        String ammunitionKey = fireable.getAmmunitionKey();
        GunshellAmmunition ammunition = GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().get(ammunitionKey);
        if (ammunition == null) {
            MessagesConfig.ERROR_AMMUNITION_NOT_FOUND.get(player,
                    new PlaceHolder("Key", ammunitionKey));
            return;
        }

        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTag(player, AMMUNITION_KEY, ammunitionKey).isEmpty()) {
            MessagesConfig.ERROR_OUT_OF_AMMO.get(player);
            return;
        }

        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTag(player, AMMUNITION_KEY, ammunitionKey).isPresent()) {
            ItemStack ammoItem = PluginUtils.getInstance().getItemWithNBTTag(player, AMMUNITION_KEY, ammunitionKey).get();
            int ammoAmount = NBTEditor.getInt(ammoItem, AMMUNITION_AMMO_KEY);
            GunshellPlugin.getInstance().getReloadingSet().add(player.getUniqueId());

            MessagesConfig.RELOADING_START.get(player);

            if (ammoItem.getAmount() > 1) {
                ammoItem.setAmount(ammoItem.getAmount() - 1);
            } else {
                player.getInventory().removeItem(ammoItem);
            }

            Bukkit.getScheduler().runTaskLater(GunshellPlugin.getInstance(), () -> {
                int finalAmmoAmount = ammoAmount > fireable.getMaxAmmo() ? fireable.getMaxAmmo() : ammoAmount;
                PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, finalAmmoAmount);
                this.updateFireableItemMeta(itemStack, fireable, finalAmmoAmount);

                MessagesConfig.SHOW_AMMO_DURABILITY.get(player,
                        new PlaceHolder("Durability", String.valueOf(durability)),
                        new PlaceHolder("Ammo", String.valueOf(finalAmmoAmount)),
                        new PlaceHolder("MaxAmmo", String.valueOf(fireable.getMaxAmmo())));

                GunshellPlugin.getInstance().getReloadingSet().remove(player.getUniqueId());
                MessagesConfig.RELOADING_FINISHED.get(player);
            }, fireable.getReloadTime());
        } else {
            int ammoAmount = NBTEditor.getInt(itemStack, GUN_AMMO_KEY);
            GunshellAmmunition newAmmunition = GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().get(ammunitionKey);
            ItemStack ammoItem = newAmmunition.getItemStack(ammoAmount);

            // Weapon has been unloaded so set the ammo to 0
            PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, 0);
            this.updateFireableItemMeta(itemStack, fireable, 0);

            // Add the ammo to the player's inventory
            player.getInventory().addItem(ammoItem);
            MessagesConfig.UNLOADING_FINISHED.get(player);
        }
    }

    private void updateFireableItemMeta(ItemStack itemStack, GunshellFireable fireable, int ammo) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = fireable.getLore();

        itemMeta.setLore(ChatUtils.color(lore,
                new PlaceHolder("Ammo", String.valueOf(ammo)),
                new PlaceHolder("MaxAmmo", String.valueOf(fireable.getMaxAmmo())),
                new PlaceHolder("Damage", String.valueOf(fireable.getDamage())),
                new PlaceHolder("Durability", String.valueOf(NBTEditor.getInt(itemStack, DURABILITY_KEY)))));
        itemStack.setItemMeta(itemMeta);
    }
}