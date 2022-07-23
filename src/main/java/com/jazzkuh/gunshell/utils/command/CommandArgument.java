package com.jazzkuh.gunshell.utils.command;

import lombok.Getter;

public class CommandArgument {
    private final @Getter String arguments;
    private final @Getter String description;
    private final @Getter String permission;

    public CommandArgument(String arguments, String description, String permission) {
        this.arguments = arguments;
        this.description = description;
        this.permission = permission;
    }

    public CommandArgument(String arguments, String description) {
        this.arguments = arguments;
        this.description = description;
        this.permission = null;
    }
}
