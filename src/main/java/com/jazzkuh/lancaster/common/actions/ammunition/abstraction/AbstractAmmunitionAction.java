package com.jazzkuh.lancaster.common.actions.ammunition.abstraction;

import com.jazzkuh.lancaster.api.objects.LancasterAmmunition;
import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import com.jazzkuh.lancaster.api.objects.LancasterRayTraceResult;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class AbstractAmmunitionAction implements AmmunitionActionImpl {
    private final @Getter LancasterFireable fireable;
    private final @Getter LancasterAmmunition ammunition;

    public AbstractAmmunitionAction(LancasterFireable fireable, LancasterAmmunition ammunition) {
        this.fireable = fireable;
        this.ammunition = ammunition;
    }

    @Override
    public abstract void fireAction(Player player, LancasterRayTraceResult rayTraceResult, ConfigurationSection configuration);

    protected boolean isInMinimumRange(LivingEntity entity, Player player, LancasterFireable fireable) {
        return entity.getLocation().distance(player.getLocation()) >= fireable.getMinimumRange();
    }
}
