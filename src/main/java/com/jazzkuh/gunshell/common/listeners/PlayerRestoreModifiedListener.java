package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.enums.PlayerTempModification;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class PlayerRestoreModifiedListener implements Listener {
    @EventHandler
    public void onPreviousPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItem(event.getPreviousSlot()) == null) return;

        ItemStack itemStack = player.getInventory().getItem(event.getPreviousSlot());
        if (itemStack == null) return;

        if (!NBTEditor.contains(itemStack, "gunshell_weapon_key")) return;
        String weaponKey = NBTEditor.getString(itemStack, "gunshell_weapon_key");
        GunshellFireable fireable = GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

        CompatibilityLayer compatibilityLayer = GunshellPlugin.getInstance().getCompatibilityLayer();
        if (GunshellPlugin.getInstance().getModifiedPlayerMap().containsKey(player.getUniqueId())
                && GunshellPlugin.getInstance().getModifiedPlayerMap().get(player.getUniqueId()) == PlayerTempModification.SCOPED) {
            if (player.hasPotionEffect(PotionEffectType.SLOW)) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
            if (fireable.isScopePumpkinBlurEnabled()) {
                compatibilityLayer.sendPumpkinEffect(player, true);
            }
            GunshellPlugin.getInstance().getModifiedPlayerMap().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        ItemStack itemStack = event.getItemDrop().getItemStack();

        if (!NBTEditor.contains(itemStack, "gunshell_weapon_key")) return;
        String weaponKey = NBTEditor.getString(itemStack, "gunshell_weapon_key");
        GunshellFireable fireable = GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

        CompatibilityLayer compatibilityLayer = GunshellPlugin.getInstance().getCompatibilityLayer();
        if (GunshellPlugin.getInstance().getModifiedPlayerMap().containsKey(player.getUniqueId())
                && GunshellPlugin.getInstance().getModifiedPlayerMap().get(player.getUniqueId()) == PlayerTempModification.SCOPED) {
            if (player.hasPotionEffect(PotionEffectType.SLOW)) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
            if (fireable.isScopePumpkinBlurEnabled()) {
                compatibilityLayer.sendPumpkinEffect(player, true);
            }
            GunshellPlugin.getInstance().getModifiedPlayerMap().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        ItemStack itemStack = event.getOffHandItem();
        if (itemStack == null) return;

        if (!NBTEditor.contains(itemStack, "gunshell_weapon_key")) return;
        String weaponKey = NBTEditor.getString(itemStack, "gunshell_weapon_key");
        GunshellFireable fireable = GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

        CompatibilityLayer compatibilityLayer = GunshellPlugin.getInstance().getCompatibilityLayer();
        if (GunshellPlugin.getInstance().getModifiedPlayerMap().containsKey(player.getUniqueId())
                && GunshellPlugin.getInstance().getModifiedPlayerMap().get(player.getUniqueId()) == PlayerTempModification.SCOPED) {
            if (player.hasPotionEffect(PotionEffectType.SLOW)) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
            if (fireable.isScopePumpkinBlurEnabled()) {
                compatibilityLayer.sendPumpkinEffect(player, true);
            }
            GunshellPlugin.getInstance().getModifiedPlayerMap().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;

        if (!NBTEditor.contains(itemStack, "gunshell_weapon_key")) return;
        String weaponKey = NBTEditor.getString(itemStack, "gunshell_weapon_key");
        GunshellFireable fireable = GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

        CompatibilityLayer compatibilityLayer = GunshellPlugin.getInstance().getCompatibilityLayer();
        if (GunshellPlugin.getInstance().getModifiedPlayerMap().containsKey(player.getUniqueId())
                && GunshellPlugin.getInstance().getModifiedPlayerMap().get(player.getUniqueId()) == PlayerTempModification.SCOPED) {
            if (player.hasPotionEffect(PotionEffectType.SLOW)) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
            if (fireable.isScopePumpkinBlurEnabled()) {
                compatibilityLayer.sendPumpkinEffect(player, true);
            }
            GunshellPlugin.getInstance().getModifiedPlayerMap().remove(player.getUniqueId());
        }
    }
}
