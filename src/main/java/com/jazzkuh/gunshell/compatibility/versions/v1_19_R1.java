package com.jazzkuh.gunshell.compatibility.versions;

import com.jazzkuh.gunshell.compatibility.CompatibilityLayer;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class v1_19_R1 implements CompatibilityLayer {
    @Override
    public Entity getRayTrace(Player player, int range) {
        RayTraceResult result = player.getWorld()
                .rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, null);
        if (result == null) return null;
        return result.getHitEntity();
    }

    @Override
    public String getRayTraceResult(Player player, int range) {
        RayTraceResult result = player.getWorld()
                .rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, null);
        return result != null ? result.toString() : "No result found";
    }
}
