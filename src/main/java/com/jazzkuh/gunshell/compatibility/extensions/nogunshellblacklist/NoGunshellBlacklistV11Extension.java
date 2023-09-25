package com.jazzkuh.gunshell.compatibility.extensions.nogunshellblacklist;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.compatibility.framework.Extension;
import com.jazzkuh.gunshell.compatibility.framework.ExtensionInfo;
import org.bukkit.Bukkit;

@ExtensionInfo(name = "NoGunshellBlacklistV11Extension", loadPlugin = "NoGunshell-Blacklist")
public class NoGunshellBlacklistV11Extension implements Extension {
    @Override
    public void onEnable() {
        try {
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("NoGunshell-Blacklist"));
            GunshellPlugin.getInstance().getLogger().info("Disabled NoGunshell-Blacklist - countermeasures deployed!");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDisable() {
        GunshellPlugin.getInstance().getLogger().info("NoGunshell-Blacklist disabled - disabling countermeasures!");
    }

    @Override
    public void onLoad() {
    }
}
