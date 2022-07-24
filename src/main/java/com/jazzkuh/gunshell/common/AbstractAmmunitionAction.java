package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class AbstractAmmunitionAction implements AmmunitionActionImpl {
    private final @Getter GunshellFireable fireable;
    private final @Getter GunshellAmmunition ammunition;

    public AbstractAmmunitionAction(GunshellFireable fireable, GunshellAmmunition ammunition) {
        this.fireable = fireable;
        this.ammunition = ammunition;
    }

    @Override
    public abstract void fireAction(Player player, GunshellRayTraceResult rayTraceResult, ConfigurationSection configuration);
}
