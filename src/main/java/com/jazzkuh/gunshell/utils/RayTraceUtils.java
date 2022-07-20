package com.jazzkuh.gunshell.utils;

import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class RayTraceUtils {
    public RayTraceResult getRayTrace(Player player, int range) {
        return player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), range);
    }
}
