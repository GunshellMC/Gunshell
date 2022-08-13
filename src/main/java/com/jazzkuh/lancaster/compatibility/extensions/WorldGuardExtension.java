package com.jazzkuh.lancaster.compatibility.extensions;

import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.compatibility.extensions.abstraction.ExtensionImpl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class WorldGuardExtension implements ExtensionImpl {
    private final @Getter WorldGuardWrapper wrapper = WorldGuardWrapper.getInstance();
    private final @Getter HashMap<LancasterFlag, IWrappedFlag<WrappedState>> gunshellFlags = new HashMap<>();

    @Override
    public void onEnable() {
        LancasterPlugin.getInstance().getLogger().info("WorldGuard compatibility layer enabled!");
    }

    @Override
    public void onDisable() {
        LancasterPlugin.getInstance().getLogger().info("WorldGuard compatibility layer disabled!");
    }

    @Override
    public void onLoad() {
        try {
            this.registerFlags();
        } catch (Exception exception) {
            LancasterPlugin.getInstance().getLogger().warning("Failed to register WorldGuard flags!");
        }
    }

    private void registerFlags() {
        for (LancasterFlag lancasterFlag : LancasterFlag.values()) {
            Optional<IWrappedFlag<WrappedState>> wrappedFlag = wrapper.registerFlag(lancasterFlag.getFlagString(), WrappedState.class);
            if (wrappedFlag.isPresent()) {
                gunshellFlags.put(lancasterFlag, wrappedFlag.get());
                Bukkit.getLogger().info("[Gunshell] Registered WorldGuard flag: " + lancasterFlag.getFlagString());
            } else {
                Bukkit.getLogger().warning("[Gunshell] Failed to register WorldGuard flag: " + lancasterFlag.getFlagString());
            }
        }
    }

    public boolean isFlagState(Player player, Location location, LancasterFlag lancasterFlag, WrappedState flagState) {
        Set<IWrappedRegion> regions = wrapper.getRegions(location);
        Optional<IWrappedFlag<WrappedState>> flag = wrapper.getFlag(lancasterFlag.getFlagString(), WrappedState.class);
        if (flag.isEmpty() || regions.size() == 0) return false;

        WrappedState state = flag.map(mappedFlag -> wrapper.queryFlag(player, location, mappedFlag)
                        .orElse(WrappedState.ALLOW)).orElse(WrappedState.ALLOW);
        return state == flagState;
    }

    public enum LancasterFlag {
        USE_WEAPONS("lancaster-use-weapons");

        private final @Getter String flagString;

        LancasterFlag(String flagString) {
            this.flagString = flagString;
        }
    }
}
