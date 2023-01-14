package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.enums.PlayerTempModification;
import com.jazzkuh.gunshell.api.events.FireableDamageEvent;
import com.jazzkuh.gunshell.api.events.FireableFireEvent;
import com.jazzkuh.gunshell.api.events.FireablePreFireEvent;
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
import org.bukkit.Material;
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
    private final String GUN_AMMO_KEY = "gunshell_weapon_ammo";
    private final String GUN_AMMOTYPE_KEY = "gunshell_weapon_ammotype";
    private final String DURABILITY_KEY = "gunshell_weapon_durability";

    private final String AMMUNITION_KEY = "gunshell_ammunition_key";
    private final String AMMUNITION_AMMO_KEY = "gunshell_ammunition_ammo";

    @EventHandler
    public void onFireablePreFire(FireablePreFireEvent event) {
        Player player = event.getPlayer();
        GunshellFireable fireable = event.getFireable();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        String cooldownKey = DefaultConfig.PER_WEAPON_COOLDOWN.asBoolean() ?
                player.getUniqueId() + "_" + player.getInventory().getHeldItemSlot() :
                player.getUniqueId() + "_global";

        if (GunshellPlugin.getInstance().getReloadingSet().contains(player.getUniqueId())) return;

        int ammo = NBTEditor.getInt(itemStack, GUN_AMMO_KEY);
        int durability = NBTEditor.getInt(itemStack, DURABILITY_KEY);

        if (durability <= 0) {
            player.getInventory().removeItem(itemStack);
            return;
        }

        List<String> ammunitionKeys = fireable.getAmmunitionKeys();
        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTags(player, AMMUNITION_KEY, ammunitionKeys).isEmpty()) {
            MessagesConfig.ERROR_OUT_OF_AMMO.get(player);
            player.playSound(player.getLocation(), fireable.getEmptySound(), fireable.getSoundVolume(), 1F);
            return;
        }

        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTags(player, AMMUNITION_KEY, ammunitionKeys).isPresent()) {
            ItemStack ammoItem = PluginUtils.getInstance().getItemWithNBTTags(player, AMMUNITION_KEY, ammunitionKeys).get();
            int ammoAmount = NBTEditor.getInt(ammoItem, AMMUNITION_AMMO_KEY);
            GunshellPlugin.getInstance().getReloadingSet().add(player.getUniqueId());

            for (Player target : player.getLocation().getWorld().getPlayers()) {
                if (target.getLocation().distance(player.getLocation()) <= fireable.getSoundRange()) {
                    target.playSound(player.getLocation(), fireable.getReloadSound(), fireable.getSoundVolume(), 1F);
                }
            }

            MessagesConfig.RELOADING_START.get(player,
                    new PlaceHolder("Durability", String.valueOf(durability)),
                    new PlaceHolder("Ammo", String.valueOf(ammoAmount > fireable.getMaxAmmo() ? fireable.getMaxAmmo() : ammoAmount)),
                    new PlaceHolder("MaxAmmo", String.valueOf(fireable.getMaxAmmo())));

            if (player.getInventory().getItemInOffHand().equals(ammoItem)) {
                ItemStack offHand = player.getInventory().getItemInOffHand();
                if (offHand.getAmount() > 1) {
                    offHand.setAmount(offHand.getAmount() - 1);
                } else {
                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                }
            } else {
                if (ammoItem.getAmount() > 1) {
                    ammoItem.setAmount(ammoItem.getAmount() - 1);
                } else {
                    player.getInventory().removeItem(ammoItem);
                }
            }

            Bukkit.getScheduler().runTaskLater(GunshellPlugin.getInstance(), () -> {
                int finalAmmoAmount = ammoAmount > fireable.getMaxAmmo() ? fireable.getMaxAmmo() : ammoAmount;
                PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, finalAmmoAmount);
                PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMOTYPE_KEY, NBTEditor.getString(ammoItem, AMMUNITION_KEY));
                fireable.updateItemMeta(itemStack, finalAmmoAmount);

                MessagesConfig.SHOW_AMMO_DURABILITY.get(player,
                        new PlaceHolder("Durability", String.valueOf(durability)),
                        new PlaceHolder("Ammo", String.valueOf(finalAmmoAmount)),
                        new PlaceHolder("MaxAmmo", String.valueOf(fireable.getMaxAmmo())));

                GunshellPlugin.getInstance().getReloadingSet().remove(player.getUniqueId());
                MessagesConfig.RELOADING_FINISHED.get(player);
            }, fireable.getReloadTime());
            return;
        }

        GunshellAmmunition ammunition = GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition()
                .get(NBTEditor.getString(itemStack, GUN_AMMOTYPE_KEY));
        if (ammunition == null) {
            MessagesConfig.ERROR_AMMUNITION_NOT_FOUND.get(player,
                    new PlaceHolder("Type", NBTEditor.getString(itemStack, GUN_AMMOTYPE_KEY)));
            return;
        }

        if (hasCooldown(cooldownKey, fireable) || hasGrabCooldown(player.getUniqueId(), fireable)) return;

        FireableFireEvent fireableFireEvent = new FireableFireEvent(player, fireable);
        Bukkit.getPluginManager().callEvent(fireableFireEvent);
        if (fireableFireEvent.isCancelled()) return;

        /*
         * Perform the raytrace to find the target
         */
        CompatibilityLayer compatibilityLayer = GunshellPlugin.getInstance().getCompatibilityLayer();
        GunshellRayTraceResult rayTraceResult = compatibilityLayer.performRayTrace(player, fireable.getRange());

        GunshellPlugin.getInstance().getWeaponCooldownMap().put(cooldownKey, System.currentTimeMillis());
        PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, ammo - 1);
        PluginUtils.getInstance().applyNBTTag(itemStack, DURABILITY_KEY, durability - 1);
        fireable.updateItemMeta(itemStack, ammo - 1);

        PluginUtils.getInstance().performRecoil(player,
                (float) fireable.getRecoilAmount(), fireable.getSelfKnockbackAmount());

        ParticleEffect particleEffect = new ParticleEffect(GunshellPlugin.getInstance().getEffectManager());
        particleEffect.particle = Particle.FLAME;
        particleEffect.particleSize = 1;
        particleEffect.particleCount = 8;
        particleEffect.iterations = 1;
        particleEffect.particleOffsetX = 0.2F;
        particleEffect.particleOffsetY = 0.2F;
        particleEffect.particleOffsetZ = 0.2F;
        particleEffect.setLocation(PluginUtils.getInstance().getRightHandLocation(player));
        particleEffect.start();

        if (NBTEditor.getInt(itemStack, GUN_AMMO_KEY) >= 1) {
            MessagesConfig.SHOW_AMMO_DURABILITY.get(player,
                    new PlaceHolder("Durability", String.valueOf(NBTEditor.getInt(itemStack, DURABILITY_KEY))),
                    new PlaceHolder("Ammo", String.valueOf(NBTEditor.getInt(itemStack, GUN_AMMO_KEY))),
                    new PlaceHolder("MaxAmmo", String.valueOf(fireable.getMaxAmmo())));
        } else {
            MessagesConfig.BULLET_SHOT_LAST.get(player);
        }

        if (NBTEditor.getInt(itemStack, DURABILITY_KEY) <= 0) {
            player.getInventory().removeItem(itemStack);
        }

        for (Player target : player.getLocation().getWorld().getPlayers()) {
            if (target.getLocation().distance(player.getLocation()) <= fireable.getSoundRange()) {
                target.playSound(player.getLocation(), fireable.getSound(), fireable.getSoundVolume(), 1F);
            }
        }

        if (GunshellPlugin.getInstance().getModifiedPlayerMap().containsKey(player.getUniqueId())
                && GunshellPlugin.getInstance().getModifiedPlayerMap().get(player.getUniqueId()) == PlayerTempModification.SCOPED) {
            if (player.hasPotionEffect(PotionEffectType.SLOW)) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
            if (fireable.isScopePumpkinBlurEnabled()) {
                compatibilityLayer.sendPumpkinEffect(player, true);
            }
            GunshellPlugin.getInstance().getModifiedPlayerMap().remove(player.getUniqueId());
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

        FireableDamageEvent fireableDamageEvent = new FireableDamageEvent(player, rayTraceResult, fireable);
        Bukkit.getPluginManager().callEvent(fireableDamageEvent);
        if (fireableDamageEvent.isCancelled()) return;

        ammunitionAction.fireAction(player, rayTraceResult, ammunition.getConfiguration());
    }

    private boolean hasCooldown(String cooldownKey, GunshellFireable fireable) {
        Long lastUsed = GunshellPlugin.getInstance().getWeaponCooldownMap().getOrDefault(cooldownKey, 0L);
        return System.currentTimeMillis() <= (lastUsed + fireable.getCooldown());
    }

    private boolean hasGrabCooldown(UUID uniqueId, GunshellFireable fireable) {
        Long lastUsed = GunshellPlugin.getInstance().getGrabCooldownMap().getOrDefault(uniqueId, 0L);
        return System.currentTimeMillis() <= (lastUsed + (fireable.getGrabCooldown() * 1000));
    }
}
