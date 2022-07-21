package com.jazzkuh.gunshell.compatibility.versions;

import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class v1_19_R1 implements CompatibilityLayer {
    @Override
    public Entity getRayTrace(Player player, int range) {
        RayTraceResult result = player.getWorld()
                .rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), range, 0.2);
        if (result == null) return null;
        return result.getHitEntity();
    }

    @Override
    public String getRayTraceResult(Player player, int range) {
        RayTraceResult result = player.getWorld()
                .rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), range, 0.2);
        return result != null ? result.toString() : "No result found";
    }
}
