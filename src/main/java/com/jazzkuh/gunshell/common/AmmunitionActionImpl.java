package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface AmmunitionActionImpl {
    void fireAction(Player player, GunshellRayTraceResult rayTraceResult, ConfigurationSection configuration);
}
