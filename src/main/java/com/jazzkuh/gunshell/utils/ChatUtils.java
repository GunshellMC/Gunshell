package com.jazzkuh.gunshell.utils;

import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
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
}
