package com.jazzkuh.gunshell.compatibility.extensions.worldguard;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.compatibility.extensions.worldguard.utils.WorldGuardUtils;
import com.jazzkuh.gunshell.compatibility.framework.Extension;
import com.jazzkuh.gunshell.compatibility.framework.ExtensionInfo;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

@ExtensionInfo(name = "WorldGuardExtension", loadPlugin = "WorldGuard")
public class WorldGuardExtension implements Extension {
    private final @Getter HashMap<GunshellFlag, Flag<?>> gunshellFlags = new HashMap<>();

    @Override
    public void onLoad() {
        try {
            this.registerFlags();
        } catch (Exception exception) {
            GunshellPlugin.getInstance().getLogger().warning("Failed to register WorldGuard flags!");
        }
    }

    @Override
    public void onEnable() {
        GunshellPlugin.getInstance().getLogger().info("WorldGuard compatibility layer enabled!");
    }

    @Override
    public void onDisable() {
        GunshellPlugin.getInstance().getLogger().info("WorldGuard compatibility layer disabled!");
    }

    private void registerFlags() {
        for (GunshellFlag gunshellFlag : GunshellFlag.values()) {
            try {
                Flag<?> worldGuardFlag = WorldGuardUtils.registerStateFlag(gunshellFlag.getFlagString(), gunshellFlag.isDefaultState());
                gunshellFlags.put(gunshellFlag, worldGuardFlag);
                Bukkit.getLogger().info("[Gunshell] Registered WorldGuard flag: " + gunshellFlag.getFlagString());
            } catch (Exception exception) {
                Bukkit.getLogger().warning("[Gunshell] Failed to register WorldGuard flag: " + gunshellFlag.getFlagString());
            }
        }
    }

    public boolean isFlagState(Location location, GunshellFlag gunshellFlag, boolean state) {
        ProtectedRegion region = WorldGuardUtils.getProtectedRegion(location, priority -> priority >= 0);
        if (region == null) return false;

        Flag<?> flag = gunshellFlags.get(gunshellFlag);
        if (flag == null) return false;
        if (!(flag instanceof StateFlag)) return false;

        StateFlag stateFlag = (StateFlag) flag;
        StateFlag.State flagState = WorldGuardUtils.getRegionFlag(region, stateFlag);
        if (flagState == null) return false;

        StateFlag.State neededState = state ? StateFlag.State.ALLOW : StateFlag.State.DENY;
        return flagState == neededState;
    }

    @AllArgsConstructor
    @Getter
    public enum GunshellFlag {
        GUNSHELL_USE_WEAPONS("gunshell-use-weapons", true);

        private final String flagString;
        private final boolean defaultState;
    }
}
