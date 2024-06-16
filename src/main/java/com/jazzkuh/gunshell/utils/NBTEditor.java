package com.jazzkuh.gunshell.utils;

import com.saicone.rtag.RtagItem;
import org.bukkit.inventory.ItemStack;

public class NBTEditor {

    public static ItemStack set(ItemStack object, Object value, Object... keys) {
        RtagItem tag = new RtagItem(object);
        tag.set(value, keys);
        return tag.loadCopy();
    }

    public static boolean contains(ItemStack object, Object... keys) {
        RtagItem tag = new RtagItem(object);
        return !tag.getOptional(keys).isEmpty();
    }

    public static String getString(ItemStack object, Object... keys) {
        RtagItem tag = new RtagItem(object);
        return tag.get(keys);
    }

    public static Integer getInt(ItemStack object, Object... keys) {
        RtagItem tag = new RtagItem(object);
        return tag.get(keys);
    }

    public static Double getDouble(ItemStack object, Object... keys) {
        RtagItem tag = new RtagItem(object);
        return tag.get(keys);
    }

    public static Byte getByte(ItemStack object, Object... keys) {
        RtagItem tag = new RtagItem(object);
        return tag.get(keys);
    }

    public static Long getLong(ItemStack object, Object... keys) {
        RtagItem tag = new RtagItem(object);
        return tag.get(keys);
    }
}