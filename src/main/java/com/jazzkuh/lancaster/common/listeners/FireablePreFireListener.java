package com.jazzkuh.lancaster.common.listeners;

import com.jazzkuh.core.utils.ChatUtils;
import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.api.enums.PlayerTempModification;
import com.jazzkuh.lancaster.api.events.FireableFireEvent;
import com.jazzkuh.lancaster.api.events.FireablePreFireEvent;
import com.jazzkuh.lancaster.api.objects.LancasterAmmunition;
import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import com.jazzkuh.lancaster.api.objects.LancasterRayTraceResult;
import com.jazzkuh.lancaster.common.AmmunitionActionRegistry;
import com.jazzkuh.lancaster.common.actions.ammunition.abstraction.AmmunitionActionImpl;
import com.jazzkuh.lancaster.common.configuration.DefaultConfig;
import com.jazzkuh.lancaster.compatibility.CompatibilityLayer;
import com.jazzkuh.lancaster.utils.PluginUtils;
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
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

public class FireablePreFireListener implements Listener {
    private final String GUN_AMMO_KEY = "lancaster_weapon_ammo";
    private final String GUN_AMMOTYPE_KEY = "lancaster_weapon_ammotype";
    private final String CONDITION_KEY = "lancaster_weapon_condition";

    private final String AMMUNITION_KEY = "lancaster_ammunition_key";
    private final String AMMUNITION_AMMO_KEY = "lancaster_ammunition_ammo";

