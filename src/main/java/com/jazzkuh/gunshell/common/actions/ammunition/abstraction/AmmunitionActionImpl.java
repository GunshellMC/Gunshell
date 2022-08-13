package com.jazzkuh.gunshell.common.actions.ammunition.abstraction;

import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface AmmunitionActionImpl {
    void fireAction(Player player, GunshellRayTraceResult rayTraceResult, ConfigurationSection configuration);
}
