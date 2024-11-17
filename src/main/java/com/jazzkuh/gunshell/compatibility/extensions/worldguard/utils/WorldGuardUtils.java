package com.jazzkuh.gunshell.compatibility.extensions.worldguard.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UtilityClass
public class WorldGuardUtils {
    public ProtectedRegion getProtectedRegion(@Nonnull Location location, Predicate<Integer> priorityPredicate) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        ProtectedRegion region = null;
        if (manager != null) {
            ApplicableRegionSet fromRegions = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
            region = fromRegions.getRegions().stream().filter(protectedRegion -> priorityPredicate.test(protectedRegion.getPriority()))
                    .max(Comparator.comparing(ProtectedRegion::getPriority)).orElse(null);
        }
        return region;
    }

    public List<ProtectedRegion> getProtectedRegions(@Nonnull World world, Predicate<Integer> priorityPredicate) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(world));
        if (manager == null) return new ArrayList<>();
        return manager.getRegions().values().stream().filter(protectedRegion -> priorityPredicate.test(protectedRegion.getPriority())).collect(Collectors.toList());
    }

    public boolean isRegionOwner(ProtectedRegion protectedRegion, Player player) {
        return protectedRegion != null && protectedRegion.isOwner(WorldGuardPlugin.inst().wrapPlayer(player));
    }

    public boolean isRegionMember(ProtectedRegion protectedRegion, Player player) {
        return protectedRegion != null && protectedRegion.isMember(WorldGuardPlugin.inst().wrapPlayer(player));
    }

    public boolean isRegionOwnerOrMember(ProtectedRegion protectedRegion, Player player) {
        return protectedRegion != null && (protectedRegion.isOwner(WorldGuardPlugin.inst().wrapPlayer(player)) || protectedRegion.isMember(WorldGuardPlugin.inst().wrapPlayer(player)));
    }

    public List<String> getRegionOwners(ProtectedRegion protectedRegion) {
        if (protectedRegion == null) return new ArrayList<>();
        List<String> owners = new ArrayList<>();
        for (UUID uuid : protectedRegion.getOwners().getUniqueIds()) {
            owners.add(Bukkit.getOfflinePlayer(uuid).getName());
        }

        if (owners.isEmpty()) return new ArrayList<>();
        return owners;
    }

    public List<String> getRegionMembers(ProtectedRegion protectedRegion) {
        if (protectedRegion == null) return new ArrayList<>();
        List<String> members = new ArrayList<>();
        for (UUID uuid : protectedRegion.getMembers().getUniqueIds()) {
            members.add(Bukkit.getOfflinePlayer(uuid).getName());
        }

        if (members.isEmpty()) return new ArrayList<>();
        return members;
    }

    public boolean isInRegionWithFlag(ProtectedRegion protectedRegion, StringFlag stringFlag) {
        return protectedRegion != null && protectedRegion.getFlag(stringFlag) != null;
    }

    @Nullable
    public String getRegionFlag(ProtectedRegion protectedRegion, StringFlag stringFlag) {
        if (protectedRegion != null && protectedRegion.getFlag(stringFlag) != null) {
            return protectedRegion.getFlag(stringFlag);
        }

        return null;
    }

    public boolean getRegionFlag(ProtectedRegion protectedRegion, BooleanFlag booleanFlag) {
        if (protectedRegion != null && protectedRegion.getFlag(booleanFlag) != null) {
            return Boolean.TRUE.equals(protectedRegion.getFlag(booleanFlag));
        }

        return false;
    }

    @Nullable
    public StateFlag.State getRegionFlag(ProtectedRegion protectedRegion, StateFlag stateFlag) {
        if (protectedRegion != null && protectedRegion.getFlag(stateFlag) != null) {
            return protectedRegion.getFlag(stateFlag);
        }

        return null;
    }

    public StringFlag registerStringFlag(String flagName) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StringFlag stringFlag = new StringFlag(flagName);
            registry.register(stringFlag);
            return stringFlag;
        } catch (Exception e) {
            Flag<?> existingFlag = registry.get(flagName);
            if (existingFlag instanceof StringFlag) {
                return (StringFlag) existingFlag;
            }
        }

        return null;
    }

    public DoubleFlag registerDoubleFlag(String flagName) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            DoubleFlag doubleFlag = new DoubleFlag(flagName);
            registry.register(doubleFlag);
            return doubleFlag;
        } catch (Exception e) {
            Flag<?> existingFlag = registry.get(flagName);
            if (existingFlag instanceof DoubleFlag) {
                return (DoubleFlag) existingFlag;
            }
        }

        return null;
    }

    public StateFlag registerStateFlag(String flagName, boolean defaultValue) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag stateFlag = new StateFlag(flagName, defaultValue);
            registry.register(stateFlag);
            return stateFlag;
        } catch (Exception e) {
            Flag<?> existingFlag = registry.get(flagName);
            if (existingFlag instanceof StateFlag) {
                return (StateFlag) existingFlag;
            }
        }

        return null;
    }

    public FlagRegistry getFlagRegistry() {
        return WorldGuard.getInstance().getFlagRegistry();
    }
}
