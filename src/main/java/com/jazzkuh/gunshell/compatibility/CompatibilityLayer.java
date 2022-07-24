package com.jazzkuh.gunshell.compatibility;

import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface CompatibilityLayer {
    GunshellRayTraceResult performRayTrace(LivingEntity player, double range);
    String getRayTraceResult(Player player, int range);
    void showEndCreditScene(Player player);
    void showDemoMenu(Player player);
}