    @EventHandler
    public void onFireablePreFire(FireablePreFireEvent event) {
        Player player = event.getPlayer();
        LancasterFireable fireable = event.getFireable();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        String cooldownKey = DefaultConfig.PER_WEAPON_COOLDOWN.asBoolean() ?
                player.getUniqueId() + "_" + player.getInventory().getHeldItemSlot() :
                player.getUniqueId() + "_global";

        if (LancasterPlugin.getInstance().getReloadingSet().contains(player.getUniqueId())) return;

        int ammo = NBTEditor.getInt(itemStack, GUN_AMMO_KEY);
        int condition = NBTEditor.getInt(itemStack, CONDITION_KEY);

        List<String> ammunitionKeys = fireable.getAmmunitionKeys();
        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTags(player, AMMUNITION_KEY, ammunitionKeys).isEmpty()) {
            player.playSound(player.getLocation(), fireable.getEmptySound(), 100, 1F);
            return;
        }

        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTags(player, AMMUNITION_KEY, ammunitionKeys).isPresent()) {
            ItemStack ammoItem = PluginUtils.getInstance().getItemWithNBTTags(player, AMMUNITION_KEY, ammunitionKeys).get();
            int ammoAmount = NBTEditor.getInt(ammoItem, AMMUNITION_AMMO_KEY);
            LancasterPlugin.getInstance().getReloadingSet().add(player.getUniqueId());

            for (Player target : player.getLocation().getWorld().getPlayers()) {
                if (target.getLocation().distance(player.getLocation()) <= (fireable.getRange() + 2D)) {
                    target.playSound(player.getLocation(), fireable.getReloadSound(), 100, 1F);
                }
            }

            // Start reloading

            if (ammoItem.getAmount() > 1) {
                ammoItem.setAmount(ammoItem.getAmount() - 1);
            } else {
                player.getInventory().removeItem(ammoItem);
            }

            Bukkit.getScheduler().runTaskLater(LancasterPlugin.getInstance(), () -> {
                int finalAmmoAmount = ammoAmount > fireable.getMaxAmmo() ? fireable.getMaxAmmo() : ammoAmount;
                PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, finalAmmoAmount);
                PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMOTYPE_KEY, NBTEditor.getString(ammoItem, AMMUNITION_KEY));
                fireable.updateItemMeta(itemStack, finalAmmoAmount);

                LancasterPlugin.getInstance().getReloadingSet().remove(player.getUniqueId());
            }, fireable.getReloadTime());
            return;
        }

        LancasterAmmunition ammunition = LancasterPlugin.getInstance().getWeaponRegistry().getAmmunition()
                .get(NBTEditor.getString(itemStack, GUN_AMMOTYPE_KEY));
        if (ammunition == null) {
            ChatUtils.sendMessage(player, "<error>Error: Ammunition not found!");
            return;
        }

        if (hasCooldown(cooldownKey, fireable) || hasGrabCooldown(player.getUniqueId(), fireable)) return;

        FireableFireEvent fireableFireEvent = new FireableFireEvent(player, fireable);
        if (fireableFireEvent.isCancelled()) return;
        Bukkit.getPluginManager().callEvent(fireableFireEvent);

        /*
         * Perform the raytrace to find the target
         */
        CompatibilityLayer compatibilityLayer = LancasterPlugin.getInstance().getCompatibilityLayer();
        LancasterRayTraceResult rayTraceResult = compatibilityLayer.performRayTrace(player, fireable.getRange());

        LancasterPlugin.getInstance().getWeaponCooldownMap().put(cooldownKey, System.currentTimeMillis());
        PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, ammo - 1);
        PluginUtils.getInstance().applyNBTTag(itemStack, CONDITION_KEY, condition - 1);
        fireable.updateItemMeta(itemStack, ammo - 1);

        PluginUtils.getInstance().performRecoil(player,
                (float) fireable.getRecoilAmount(), fireable.getSelfKnockbackAmount());

        ParticleEffect particleEffect = new ParticleEffect(LancasterPlugin.getInstance().getEffectManager());
        particleEffect.particle = Particle.FLAME;
        particleEffect.particleSize = 1;
        particleEffect.particleCount = 8;
        particleEffect.iterations = 1;
        particleEffect.particleOffsetX = 0.2F;
        particleEffect.particleOffsetY = 0.2F;
        particleEffect.particleOffsetZ = 0.2F;
        particleEffect.setLocation(PluginUtils.getInstance().getRightHandLocation(player));
        particleEffect.start();

        // Fire the weapon

        for (Player target : player.getLocation().getWorld().getPlayers()) {
            if (target.getLocation().distance(player.getLocation()) <= (fireable.getRange() + 2D)) {
                target.playSound(player.getLocation(), fireable.getSound(), 100, 1F);
            }
        }

        if (LancasterPlugin.getInstance().getModifiedPlayerMap().containsKey(player.getUniqueId())
                && LancasterPlugin.getInstance().getModifiedPlayerMap().get(player.getUniqueId()) == PlayerTempModification.SCOPED) {
            if (player.hasPotionEffect(PotionEffectType.SLOW)) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
            if (fireable.isScopePumpkinBlurEnabled()) {
                compatibilityLayer.sendPumpkinEffect(player, true);
            }
            LancasterPlugin.getInstance().getModifiedPlayerMap().remove(player.getUniqueId());
        }

        if (rayTraceResult.getOptionalBlock().isPresent()) {
            Block block = rayTraceResult.getOptionalBlock().get();
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
        }

        AmmunitionActionImpl ammunitionAction = AmmunitionActionRegistry.getAction(fireable, ammunition, ammunition.getActionType());
        if (ammunitionAction == null) {
            ChatUtils.sendMessage(player, "&cError: &4Ammunition action not found!");
            return;
        }

        ammunitionAction.fireAction(player, rayTraceResult, ammunition.getConfiguration());
    }

    private boolean hasCooldown(String cooldownKey, LancasterFireable fireable) {
        Long lastUsed = LancasterPlugin.getInstance().getWeaponCooldownMap().getOrDefault(cooldownKey, 0L);
        return System.currentTimeMillis() <= (lastUsed + fireable.getCooldown());
    }

    private boolean hasGrabCooldown(UUID uniqueId, LancasterFireable fireable) {
        Long lastUsed = LancasterPlugin.getInstance().getGrabCooldownMap().getOrDefault(uniqueId, 0L);
        return System.currentTimeMillis() <= (lastUsed + (fireable.getGrabCooldown() * 1000));
    }
}
