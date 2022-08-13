package com.jazzkuh.lancaster.common.listeners;

import com.jazzkuh.core.utils.ChatUtils;
import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.api.events.FireablePreFireEvent;
import com.jazzkuh.lancaster.api.events.FireableToggleScopeEvent;
import com.jazzkuh.lancaster.api.events.ThrowablePreFireEvent;
import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import com.jazzkuh.lancaster.api.objects.LancasterThrowable;
import com.jazzkuh.lancaster.compatibility.CompatibilityManager;
import com.jazzkuh.lancaster.compatibility.extensions.WorldGuardExtension;
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
import org.codemc.worldguardwrapper.flag.WrappedState;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) return;
        if (player.getInventory().getItemInMainHand().getItemMeta() == null) return;
        if (!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        CompatibilityManager compatibilityManager = LancasterPlugin.getInstance().getCompatibilityManager();
        if (NBTEditor.contains(itemStack, "lancaster_weapon_key")) {
            event.setCancelled(true);
            String weaponKey = NBTEditor.getString(itemStack, "lancaster_weapon_key");
            LancasterFireable fireable = LancasterPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

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

                    if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.WORLDGUARD)
                            && compatibilityManager.getWorldGuardExtension().isFlagState(player, player.getLocation(),
                            WorldGuardExtension.LancasterFlag.USE_WEAPONS, WrappedState.DENY)) {
                        fireablePreFireEvent.setCancelled(true);
                        ChatUtils.sendMessage(player, "<error>You are not allowed to use weapons here.");
                        return;
                    }

                    if (fireablePreFireEvent.isCancelled()) return;
                    Bukkit.getPluginManager().callEvent(fireablePreFireEvent);
                    break;
                }
                default: {
                    break;
                }
            }
        } else if (NBTEditor.contains(itemStack, "lancaster_throwable_key")) {
            event.setCancelled(true);

            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            String throwableKey = NBTEditor.getString(itemStack, "lancaster_throwable_key");
            LancasterThrowable throwable = LancasterPlugin.getInstance().getWeaponRegistry().getThrowables().get(throwableKey);

            ThrowablePreFireEvent throwablePreFireEvent = new ThrowablePreFireEvent(player, throwable);

            if (compatibilityManager.isExtensionEnabled(CompatibilityManager.Extension.WORLDGUARD)
                    && compatibilityManager.getWorldGuardExtension().isFlagState(player, player.getLocation(),
                    WorldGuardExtension.LancasterFlag.USE_WEAPONS, WrappedState.DENY)) {
                throwablePreFireEvent.setCancelled(true);
                ChatUtils.sendMessage(player, "<error>You are not allowed to use throwables here.");
                return;
            }

            if (throwablePreFireEvent.isCancelled()) return;
            Bukkit.getPluginManager().callEvent(throwablePreFireEvent);
        }
    }
}
