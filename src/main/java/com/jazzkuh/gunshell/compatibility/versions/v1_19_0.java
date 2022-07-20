package com.jazzkuh.gunshell.compatibility.versions;

import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class v1_19_0 implements CompatibilityLayer {
    @Override
    public Entity getRayTrace(Player player, int range) {
        RayTraceResult result = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), range);
        if (result == null) return null;
        return result.getHitEntity();
    }
}
