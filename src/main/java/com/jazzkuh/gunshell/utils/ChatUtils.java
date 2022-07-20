package com.jazzkuh.gunshell.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Create a new Component from a string.
     * @param message The message to send
     */
    public static Component color(String message) {
        return miniMessage.deserialize(message);
    }

    /**
     * Create a new Component from a string list.
     * @param lore The lore list to color
     */
    public static List<Component> color(List<String> lore) {
        List<Component> components = new ArrayList<>();
        for (String line : lore) {
            components.add(color(line));
        }
        return components;
    }

    /**
     * Send a message to a CommandSender
     * @param commandSender The CommandSender to send the message to
     * @param message The message to send
     */
    public static void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(color(message));
    }

    /**
     * Send a component to a CommandSender
     * @param commandSender The CommandSender to send the message to
     * @param message The component to send
     */
    public static void sendMessage(CommandSender commandSender, Component message) {
        commandSender.sendMessage(message);
    }
}
