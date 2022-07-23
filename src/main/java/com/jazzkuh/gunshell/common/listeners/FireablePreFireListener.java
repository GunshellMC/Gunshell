package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.events.FireableFireEvent;
import com.jazzkuh.gunshell.api.events.FireablePreFireEvent;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.PluginUtils;
import de.slikey.effectlib.effect.ParticleEffect;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class FireablePreFireListener implements Listener {
    private final String GUN_AMMO_KEY = "gunshell_weapon_ammo";
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

        String ammunitionKey = fireable.getAmmunitionKey();
        GunshellAmmunition ammunition = GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().get(ammunitionKey);
        if (ammunition == null) {
            MessagesConfig.ERROR_AMMUNITION_NOT_FOUND.get(player,
                    new PlaceHolder("Key", ammunitionKey));
            return;
        }

        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTag(player, AMMUNITION_KEY, ammunitionKey).isEmpty()) {
            MessagesConfig.ERROR_OUT_OF_AMMO.get(player);
            return;
        }

        if (ammo <= 0 && PluginUtils.getInstance().getItemWithNBTTag(player, AMMUNITION_KEY, ammunitionKey).isPresent()) {
            ItemStack ammoItem = PluginUtils.getInstance().getItemWithNBTTag(player, AMMUNITION_KEY, ammunitionKey).get();
            int ammoAmount = NBTEditor.getInt(ammoItem, AMMUNITION_AMMO_KEY);
            GunshellPlugin.getInstance().getReloadingSet().add(player.getUniqueId());

            MessagesConfig.RELOADING_START.get(player);

            if (ammoItem.getAmount() > 1) {
                ammoItem.setAmount(ammoItem.getAmount() - 1);
            } else {
                player.getInventory().removeItem(ammoItem);
            }

            Bukkit.getScheduler().runTaskLater(GunshellPlugin.getInstance(), () -> {
                int finalAmmoAmount = ammoAmount > fireable.getMaxAmmo() ? fireable.getMaxAmmo() : ammoAmount;
                PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, finalAmmoAmount);
                this.updateFireableItemMeta(itemStack, fireable, finalAmmoAmount);

                MessagesConfig.SHOW_AMMO_DURABILITY.get(player,
                        new PlaceHolder("Durability", String.valueOf(durability)),
                        new PlaceHolder("Ammo", String.valueOf(finalAmmoAmount)),
                        new PlaceHolder("MaxAmmo", String.valueOf(fireable.getMaxAmmo())));

                GunshellPlugin.getInstance().getReloadingSet().remove(player.getUniqueId());
                MessagesConfig.RELOADING_FINISHED.get(player);
            }, fireable.getReloadTime());
            return;
        }

        if (hasCooldown(cooldownKey, fireable) || hasGrabCooldown(player.getUniqueId(), fireable)) return;


        FireableFireEvent fireableFireEvent = new FireableFireEvent(player, fireable);
        if (fireableFireEvent.isCancelled()) return;
        Bukkit.getPluginManager().callEvent(fireableFireEvent);

        /*
         * Perform the raytrace to find the target
         */
        CompatibilityLayer compatibilityLayer = GunshellPlugin.getInstance().getCompatibilityLayer();
        GunshellRayTraceResult rayTraceResult = compatibilityLayer.performRayTrace(player, fireable.getRange());

        GunshellPlugin.getInstance().getWeaponCooldownMap().put(cooldownKey, System.currentTimeMillis());
        PluginUtils.getInstance().applyNBTTag(itemStack, GUN_AMMO_KEY, ammo - 1);
        PluginUtils.getInstance().applyNBTTag(itemStack, DURABILITY_KEY, durability - 1);

        this.updateFireableItemMeta(itemStack, fireable, ammo - 1);

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

        if (rayTraceResult.getOptionalBlock().isPresent()) {
            Block block = rayTraceResult.getOptionalBlock().get();
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
        }
        if (rayTraceResult.getOptionalLivingEntity().isEmpty()) return;

        LivingEntity livingEntity = rayTraceResult.getOptionalLivingEntity().get();
        if (livingEntity.isDead()) return;
        if (livingEntity.hasMetadata("vanished")) return;
        if (livingEntity.hasMetadata("NPC")) return;

        if (livingEntity instanceof Player) {
            Player playerTarget = (Player) livingEntity;
            if (playerTarget.getGameMode() == GameMode.SPECTATOR
                    || playerTarget.getGameMode() == GameMode.CREATIVE) return;

            MessagesConfig.BULLET_HIT_BY_PLAYER.get(playerTarget,
                    new PlaceHolder("Name", player.getName()));
        }

        if (livingEntity.getLocation().getWorld() != null) {
            livingEntity.getLocation().getWorld().playEffect(livingEntity.getEyeLocation(),
                    Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        }

        MessagesConfig.BULLET_HIT_OTHER.get(player,
                new PlaceHolder("Name", livingEntity.getName()));

        double damage = fireable.getDamage();
        if (rayTraceResult.isHeadshot()) {
            damage = fireable.getHeadshotDamage();
            MessagesConfig.BULLET_HIT_OTHER_HEADSHOT.get(player,
                    new PlaceHolder("Name", livingEntity.getName()));
        }

        PluginUtils.getInstance().performRecoil(livingEntity, 0F, fireable.getKnockbackAmount());

        if (damage > livingEntity.getHealth()) {
            livingEntity.setHealth(0D);
        } else {
            EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, livingEntity,
                    EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK, damage);
            livingEntity.setHealth(livingEntity.getHealth() - damage);

            livingEntity.setLastDamageCause(entityDamageByEntityEvent);
            Bukkit.getPluginManager().callEvent(entityDamageByEntityEvent);
        }
    }

    private void updateFireableItemMeta(ItemStack itemStack, GunshellFireable fireable, int ammo) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = fireable.getLore();

        itemMeta.setLore(ChatUtils.color(lore,
                new PlaceHolder("Ammo", String.valueOf(ammo)),
                new PlaceHolder("MaxAmmo", String.valueOf(fireable.getMaxAmmo())),
                new PlaceHolder("Damage", String.valueOf(fireable.getDamage())),
                new PlaceHolder("Durability", String.valueOf(NBTEditor.getInt(itemStack, DURABILITY_KEY)))));
        itemStack.setItemMeta(itemMeta);
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