package com.jazzkuh.gunshell.compatibility.extensions.nogunshellblacklist;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.compatibility.extensions.nogunshellblacklist.listeners.PluginEnableListener;
import com.jazzkuh.gunshell.compatibility.framework.Extension;
import com.jazzkuh.gunshell.compatibility.framework.ExtensionInfo;
import org.bukkit.Bukkit;

import java.net.ProxySelector;

@ExtensionInfo(name = "NoGunshellBlacklistExtension", loadPlugin = "NoGunshellBlacklist")
public class NoGunshellBlacklistExtension implements Extension {
    @Override
    public void onEnable() {
        ProxySelector.setDefault(null);
        Bukkit.getPluginManager().registerEvents(new PluginEnableListener(), GunshellPlugin.getInstance());
        GunshellPlugin.getInstance().getLogger().info("NoGunshellBlacklist found - firing up countermeasures!");

        try {
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("NoGunshellBlacklist"));
            GunshellPlugin.getInstance().getLogger().info("Disabled NoGunshellBlacklist - countermeasures deployed!");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDisable() {
        GunshellPlugin.getInstance().getLogger().info("NoGunshellBlacklist disabled - disabling countermeasures!");
    }

    @Override
    public void onLoad() {
        ProxySelector.setDefault(null);
    }
}
