package com.jazzkuh.lancaster.utils.command;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandInvocation {
    private final @Getter CommandSender commandSender;
    private final @Getter Command command;
    private final @Getter String label;
    private final @Getter String[] arguments;

    public CommandInvocation(CommandSender commandSender, Command command, String label, String[] arguments) {
        this.commandSender = commandSender;
        this.command = command;
        this.label = label;
        this.arguments = arguments;
    }
}
