package com.jazzkuh.gunshell.common.configuration.lang;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.utils.ChatUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.CommandSender;

public enum MessagesConfig {
    PREFIX("messages.general.prefix", "&8[&cGunshell&8] &s"),
    ERROR_AMMUNITION_NOT_FOUND("messages.error.ammunition_not_found", "&cAmmunition not found for key: %s");

    private final @Getter String path;
    private final @Getter(AccessLevel.PRIVATE) String message;

    MessagesConfig(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public void get(CommandSender commandSender) {
        String msg = GunshellPlugin.getMessages().getConfig().getString(this.getPath());
        ChatUtils.sendMessage(commandSender, msg);
    }

    public void get(CommandSender commandSender, String... placeholder) {
        String msg = GunshellPlugin.getMessages().getConfig().getString(this.getPath());
        ChatUtils.sendMessage(commandSender, String.format(msg, placeholder));
    }

    public static void init() {
        for (MessagesConfig msg : MessagesConfig.values()) {
            if (GunshellPlugin.getMessages().getConfig().getString(msg.getPath()) == null) {
                GunshellPlugin.getMessages().getConfig().set(msg.getPath(), msg.getMessage());
            }
        }

        GunshellPlugin.getMessages().saveConfig();
    }
}
