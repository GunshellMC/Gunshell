package com.jazzkuh.gunshell.utils.command;

import com.jazzkuh.commandlib.spigot.AnnotationCommand;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.utils.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

public abstract class ExtendedAnnotationCommand extends AnnotationCommand {
    protected void sendDefaultMessage(CommandSender sender) {
        ChatUtils.sendMessage(sender, "&8 ----------------------------------------------");
        ChatUtils.sendMessage(sender, "&8| &aThis server is using Gunshell &2v" + GunshellPlugin.getInstance().getDescription().getVersion() + "&a.");
        ChatUtils.sendMessage(sender, "&8| &2Description: &a" + GunshellPlugin.getInstance().getDescription().getDescription());
        ChatUtils.sendMessage(sender, "&8| &2Download: &a" + GunshellPlugin.getInstance().getDescription().getWebsite());
        ChatUtils.sendMessage(sender, "&8| &2Authors: &a" + StringUtils.join(GunshellPlugin.getInstance().getDescription().getAuthors(), ", "));
        ChatUtils.sendMessage(sender, "&8 ----------------------------------------------");
    }
}