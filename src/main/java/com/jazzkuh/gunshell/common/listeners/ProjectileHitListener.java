package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
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
