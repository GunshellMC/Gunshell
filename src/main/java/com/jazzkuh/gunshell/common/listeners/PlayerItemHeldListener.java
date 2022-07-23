package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
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

        if (GunshellPlugin.getInstance().getReloadingSet().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        GunshellPlugin.getInstance().getGrabCooldownMap().remove(player.getUniqueId());
        if (event.getPlayer().getInventory().getItem(event.getNewSlot()) == null) return;
        ItemStack itemStack = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (itemStack == null) return;
        if (!NBTEditor.contains(itemStack, "gunshell_weapon_key")) return;
        String weaponKey = NBTEditor.getString(itemStack, "gunshell_weapon_key");
        GunshellFireable fireable = GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

        GunshellPlugin.getInstance().getGrabCooldownMap().put(player.getUniqueId(), System.currentTimeMillis());

        int ammo = NBTEditor.getInt(itemStack, "gunshell_weapon_ammo");
        int durability = NBTEditor.getInt(itemStack, "gunshell_weapon_durability");

        MessagesConfig.SHOW_AMMO_DURABILITY.get(player,
                new PlaceHolder("Durability", String.valueOf(durability)),
                new PlaceHolder("Ammo", String.valueOf(ammo)),
                new PlaceHolder("MaxAmmo", String.valueOf(fireable.getMaxAmmo())));
    }
}
