package com.jazzkuh.lancaster.common.listeners;

import com.jazzkuh.lancaster.LancasterPlugin;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemHeldListener implements Listener {
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (LancasterPlugin.getInstance().getReloadingSet().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        LancasterPlugin.getInstance().getGrabCooldownMap().remove(player.getUniqueId());
        if (event.getPlayer().getInventory().getItem(event.getNewSlot()) == null) return;
        ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (itemStack == null) return;
        if (NBTEditor.contains(itemStack, "lancaster_weapon_key")) {
            LancasterPlugin.getInstance().getGrabCooldownMap().put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}
