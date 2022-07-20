package com.jazzkuh.gunshell.compatibility;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface CompatibilityLayer {
    Entity getRayTrace(Player player, int range);
}
