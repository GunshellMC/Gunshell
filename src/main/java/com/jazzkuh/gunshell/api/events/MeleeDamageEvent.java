package com.jazzkuh.gunshell.api.events;

import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MeleeDamageEvent extends Event implements Cancellable {
    private final @Getter Player player;
    private final @Getter GunshellMelee melee;
    private static final HandlerList handlers = new HandlerList();

    public MeleeDamageEvent(Player player, @NotNull GunshellMelee melee) {
        this.player = player;
        this.melee = melee;
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
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

    }
}