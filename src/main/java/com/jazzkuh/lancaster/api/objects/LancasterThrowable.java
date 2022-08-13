package com.jazzkuh.lancaster.api.objects;

import com.jazzkuh.core.utils.ChatUtils;
import com.jazzkuh.core.utils.ItemBuilder;
import com.jazzkuh.lancaster.api.enums.BuiltinThrowableActionType;
import com.jazzkuh.lancaster.common.configuration.PlaceHolder;
import com.jazzkuh.lancaster.utils.PluginUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LancasterThrowable {
    private final @NotNull @Getter String key;
    private final @NotNull @Getter ConfigurationSection configuration;

    private final @Getter String name;
    private final @Getter List<String> lore;
    private final @Getter Material material;
    private final @Getter boolean hideItemFlags;
    private final @Getter String nbtKey;
    private final @Getter String nbtValue;
    private final @Getter int customModelData;
    private final @Getter int damage;
    private final @Getter int range;
    private final @Getter int fuseTime;
    private final @Getter double cooldown;
    private final @Getter double knockbackAmount;
    private final @Getter String actionType;

    public LancasterThrowable(@NotNull String key, @NotNull ConfigurationSection configuration) {
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
        this.range = configuration.getInt("range", 10);
        this.fuseTime = configuration.getInt("fuseTime", 1) * 20; // convert to ticks
        this.cooldown = configuration.getDouble("cooldown", 1) * 1000; // convert to milliseconds
        this.knockbackAmount = configuration.getDouble("knockbackAmount", 0);
        this.actionType = configuration.getString("actionType", BuiltinThrowableActionType.EXPLOSIVE.toString()).toUpperCase();
    }

    public ItemBuilder getItem() {
        List<Component> parsedLore = new ArrayList<>();
        for (String loreString : lore) {
            parsedLore.add(ChatUtils.color(loreString));
        }

        ItemBuilder itemBuilder = new ItemBuilder(material)
                .setName(name)
                .setLore(parsedLore)
                .setNBT("lancaster_throwable_key", key)
                .setCustomModelData(customModelData)
                .makeUnbreakable(true);

        if (hideItemFlags) itemBuilder.setItemFlag(ItemFlag.values());
        if (nbtKey != null && nbtValue != null) itemBuilder.setNBT(nbtKey, nbtValue);
        return itemBuilder;
    }

    public ItemStack getItemStack() {
        return getItem().toItemStack();
    }
}
