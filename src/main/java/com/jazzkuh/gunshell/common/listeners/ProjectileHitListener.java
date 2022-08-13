package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.events.FireablePreFireEvent;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class ProjectileHitListener implements Listener {
    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().hasMetadata("gunshell_grenade_bounce")) {
            Snowball grenade = (Snowball) event.getEntity();
            int count = grenade.getMetadata("gunshell_grenade_bounce").get(0).asInt();
            if (count - 1 <= 0) return;

            Snowball bouncyGrenade = grenade.getWorld().spawn(grenade.getLocation(), Snowball.class);
            ((CraftSnowball) bouncyGrenade).getHandle()
                    .a(CraftItemStack.asNMSCopy(new ItemStack(Material.STICK)), 1.0F);
            bouncyGrenade.setMetadata("gunshell_grenade_bounce",
                    new FixedMetadataValue(GunshellPlugin.getInstance(), count-1));

            bouncyGrenade.setVelocity(new Vector(
                    ThreadLocalRandom.current().nextDouble(-0.3D, 0.3D),
                    ThreadLocalRandom.current().nextDouble(0.0D, 0.5D),
                    ThreadLocalRandom.current().nextDouble(-0.3D, 0.3D)
            ));
        }
    }
}
