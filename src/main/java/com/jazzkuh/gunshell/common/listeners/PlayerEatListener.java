package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.utils.NBTEditor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

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
