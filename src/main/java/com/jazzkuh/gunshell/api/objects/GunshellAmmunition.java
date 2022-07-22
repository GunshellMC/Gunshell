package com.jazzkuh.gunshell.api.objects;

import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GunshellAmmunition {
    private final @NotNull @Getter String key;
    private final @NotNull @Getter ConfigurationSection configuration;

    private final @Getter String name;
    private final @Getter List<String> lore;
    private final @Getter Material material;
    private final @Getter boolean hideItemFlags;
    private final @Getter String nbtKey;
    private final @Getter String nbtValue;
    private final @Getter int customModelData;
    private final @Getter int ammo;

    public GunshellAmmunition(@NotNull String key, @NotNull ConfigurationSection configuration) {
        this.key = key;
        this.configuration = configuration;

        this.name = configuration.getString("name", "NOT_SET");
        this.lore = configuration.getStringList("lore");
        this.material = Material.getMaterial(configuration.getString("material", "STICK"));
        this.hideItemFlags = configuration.getBoolean("hideItemFlags", true);
        this.nbtKey = configuration.getString("nbt.key");
        this.nbtValue = configuration.getString("nbt.value");
        this.customModelData = configuration.getInt("customModelData", 0);
        this.ammo = configuration.getInt("ammo", 8);
    }

    public ItemBuilder getItem() {
        ItemBuilder itemBuilder = new ItemBuilder(material)
                .setName(name)
                .setLore(ChatUtils.color(lore))
                .setNBT("gunshell_ammunition_key", key)
                .setNBT("gunshell_ammunition_ammo", this.getAmmo());

        if (hideItemFlags) itemBuilder.setItemFlag(ItemFlag.values());
        if (nbtKey != null && nbtValue != null) itemBuilder.setNBT(nbtKey, nbtValue);
        return itemBuilder;
    }

    public ItemStack getItemStack() {
        return getItem().toItemStack();
    }
}
