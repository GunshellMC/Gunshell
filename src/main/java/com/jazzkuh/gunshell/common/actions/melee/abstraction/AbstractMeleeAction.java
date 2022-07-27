package com.jazzkuh.gunshell.common.actions.melee.abstraction;

import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class AbstractMeleeAction implements MeleeActionImpl {
    private final @Getter GunshellMelee melee;

    public AbstractMeleeAction(GunshellMelee melee) {
        this.melee = melee;
    }

    @Override
    public abstract void fireAction(LivingEntity entity, Player player, ConfigurationSection configuration);
}
