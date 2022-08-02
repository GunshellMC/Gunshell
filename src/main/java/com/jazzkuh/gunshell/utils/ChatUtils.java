package com.jazzkuh.gunshell.utils;

import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.compatibility.CompatibilityManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    public static final Pattern hexPattern = Pattern.compile("&#(\\w{5}[0-9a-f])");
    public static String color(String message) {
        if (!CompatibilityManager.getVersion().equals("v1_12_R1")) {
            Matcher matcher = hexPattern.matcher(message);
            StringBuffer stringBuilder = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(stringBuilder, net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1)).toString());
            }

            return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(stringBuilder).toString());
        } else {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
    }

    public static List<String> color(List<String> lore) {
        List<String> components = new ArrayList<>();
        for (String line : lore) {
            components.add(color(line));
        }
        return components;
    }

    public static List<String> color(List<String> lore, PlaceHolder... placeHolders) {
        List<String> components = new ArrayList<>();
        for (String line : lore) {
            for (PlaceHolder placeHolder : placeHolders) {
                line = line.replaceAll(placeHolder.getPlaceholder(), placeHolder.getValue());
            }
            components.add(line);
        }
        return color(components);
    }

    /**
     * Send a message to a CommandSender
     * @param commandSender The CommandSender to send the message to
     * @param message The message to send
     */
    public static void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(color(message));
    }

    public static void sendBroadcast(String message) {
        Bukkit.broadcastMessage(color(message));
    }
}
