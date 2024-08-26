package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.utils.NBTEditor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerEatListener implements Listener {
    @EventHandler
    public void onProjectileHit(final PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) return;
        if (!NBTEditor.contains(player.getInventory().getItemInMainHand(), "gunshell_weapon_key")) return;
        if (!NBTEditor.contains(player.getInventory().getItemInMainHand(), "gunshell_melee_key")) return;
        if (!NBTEditor.contains(player.getInventory().getItemInMainHand(), "gunshell_throwable_key")) return;

        event.setCancelled(true);
    }
}
