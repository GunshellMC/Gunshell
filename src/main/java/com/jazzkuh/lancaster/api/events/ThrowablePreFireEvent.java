package com.jazzkuh.lancaster.api.events;

import com.jazzkuh.lancaster.api.objects.LancasterThrowable;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ThrowablePreFireEvent extends Event implements Cancellable {
    private final @Getter Player player;
    private final @Getter LancasterThrowable throwable;
    private static final HandlerList handlers = new HandlerList();

    public ThrowablePreFireEvent(Player player, @NotNull LancasterThrowable throwable) {
        this.player = player;
        this.throwable = throwable;
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