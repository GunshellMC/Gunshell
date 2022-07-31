package com.jazzkuh.gunshell.common.actions.throwable.abstraction;

import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class AbstractThrowableAction implements ThrowableActionImpl {
    private final @Getter GunshellThrowable throwable;

    public AbstractThrowableAction(GunshellThrowable throwable) {
        this.throwable = throwable;
    }

    @Override
    public abstract void fireAction(Player player, Location location, ConfigurationSection configuration);

    protected ArrayList<Block> getBlocksAroundCenter(Location loc, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();

        int i = 0;
        for (int x = (loc.getBlockX()-radius); x <= (loc.getBlockX()+radius); x++) {
            for (int y = (loc.getBlockY()-radius); y <= (loc.getBlockY()+radius); y++) {
                for (int z = (loc.getBlockZ()-radius); z <= (loc.getBlockZ()+radius); z++) {
                    Location l = new Location(loc.getWorld(), x, y, z);
                    if (l.distance(loc) <= radius) {
                        i++;
                        if ((i & 1) == 0) continue;
                        blocks.add(l.getBlock());
                    }
                }
            }
        }

        return blocks;
    }

    protected ArrayList<Block> getGroundBlockAroundCenter(Location loc, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();

        for (int x = (loc.getBlockX()-radius); x <= (loc.getBlockX()+radius); x++) {
            for (int z = (loc.getBlockZ()-radius); z <= (loc.getBlockZ()+radius); z++) {
                Location l = new Location(loc.getWorld(), x, loc.getBlockY(), z);
                if (l.distance(loc) <= radius) {
                    blocks.add(l.getBlock());
                }
            }
        }

        return blocks;
    }
}
