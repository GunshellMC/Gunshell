package com.jazzkuh.gunshell.compatibility;

import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface CompatibilityLayer {
    GunshellRayTraceResult performRayTrace(Player player, int range);
    String getRayTraceResult(Player player, int range);
}
