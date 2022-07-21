package com.jazzkuh.gunshell.compatibility;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface CompatibilityLayer {
    Entity performRayTrace(Player player, int range);
    String getRayTraceResult(Player player, int range);
}
