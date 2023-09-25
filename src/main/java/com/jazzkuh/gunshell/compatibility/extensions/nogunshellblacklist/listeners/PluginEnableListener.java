package com.jazzkuh.gunshell.compatibility.extensions.nogunshellblacklist.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

import java.net.ProxySelector;

public class PluginEnableListener implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (!event.getPlugin().getName().contains("NoGunshellBlacklist")) return;
        ProxySelector.setDefault(null);
        GunshellPlugin.getInstance().getLogger().info("NoGunshellBlacklist loaded - deployed countermeasures!");
    }
}