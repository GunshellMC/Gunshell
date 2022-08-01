package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.common.ErrorResult;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.PluginUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerArmorStandManipulateListener implements Listener {
    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        ArmorStand armorStand = event.getRightClicked();
        if (armorStand.hasMetadata("gunshell_throwable_armorstand")) {
            event.setCancelled(true);
        }
    }
}
