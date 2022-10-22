package com.jazzkuh.gunshell.compatibility.extensions;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.compatibility.extensions.abstraction.ExtensionImpl;
import lombok.Getter;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.TagManager;
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

public class CombatTagPlusExtension implements ExtensionImpl {
    @Override
    public void onEnable() {
        GunshellPlugin.getInstance().getLogger().info("CombatTagPlus compatibility layer enabled!");
    }

    @Override
    public void onDisable() {
        GunshellPlugin.getInstance().getLogger().info("CombatTagPlus compatibility layer disabled!");
    }

    @Override
    public void onLoad() {
    }

    public TagManager getTagManager() {
         return CombatTagPlus.getPlugin(CombatTagPlus.class).getTagManager();
    }
}
