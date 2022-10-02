package com.jazzkuh.gunshell.api.events;

import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FireableDamageEvent extends Event implements Cancellable {
    private boolean cancelled;
    private final @Getter Player player;
    private final @Getter GunshellRayTraceResult rayTraceResult;
    private final @Getter GunshellFireable fireable;
    private static final HandlerList handlers = new HandlerList();

    public FireableDamageEvent(Player player, GunshellRayTraceResult rayTraceResult, @NotNull GunshellFireable fireable) {
        this.player = player;
        this.rayTraceResult = rayTraceResult;
        this.fireable = fireable;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}