package com.jazzkuh.lancaster.api.objects;

import com.jazzkuh.core.utils.ChatUtils;
import com.jazzkuh.core.utils.ItemBuilder;
import com.jazzkuh.lancaster.api.enums.BuiltinAmmoActionType;
import com.jazzkuh.lancaster.common.configuration.PlaceHolder;
import com.jazzkuh.lancaster.utils.PluginUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LancasterAmmunition {
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
    private final @Getter String ammoIcon;
    private @Setter @Getter String actionType;

    public LancasterAmmunition(@NotNull String key, @NotNull ConfigurationSection configuration) {
        this.key = key;
        this.configuration = configuration;

        this.name = configuration.getString("name", "NOT_SET");
        this.lore = configuration.getStringList("lore");
        this.material = PluginUtils.getInstance().getMaterial(configuration.getString("material", "STICK"));
        this.hideItemFlags = configuration.getBoolean("hideItemFlags", true);
        this.nbtKey = configuration.getString("nbt.key");
        this.nbtValue = configuration.getString("nbt.value");
        this.customModelData = configuration.getInt("customModelData", 0);
        this.ammo = configuration.getInt("ammo", 8);
        this.ammoIcon = configuration.getString("ammoIcon", "\uE001");
        this.actionType = configuration.getString("actionType", BuiltinAmmoActionType.DAMAGE.toString()).toUpperCase();
    }

    public ItemBuilder getItem() {
        return this.getItem(this.getAmmo());
    }

    public ItemBuilder getItem(int ammo) {
        List<Component> parsedLore = new ArrayList<>();
        for (String loreString : lore) {
            parsedLore.add(ChatUtils.color(loreString));
        }

        ItemBuilder itemBuilder = new ItemBuilder(material)
                .setName(name)
                .setLore(parsedLore)
                .setNBT("lancaster_ammunition_key", key)
                .setNBT("lancaster_ammunition_ammo", ammo)
                .setCustomModelData(customModelData)
                .makeUnbreakable(true);

        if (hideItemFlags) itemBuilder.setItemFlag(ItemFlag.values());
        if (nbtKey != null && nbtValue != null) itemBuilder.setNBT(nbtKey, nbtValue);
        return itemBuilder;
    }

    public ItemStack getItemStack(int ammo) {
        return getItem(ammo).toItemStack();
    }
}
