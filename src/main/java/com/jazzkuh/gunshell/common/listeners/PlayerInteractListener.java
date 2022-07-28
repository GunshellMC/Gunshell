package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.events.FireablePreFireEvent;
import com.jazzkuh.gunshell.api.events.FireableToggleScopeEvent;
import com.jazzkuh.gunshell.api.events.ThrowablePreFireEvent;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) return;
        if (player.getInventory().getItemInMainHand().getItemMeta() == null) return;
        if (!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (NBTEditor.contains(itemStack, "gunshell_weapon_key")) {
            event.setCancelled(true);
            String weaponKey = NBTEditor.getString(itemStack, "gunshell_weapon_key");
            GunshellFireable fireable = GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

            switch (event.getAction()) {
                case LEFT_CLICK_AIR: {
                    if (!fireable.isScopeEnabled()) return;
                    FireableToggleScopeEvent scopeEvent = new FireableToggleScopeEvent(player, fireable);
                    if (scopeEvent.isCancelled()) return;
                    Bukkit.getPluginManager().callEvent(scopeEvent);
                    break;
                }
                case RIGHT_CLICK_BLOCK:
                case RIGHT_CLICK_AIR: {
                    FireablePreFireEvent fireablePreFireEvent = new FireablePreFireEvent(player, fireable);
                    if (fireablePreFireEvent.isCancelled()) return;
                    Bukkit.getPluginManager().callEvent(fireablePreFireEvent);
                    break;
                }
                default: {
                    break;
                }
            }
        } else if (NBTEditor.contains(itemStack, "gunshell_throwable_key")) {
            event.setCancelled(true);

            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            String throwableKey = NBTEditor.getString(itemStack, "gunshell_throwable_key");
            GunshellThrowable throwable = GunshellPlugin.getInstance().getWeaponRegistry().getThrowables().get(throwableKey);

            ThrowablePreFireEvent throwablePreFireEvent = new ThrowablePreFireEvent(player, throwable);
            if (throwablePreFireEvent.isCancelled()) return;
            Bukkit.getPluginManager().callEvent(throwablePreFireEvent);
        }
    }
}
