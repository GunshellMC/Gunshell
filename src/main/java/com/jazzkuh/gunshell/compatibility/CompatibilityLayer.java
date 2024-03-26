package com.jazzkuh.gunshell.compatibility;

import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface CompatibilityLayer {
    GunshellRayTraceResult performRayTrace(LivingEntity player, double range);
    String getRayTraceResult(Player player, int range);
    void showEndCreditScene(Player player);
    void showDemoMenu(Player player);
    void sendPumpkinEffect(Player player, boolean forRemoval);
    boolean isPassable(Block block);

    void setCustomModelData(ItemStack itemStack, int customModelData);
}
