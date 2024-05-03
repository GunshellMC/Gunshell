package com.jazzkuh.gunshell.api.events;

import com.jazzkuh.gunshell.api.interfaces.GunshellWeaponImpl;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GunshellDeathEvent extends Event {
    @Getter
    private final LivingEntity entity;

    @Getter
    private final LivingEntity killer;

    private static final HandlerList handlers = new HandlerList();

    public GunshellDeathEvent(LivingEntity entity, LivingEntity killer) {
        this.entity = entity;
        this.killer = killer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}