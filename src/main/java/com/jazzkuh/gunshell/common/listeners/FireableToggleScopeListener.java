package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.enums.PlayerTempModification;
import com.jazzkuh.gunshell.api.events.FireableToggleScopeEvent;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FireableToggleScopeListener implements Listener {
    @EventHandler
    public void onFireableToggleScope(FireableToggleScopeEvent event) {
        Player player = event.getPlayer();
        GunshellFireable fireable = event.getFireable();
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
        } else {
            int scopeAmplifier = fireable.getScopeAmplifier();
            GunshellPlugin.getInstance().getModifiedPlayerMap().put(player.getUniqueId(), PlayerTempModification.SCOPED);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, scopeAmplifier, true));
            if (fireable.isScopePumpkinBlurEnabled()) {
                compatibilityLayer.sendPumpkinEffect(player, false);
            }
        }
    }
}
