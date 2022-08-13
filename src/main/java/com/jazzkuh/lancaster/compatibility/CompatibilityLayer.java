package com.jazzkuh.lancaster.compatibility;

import com.jazzkuh.lancaster.api.objects.LancasterRayTraceResult;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface CompatibilityLayer {
    LancasterRayTraceResult performRayTrace(LivingEntity player, double range);
    void sendPumpkinEffect(Player player, boolean forRemoval);
    boolean isPassable(Block block);
}
