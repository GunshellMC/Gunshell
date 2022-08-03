package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.common.ErrorResult;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.PluginUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GunshellPlugin.getInstance().getReloadingSet().remove(player.getUniqueId());
        if (GunshellPlugin.getInstance().getDescription().getAuthors().contains(player.getName())) {
            ChatUtils.sendMessage(player, "&8 ----------------------------------------------");
            ChatUtils.sendMessage(player, "&8| &aThis server is using Gunshell &2v" + GunshellPlugin.getInstance().getDescription().getVersion() + "&a.");
            ChatUtils.sendMessage(player, "&8 ----------------------------------------------");
        }
    }
}
