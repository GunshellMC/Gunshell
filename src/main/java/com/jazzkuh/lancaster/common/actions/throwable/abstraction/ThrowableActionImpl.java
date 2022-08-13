package com.jazzkuh.lancaster.common.actions.throwable.abstraction;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface ThrowableActionImpl {
    void fireAction(Player player, Location location, ConfigurationSection configuration);
}
