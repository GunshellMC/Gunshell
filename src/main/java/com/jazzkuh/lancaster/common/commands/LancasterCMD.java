package com.jazzkuh.lancaster.common.commands;

import com.jazzkuh.core.utils.ChatUtils;
import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.api.objects.LancasterAmmunition;
import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import com.jazzkuh.lancaster.api.objects.LancasterThrowable;
import com.jazzkuh.lancaster.utils.command.AbstractCommand;
import com.jazzkuh.lancaster.utils.command.CommandInvocation;
import com.jazzkuh.lancaster.utils.command.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class LancasterCMD extends AbstractCommand {

    public LancasterCMD() {
        super("lancaster");
    }

    @Override
    public void execute(CommandInvocation commandInvocation) {
        if (!hasPermission(getBasePermission(), commandInvocation.getCommandSender())) return;
        sendNotEnoughArguments(commandInvocation);
    }

    @Subcommand(name = "reloadconfig", aliases = "rel|reload",
            description = "Reload the plugins configuration files.", permission = true)
    public void onReloadConfig(CommandInvocation commandInvocation) {
        try {
            LancasterPlugin.getInstance().getWeaponRegistry().registerFireables("weapons", "builtin.yml");
            ChatUtils.sendMessage(commandInvocation.getCommandSender(), "<primary>Succesfully loaded <secondary>" + LancasterPlugin.getInstance().getWeaponRegistry().getWeapons().size() + "<primary> fireables.");

            LancasterPlugin.getInstance().getWeaponRegistry().registerAmmunition("ammunition", "builtin.yml");
            ChatUtils.sendMessage(commandInvocation.getCommandSender(), "<primary>Succesfully loaded <secondary>" + LancasterPlugin.getInstance().getWeaponRegistry().getAmmunition().size() + "<primary> ammunition types.");

            LancasterPlugin.getInstance().getWeaponRegistry().registerThrowables("throwables", "builtin.yml");
            ChatUtils.sendMessage(commandInvocation.getCommandSender(), "<primary>Succesfully loaded <secondary>" + LancasterPlugin.getInstance().getWeaponRegistry().getThrowables().size() + "<primary> throwables.");

            LancasterPlugin.getInstance().reloadConfig();
            ChatUtils.sendMessage(commandInvocation.getCommandSender(), "<primary>Succesfully reloaded the configuration files.");
        } catch (Exception exception) {
            ChatUtils.sendMessage(commandInvocation.getCommandSender(), "<error>Failed to reload the configuration files.");
            exception.printStackTrace();
        }
    }

    @Subcommand(name = "getweapon", usage = "<weaponType> [player]", permission = true,
            aliases = "get|weapon", description = "Get a weapon from the config.")
    public void onGetWeapon(CommandInvocation commandInvocation) {
        String[] args = commandInvocation.getArguments();
        CommandSender sender = commandInvocation.getCommandSender();

        if (args.length < 2) {
            this.sendNotEnoughArguments(commandInvocation);
            return;
        }

        String weaponKey = args[1].toLowerCase();
        if (!LancasterPlugin.getInstance().getWeaponRegistry().getWeapons().containsKey(weaponKey)) {
            ChatUtils.sendMessage(sender, "<error>Weapon " + weaponKey + " does not exist.");
            return;
        }
        LancasterFireable fireable = LancasterPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

        if (args.length > 3) {
            Player target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                ChatUtils.sendMessage(sender, "<error>The given player could not be found.");
                return;
            }

            target.getInventory().addItem(fireable.getItemStack());
            ChatUtils.sendMessage(sender, "<primary>Succesfully added " + fireable.getItemStack().getType().name() + " to " + target.getName() + "'s inventory.");
        } else {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(fireable.getItemStack());
                ChatUtils.sendMessage(sender, "<primary>Succesfully added " + fireable.getItemStack().getType().name() + " to your inventory.");
            } else {
                ChatUtils.sendMessage(sender, "<error>You must be a player to use this command.");
            }
        }
    }

    @Subcommand(name = "getammo", usage = "<ammoType> [player]", permission = true,
            aliases = "ammo", description = "Get ammo from the config files.")
    public void onGetAmmo(CommandInvocation commandInvocation) {
        String[] args = commandInvocation.getArguments();
        CommandSender sender = commandInvocation.getCommandSender();

        if (args.length < 2) {
            this.sendNotEnoughArguments(commandInvocation);
            return;
        }

        String ammoKey = args[1].toLowerCase();
        if (!LancasterPlugin.getInstance().getWeaponRegistry().getAmmunition().containsKey(ammoKey)) {
            ChatUtils.sendMessage(sender, "<error>Ammunition " + ammoKey + " does not exist.");
            return;
        }

        LancasterAmmunition ammunition = LancasterPlugin.getInstance().getWeaponRegistry().getAmmunition().get(ammoKey);

        if (args.length > 2) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                ChatUtils.sendMessage(sender, "<error>The given player could not be found.");
                return;
            }

            target.getInventory().addItem(ammunition.getItem().toItemStack());
            ChatUtils.sendMessage(sender, "<primary>Succesfully added " + ammunition.getItem().toItemStack().getType().name() + " to " + target.getName() + "'s inventory.");
        } else {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(ammunition.getItem().toItemStack());
                ChatUtils.sendMessage(sender, "<primary>Succesfully added " + ammunition.getItem().toItemStack().getType().name() + " to your inventory.");
            } else {
                ChatUtils.sendMessage(sender, "<error>You must be a player to use this command.");
            }
        }
    }

    @Subcommand(name = "getthrowable", usage = "<throwableType> [player]", permission = true,
            aliases = "getgrenade|throwable", description = "Get a throwable from the config files.")
    public void onGetThrowable(CommandInvocation commandInvocation) {
        String[] args = commandInvocation.getArguments();
        CommandSender sender = commandInvocation.getCommandSender();

        if (args.length < 2) {
            this.sendNotEnoughArguments(commandInvocation);
            return;
        }

        String throwableKey = args[1].toLowerCase();
        if (!LancasterPlugin.getInstance().getWeaponRegistry().getThrowables().containsKey(throwableKey)) {
            ChatUtils.sendMessage(sender, "<error>Throwable " + throwableKey + " does not exist.");
            return;
        }

        LancasterThrowable throwable = LancasterPlugin.getInstance().getWeaponRegistry().getThrowables().get(throwableKey);

        if (args.length > 2) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                ChatUtils.sendMessage(sender, "<error>The given player could not be found.");
                return;
            }

            target.getInventory().addItem(throwable.getItem().toItemStack());
            ChatUtils.sendMessage(sender, "<primary>Succesfully added " + throwable.getItem().toItemStack().getType().name() + " to " + target.getName() + "'s inventory.");
        } else {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(throwable.getItem().toItemStack());
                ChatUtils.sendMessage(sender, "<primary>Succesfully added " + throwable.getItem().toItemStack().getType().name() + " to your inventory.");
            } else {
                ChatUtils.sendMessage(sender, "<error>You must be a player to use this command.");
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandInvocation commandInvocation) {
        String[] args = commandInvocation.getArguments();
        if (!commandInvocation.getCommandSender().hasPermission(getBasePermission())) {
            return Collections.emptyList();
        }

        if (args.length == 2 && Stream.of("getweapon", "get", "weapon").anyMatch(cmd -> cmd.equalsIgnoreCase(args[0]))) {
            Set<String> weaponKeys = LancasterPlugin.getInstance().getWeaponRegistry().getWeapons().keySet();
            return getApplicableTabCompleters(args[1], weaponKeys);
        }

        if (args.length == 2 && Stream.of("getammo", "ammo").anyMatch(cmd -> cmd.equalsIgnoreCase(args[0]))) {
            Set<String> ammoKeys = LancasterPlugin.getInstance().getWeaponRegistry().getAmmunition().keySet();
            return getApplicableTabCompleters(args[1], ammoKeys);
        }

        if (args.length == 2 && Stream.of("getthrowable", "getgrenade", "throwable").anyMatch(cmd -> cmd.equalsIgnoreCase(args[0]))) {
            Set<String> throwableKeys = LancasterPlugin.getInstance().getWeaponRegistry().getThrowables().keySet();
            return getApplicableTabCompleters(args[1], throwableKeys);
        }
        return null;
    }
}