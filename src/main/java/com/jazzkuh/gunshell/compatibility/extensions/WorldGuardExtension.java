package com.jazzkuh.gunshell.compatibility.extensions;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.compatibility.extensions.abstraction.ExtensionImpl;
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
    private final @Getter HashMap<GunshellFlag, IWrappedFlag<WrappedState>> gunshellFlags = new HashMap<>();

    @Override
    public void onEnable() {
        GunshellPlugin.getInstance().getLogger().info("WorldGuard compatibility layer enabled!");
    }

    @Override
    public void onDisable() {
        GunshellPlugin.getInstance().getLogger().info("WorldGuard compatibility layer disabled!");
    }

    @Override
    public void onLoad() {
        try {
            this.registerFlags();
        } catch (Exception exception) {
            GunshellPlugin.getInstance().getLogger().warning("Failed to register WorldGuard flags!");
        }
    }

    private void registerFlags() {
        for (GunshellFlag gunshellFlag : GunshellFlag.values()) {
            Optional<IWrappedFlag<WrappedState>> wrappedFlag = wrapper.registerFlag(gunshellFlag.getFlagString(), WrappedState.class);
            if (wrappedFlag.isPresent()) {
                gunshellFlags.put(gunshellFlag, wrappedFlag.get());
                Bukkit.getLogger().info("[Gunshell] Registered WorldGuard flag: " + gunshellFlag.getFlagString());
            } else {
                Bukkit.getLogger().warning("[Gunshell] Failed to register WorldGuard flag: " + gunshellFlag.getFlagString());
            }
        }
    }

    public boolean isFlagState(Player player, Location location, GunshellFlag gunshellFlag, WrappedState flagState) {
        Set<IWrappedRegion> regions = wrapper.getRegions(location);
        Optional<IWrappedFlag<WrappedState>> flag = wrapper.getFlag(gunshellFlag.getFlagString(), WrappedState.class);
        if (flag.isEmpty() || regions.size() == 0) return false;

        WrappedState state = flag.map(mappedFlag -> wrapper.queryFlag(player, location, mappedFlag)
                        .orElse(WrappedState.ALLOW)).orElse(WrappedState.ALLOW);
        return state == flagState;
    }

    public enum GunshellFlag {
        GUNSHELL_USE_WEAPONS("gunshell-use-weapons");

        private final @Getter String flagString;

        GunshellFlag(String flagString) {
            this.flagString = flagString;
        }
    }
}
