package com.jazzkuh.lancaster.api.events;

import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FireablePreFireEvent extends Event implements Cancellable {
    private final @Getter Player player;
    private final @Getter LancasterFireable fireable;
    private static final HandlerList handlers = new HandlerList();

    public FireablePreFireEvent(Player player, @NotNull LancasterFireable fireable) {
        this.player = player;
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
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

    }
}