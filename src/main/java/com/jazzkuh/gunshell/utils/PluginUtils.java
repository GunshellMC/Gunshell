package com.jazzkuh.gunshell.utils;

import com.cryptomorin.xseries.XMaterial;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Optional;

public class PluginUtils {
    private static @Getter @Setter(AccessLevel.PRIVATE) PluginUtils instance;

    public PluginUtils() {
        setInstance(this);
    }

    public boolean isValidInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public double applyProtectionModifier(double damage, boolean isHeadShot, LivingEntity entity) {
        if (!DefaultConfig.PROTECTION_DAMAGE_REDUCTION_ENABLED.asBoolean()) return damage;
        if (entity.getEquipment() == null) return damage;

        ItemStack stack = isHeadShot ? entity.getEquipment().getHelmet() : entity.getEquipment().getChestplate();
        if (stack == null) return damage;

        if (!stack.containsEnchantment(Enchantment.PROTECTION_PROJECTILE)) return damage;
        int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);

        int percentage = DefaultConfig.PROTECTION_DAMAGE_REDUCTION_AMOUNT.asInteger();
        if (percentage == 0) percentage = 5;

        return damage - ((damage / 100 * percentage) * enchantmentLevel);
    }

    public Optional<ItemStack> getItemWithNBTTags(Player player, String tag, List<String> values) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || !NBTEditor.contains(item, tag)) continue;
            if (values.stream().anyMatch(value -> NBTEditor.getString(item, tag).equals(value))) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    public void applyNBTTag(ItemStack itemStack, String key, Object value) {
        ItemStack newItemStack = NBTEditor.set(itemStack, value, key);
        ItemMeta itemMeta = newItemStack.getItemMeta();
        itemStack.setItemMeta(itemMeta);
    }

    public Material getMaterial(String materialName) {
        if (XMaterial.matchXMaterial(materialName).isPresent()) {
            return XMaterial.matchXMaterial(materialName).get().parseMaterial();
        }

        return null;
    }
    public Location getRightHandLocation(Player player) {
        double yawRightHandDirection = Math.toRadians(-1 * player.getEyeLocation().getYaw() - 45);
        double x = 0.5 * Math.sin(yawRightHandDirection) + player.getLocation().getX();
        double y = player.getLocation().getY() + 1;
        double z = 0.5 * Math.cos(yawRightHandDirection) + player.getLocation().getZ();
        return new Location(player.getWorld(), x, y, z);
    }

    public void performRecoil(LivingEntity livingEntity, Player player, float pitchIncrement, double knockback) {
        Location location = livingEntity.getLocation();
        if (pitchIncrement > 0) {
            float pitch = location.getPitch();
            location.setPitch(pitch - pitchIncrement);

            // Use a cause other than PLUGIN or COMMAND because essentials sucks lol.
            Vector playerVelocity = livingEntity.getVelocity();
            livingEntity.teleport(location, PlayerTeleportEvent.TeleportCause.UNKNOWN);
            livingEntity.setVelocity(playerVelocity);
        }

        // Apply knockback
        double finalKnockback = ( knockback * 10 ) / 2;
        KnockbackUtils.applyKnockBack(livingEntity, player, finalKnockback);
    }
}
