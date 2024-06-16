package com.jazzkuh.gunshell.api.objects;

import com.jazzkuh.gunshell.api.interfaces.GunshellWeaponImpl;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.ItemBuilder;
import com.jazzkuh.gunshell.utils.PluginUtils;
import com.jazzkuh.gunshell.utils.NBTEditor;
import lombok.Getter;
import lombok.Setter;
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
    private @Getter @Setter int damage;
    private @Getter @Setter int headshotDamage;
    private @Getter @Setter int range;
    private final @Getter int minimumRange;
    private final @Getter double cooldown;
    private final @Getter double grabCooldown;
    private @Getter @Setter int reloadTime;
    private @Getter @Setter int maxAmmo;
    private final @Getter List<String> ammunitionKeys;
    private @Getter @Setter String sound;
    private @Getter @Setter String reloadSound;
    private @Getter @Setter String emptySound;
    private @Getter @Setter int soundRange;
    private @Getter @Setter int soundVolume;
    private @Getter @Setter double recoilAmount;
    private @Getter @Setter double knockbackAmount;
    private @Getter @Setter double selfKnockbackAmount;
    private @Getter @Setter boolean scopeEnabled;
    private @Getter @Setter boolean scopePumpkinBlurEnabled;
    private @Getter @Setter int scopeAmplifier;

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
        this.soundRange = configuration.getInt("soundRange", this.range);
        this.soundVolume = configuration.getInt("soundVolume", 100);
        this.recoilAmount = configuration.getDouble("recoilAmount", 0.0);
        this.knockbackAmount = configuration.getDouble("knockbackAmount", 0.0);
        this.selfKnockbackAmount = configuration.getDouble("selfKnockbackAmount", 0.0);
        this.scopeEnabled = configuration.getBoolean("scope.enabled", false);
        this.scopePumpkinBlurEnabled = configuration.getBoolean("scope.pumpkinBlurEnabled", false);
        this.scopeAmplifier = configuration.getInt("scope.amplifier", 8);
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
        if (customModelData != 0) itemBuilder.setCustomModelData(customModelData);
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