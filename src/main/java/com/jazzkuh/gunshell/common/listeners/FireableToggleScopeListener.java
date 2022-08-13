package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.enums.PlayerTempModification;
import com.jazzkuh.gunshell.api.events.FireableFireEvent;
import com.jazzkuh.gunshell.api.events.FireablePreFireEvent;
import com.jazzkuh.gunshell.api.events.FireableToggleScopeEvent;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.common.AmmunitionActionRegistry;
import com.jazzkuh.gunshell.common.actions.ammunition.abstraction.AmmunitionActionImpl;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.PluginUtils;
import de.slikey.effectlib.effect.ParticleEffect;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

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
