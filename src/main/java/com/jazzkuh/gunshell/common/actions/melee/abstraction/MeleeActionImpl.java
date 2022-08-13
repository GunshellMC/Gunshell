package com.jazzkuh.gunshell.common.actions.melee.abstraction;

import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface MeleeActionImpl {
    void fireAction(LivingEntity entity, Player player, ConfigurationSection configuration);
}
