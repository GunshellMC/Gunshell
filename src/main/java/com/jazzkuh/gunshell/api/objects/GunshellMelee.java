package com.jazzkuh.gunshell.api.objects;

import com.jazzkuh.gunshell.api.enums.BuiltinMeleeActionType;
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

public class GunshellMelee {
    private final @NotNull @Getter String key;
    private final @NotNull @Getter ConfigurationSection configuration;

    private final @Getter String name;
    private final @Getter List<String> lore;
    private final @Getter Material material;
    private final @Getter boolean hideItemFlags;
    private final @Getter String nbtKey;
    private final @Getter String nbtValue;
    private final @Getter int customModelData;
    private final @Getter double damage;
    private final @Getter double cooldown;
private final @Getter double grabCooldown;
    private final @Getter String actionType;

    public GunshellMelee(@NotNull String key, @NotNull ConfigurationSection configuration) {
        this.key = key;
        this.configuration = configuration;

        this.name = configuration.getString("name", "NOT_SET");
        this.lore = configuration.getStringList("lore");
        this.material = PluginUtils.getInstance().getMaterial(configuration.getString("material", "STICK"));
        this.hideItemFlags = configuration.getBoolean("hideItemFlags", true);
        this.nbtKey = configuration.getString("nbt.key");
        this.nbtValue = configuration.getString("nbt.value");
        this.customModelData = configuration.getInt("customModelData", 0);
        this.damage = configuration.getDouble("damage", 5);
        this.cooldown = configuration.getDouble("cooldown", 1) * 1000; // convert to milliseconds
        this.grabCooldown = configuration.getDouble("grabCooldown", 1);
        this.actionType = configuration.getString("actionType", BuiltinMeleeActionType.DAMAGE.toString()).toUpperCase();
    }
    public ItemBuilder getItem(int durability) {
        double attackSpeed = -4 + 1 / this.getGrabCooldown();
        ItemBuilder itemBuilder = new ItemBuilder(material)
                .setName(name)
                .setLore(ChatUtils.color(lore,
                        new PlaceHolder("Damage", String.valueOf(this.getDamage())),
                        new PlaceHolder("Durability", String.valueOf(durability))))
                .setNBT("gunshell_melee_key", key)
                .setNBT("gunshell_melee_durability", durability)
                .setAttackSpeed(attackSpeed)
                .makeUnbreakable(true);

        if (hideItemFlags) itemBuilder.setItemFlag(ItemFlag.values());
        if (nbtKey != null && nbtValue != null) itemBuilder.setNBT(nbtKey, nbtValue);
        return itemBuilder;
    }

    public void updateItemMeta(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        List<String> lore = this.getLore();
        itemMeta.setLore(ChatUtils.color(lore,
                new PlaceHolder("Damage", String.valueOf(this.getDamage())),
                new PlaceHolder("Durability", String.valueOf(NBTEditor.getInt(itemStack, "gunshell_melee_durability")))));
        itemStack.setItemMeta(itemMeta);
    }

    public ItemStack getItemStack(int durability) {
        return getItem(durability).toItemStack();
    }
}
