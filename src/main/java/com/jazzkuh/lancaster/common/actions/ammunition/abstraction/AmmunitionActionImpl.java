package com.jazzkuh.lancaster.common.actions.ammunition.abstraction;

import com.jazzkuh.lancaster.api.objects.LancasterRayTraceResult;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface AmmunitionActionImpl {
    void fireAction(Player player, LancasterRayTraceResult rayTraceResult, ConfigurationSection configuration);
}
