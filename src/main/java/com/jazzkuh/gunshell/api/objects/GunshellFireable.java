package com.jazzkuh.gunshell.api.objects;

import com.jazzkuh.gunshell.api.interfaces.GunshellWeaponImpl;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.ItemBuilder;
import com.jazzkuh.gunshell.utils.PluginUtils;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GunshellFireable implements GunshellWeaponImpl {
    private final @NotNull @Getter String key;
    private final @NotNull @Getter ConfigurationSection configuration;

    private final @Getter String name;
    private final @Getter List<String> lore;
    private final @Getter Material material;
    private final @Getter boolean hideItemFlags;
    private final @Getter String nbtKey;
    private final @Getter String nbtValue;
    private final @Getter int customModelData;
    private @Getter int damage;
    private @Getter int headshotDamage;
    private @Getter int range;
    private final @Getter int minimumRange;
    private final @Getter double cooldown;
    private final @Getter double grabCooldown;
    private @Getter int reloadTime;
    private @Getter int maxAmmo;
    private final @Getter List<String> ammunitionKeys;
    private @Getter String sound;
    private @Getter String reloadSound;
    private @Getter String emptySound;
    private @Getter double recoilAmount;
    private @Getter double knockbackAmount;
    private @Getter double selfKnockbackAmount;
    private @Getter boolean scopeEnabled;
    private @Getter boolean scopePumpkinBlurEnabled;
    private @Getter int scopeAmplifier;

    public GunshellFireable(@NotNull String key, @NotNull ConfigurationSection configuration) {
        this.key = key;
        this.configuration = configuration;

        this.name = configuration.getString("name", "NOT_SET");
        this.lore = configuration.getStringList("lore");
        this.material = PluginUtils.getInstance().getMaterial(configuration.getString("material", "STICK"));
        this.hideItemFlags = configuration.getBoolean("hideItemFlags", true);
        this.nbtKey = configuration.getString("nbt.key");
        this.nbtValue = configuration.getString("nbt.value");
        this.customModelData = configuration.getInt("customModelData", 0);
        this.damage = configuration.getInt("damage", 5);
        this.headshotDamage = configuration.getInt("headshotDamage", this.damage);
        this.range = configuration.getInt("range", 10);
        this.minimumRange = configuration.getInt("minimumRange", 0);
        this.cooldown = configuration.getDouble("cooldown", 1) * 1000; // convert to milliseconds
        this.grabCooldown = configuration.getDouble("grabCooldown", 1);
        this.reloadTime = configuration.getInt("reloadTime", 1) * 20; // Tick based timer
        this.maxAmmo = configuration.getInt("maxAmmo", 8);
        this.ammunitionKeys = configuration.getStringList("ammunitionKeys");
        this.sound = configuration.getString("sound", "empty");
        this.reloadSound = configuration.getString("reloadSound", "empty");
        this.emptySound = configuration.getString("emptySound", "empty");
        this.recoilAmount = configuration.getDouble("recoilAmount", 0.0);
        this.knockbackAmount = configuration.getDouble("knockbackAmount", 0.0);
        this.selfKnockbackAmount = configuration.getDouble("selfKnockbackAmount", 0.0);
        this.scopeEnabled = configuration.getBoolean("scope.enabled", false);
        this.scopePumpkinBlurEnabled = configuration.getBoolean("scope.pumpkinBlurEnabled", false);
        this.scopeAmplifier = configuration.getInt("scope.amplifier", 8);
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }
    public void setReloadTime(int time) {
        this.reloadTime = time;
    }
    public void setHeadshotDamage(int damage) {
        this.headshotDamage = damage;
    }
    public void setRange(int range) {
        this.range = range;
    }
    public void setRecoilAmount(double amount) {
        this.recoilAmount = amount;
    }
    public void setKnockbackAmount(double amount) {
        this.knockbackAmount = range;
    }
    public void setSelfKnockbackAmount(double amount) {
        this.selfKnockbackAmount = range;
    }
    public void setScopeAmplifier(int amplifier) {
        this.scopeAmplifier = amplifier;
    }
    public void setScopeEnabled(boolean isEnabled) {
        this.scopeEnabled = isEnabled;
    }
    public void setScopePumpkinBlurEnabled(boolean isEnabled) {
        this.scopePumpkinBlurEnabled = isEnabled;
    }
    public void setMaxAmmo(int amount) {
        this.maxAmmo = amount;
    }
    public void setEmptySound(String sound) {
        this.emptySound = sound;
    }
    public void setReloadSound(String sound) {
        this.reloadSound = sound;
    }
    public void setSound(String sound) {
        this.sound = sound;
    }
    @Override
    public ItemBuilder getItem(int durability) {
        double attackSpeed = -4 + 1 / this.getGrabCooldown();
        ItemBuilder itemBuilder = new ItemBuilder(material)
                .setName(name)
                .setLore(ChatUtils.color(lore,
                        new PlaceHolder("Ammo", String.valueOf(this.getMaxAmmo())),
                        new PlaceHolder("MaxAmmo", String.valueOf(this.getMaxAmmo())),
                        new PlaceHolder("Damage", String.valueOf(this.getDamage())),
                        new PlaceHolder("Durability", String.valueOf(durability))))
                .setNBT("gunshell_weapon_key", key)
                .setNBT("gunshell_weapon_ammo", this.getMaxAmmo())
                .setNBT("gunshell_weapon_durability", durability)
                .setNBT("gunshell_weapon_ammotype", this.getAmmunitionKeys().get(0))
                .setAttackSpeed(attackSpeed)
                .makeUnbreakable(true);

        if (hideItemFlags) itemBuilder.setItemFlag(ItemFlag.values());
        if (nbtKey != null && nbtValue != null) itemBuilder.setNBT(nbtKey, nbtValue);
        return itemBuilder;
    }
    public void updateItemMeta(ItemStack itemStack, int ammo) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        List<String> lore = this.getLore();
        itemMeta.setLore(ChatUtils.color(lore,
                new PlaceHolder("Ammo", String.valueOf(ammo)),
                new PlaceHolder("MaxAmmo", String.valueOf(this.getMaxAmmo())),
                new PlaceHolder("Damage", String.valueOf(this.getDamage())),
                new PlaceHolder("Durability", String.valueOf(NBTEditor.getInt(itemStack, "gunshell_weapon_durability")))));
        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public ItemStack getItemStack(int durability) {
        return getItem(durability).toItemStack();
    }
}
