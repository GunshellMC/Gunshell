package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class AbstractMeleeAction implements MeleeActionImpl {
    private final @Getter GunshellMelee melee;

    public AbstractMeleeAction(GunshellMelee melee) {
        this.melee = melee;
    }

    @Override
    public abstract void fireAction(LivingEntity entity, Player player, ConfigurationSection configuration);
}
