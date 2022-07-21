package com.jazzkuh.gunshell.api.objects;

import com.jazzkuh.gunshell.api.interfaces.IGunshellWeapon;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GunshellFireable implements IGunshellWeapon {
    private final @NotNull @Getter String key;
    private final @NotNull @Getter ConfigurationSection configuration;

    private final @Getter String name;
    private final @Getter List<String> lore;
    private final @Getter Material material;
    private final @Getter boolean hideItemFlags;

    public GunshellFireable(@NotNull String key, @NotNull ConfigurationSection configuration) {
        this.key = key;
        this.configuration = configuration;

        this.name = configuration.getString("name", "NOT_SET");
        this.lore = configuration.getStringList("lore");
        this.material = Material.getMaterial(configuration.getString("material", "NOT_SET"));
        this.hideItemFlags = configuration.getBoolean("hideItemFlags", true);

    }

    @Override
    public ItemBuilder getItem() {
        ItemBuilder itemBuilder = new ItemBuilder(material)
                .setName(name)
                .setLore(ChatUtils.color(lore))
                .setNBT("gunshell_weapon_key", key)
                .setNBT("gunshell_weapon_type", "fireable");

        if (hideItemFlags) itemBuilder.setItemFlag(ItemFlag.values());
        return itemBuilder;
    }

    @Override
    public ItemStack getItemStack() {
        return getItem().toItemStack();
    }
}
