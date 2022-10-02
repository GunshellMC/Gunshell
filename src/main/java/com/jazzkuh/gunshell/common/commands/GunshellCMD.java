package com.jazzkuh.gunshell.common.commands;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.PluginUtils;
import com.jazzkuh.gunshell.utils.command.AbstractCommand;
import com.jazzkuh.gunshell.utils.command.CommandInvocation;
import com.jazzkuh.gunshell.utils.command.Subcommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class GunshellCMD extends AbstractCommand {

    public GunshellCMD() {
        super("gunshell");
    }

    @Override
    public void execute(CommandInvocation commandInvocation) {
        if (!hasPermission(getBasePermission(), commandInvocation.getCommandSender())) {
            this.onInfo(commandInvocation);
            return;
        }
        sendNotEnoughArguments(commandInvocation);
    }

    private void sendDefaultMessage(CommandSender sender) {
        ChatUtils.sendMessage(sender, "&8 ----------------------------------------------");
        ChatUtils.sendMessage(sender, "&8| &aThis server is using Gunshell &2v" + GunshellPlugin.getInstance().getDescription().getVersion() + "&a.");
        ChatUtils.sendMessage(sender, "&8| &2Description: &a" + GunshellPlugin.getInstance().getDescription().getDescription());
        ChatUtils.sendMessage(sender, "&8| &2Download: &a" + GunshellPlugin.getInstance().getDescription().getWebsite());
        ChatUtils.sendMessage(sender, "&8| &2Authors: &a" + StringUtils.join(GunshellPlugin.getInstance().getDescription().getAuthors(), ", "));
        ChatUtils.sendMessage(sender, "&8 ----------------------------------------------");
    }

    @Subcommand(name = "info", description = "Shows information about the plugin.")
    public void onInfo(CommandInvocation commandInvocation) {
        this.sendDefaultMessage(commandInvocation.getCommandSender());
    }

    @Subcommand(name = "reloadconfig", aliases = "rel|reload",
            description = "Reload the plugins configuration files.", permission = true)
    public void onReloadConfig(CommandInvocation commandInvocation) {
        try {
            GunshellPlugin.getInstance().getWeaponRegistry().registerFireables("weapons", "builtin.yml");
            MessagesConfig.SUCCESSFULLY_LOADED_TYPE.get(commandInvocation.getCommandSender(),
                    new PlaceHolder("Amount", String.valueOf(GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().size())),
                    new PlaceHolder("Type", "weapon types"));

            GunshellPlugin.getInstance().getWeaponRegistry().registerAmmunition("ammunition", "builtin.yml");
            MessagesConfig.SUCCESSFULLY_LOADED_TYPE.get(commandInvocation.getCommandSender(),
                    new PlaceHolder("Amount", String.valueOf(GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().size())),
                    new PlaceHolder("Type", "ammo types"));

            GunshellPlugin.getInstance().getWeaponRegistry().registerThrowables("throwables", "builtin.yml");
            MessagesConfig.SUCCESSFULLY_LOADED_TYPE.get(commandInvocation.getCommandSender(),
                    new PlaceHolder("Amount", String.valueOf(GunshellPlugin.getInstance().getWeaponRegistry().getThrowables().size())),
                    new PlaceHolder("Type", "throwable types"));

            GunshellPlugin.getInstance().getWeaponRegistry().registerMelees("melee", "builtin.yml");
            MessagesConfig.SUCCESSFULLY_LOADED_TYPE.get(commandInvocation.getCommandSender(),
                    new PlaceHolder("Amount", String.valueOf(GunshellPlugin.getInstance().getWeaponRegistry().getMelees().size())),
                    new PlaceHolder("Type", "melee types"));

            GunshellPlugin.getMessages().reloadConfig();
            GunshellPlugin.getInstance().reloadConfig();

            MessagesConfig.SUCCESSFULLY_RELOADED_CONFIGURATION.get(commandInvocation.getCommandSender());
        } catch (Exception exception) {
            MessagesConfig.ERROR_WHILST_LOADING_CONFIGURATION.get(commandInvocation.getCommandSender(),
                    new PlaceHolder("Error", exception.getMessage()));
            exception.printStackTrace();
        }
    }

    @Subcommand(name = "getweapon", usage = "<weaponType> <durability> [player]", permission = true,
            aliases = "get|weapon", description = "Get a weapon from the config.")
    public void onGetWeapon(CommandInvocation commandInvocation) {
        String[] args = commandInvocation.getArguments();
        CommandSender sender = commandInvocation.getCommandSender();

        if (args.length < 3) {
            this.sendNotEnoughArguments(commandInvocation);
            return;
        }

        String weaponKey = args[1].toLowerCase();
        if (!GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().containsKey(weaponKey)) {
            MessagesConfig.ERROR_WEAPON_NOT_FOUND.get(sender);
            return;
        }

        if (!PluginUtils.getInstance().isValidInteger(args[2])) {
            MessagesConfig.ERROR_INVALID_INTEGER.get(sender);
            return;
        }

        int durability = Integer.parseInt(args[2]);

        GunshellFireable fireable = GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);

        if (args.length > 3) {
            Player target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
                return;
            }

            target.getInventory().addItem(fireable.getItemStack(durability));
            MessagesConfig.SUCCESSFULLY_ADDED_TO_INVENTORY.get(sender);
        } else {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(fireable.getItemStack(durability));
                MessagesConfig.SUCCESSFULLY_ADDED_TO_INVENTORY.get(sender);
            } else {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
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
        if (!GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().containsKey(ammoKey)) {
            MessagesConfig.ERROR_AMMO_NOT_FOUND.get(sender);
            return;
        }

        GunshellAmmunition ammunition = GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().get(ammoKey);

        if (args.length > 2) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
                return;
            }

            target.getInventory().addItem(ammunition.getItem().toItemStack());
            MessagesConfig.SUCCESSFULLY_ADDED_AMMO_TO_INVENTORY.get(sender);
        } else {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(ammunition.getItem().toItemStack());
                MessagesConfig.SUCCESSFULLY_ADDED_AMMO_TO_INVENTORY.get(sender);
            } else {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
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
        if (!GunshellPlugin.getInstance().getWeaponRegistry().getThrowables().containsKey(throwableKey)) {
            MessagesConfig.ERROR_THROWABLE_NOT_FOUND.get(sender);
            return;
        }

        GunshellThrowable throwable = GunshellPlugin.getInstance().getWeaponRegistry().getThrowables().get(throwableKey);

        if (args.length > 2) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
                return;
            }

            target.getInventory().addItem(throwable.getItem().toItemStack());
            MessagesConfig.SUCCESSFULLY_ADDED_THROWABLE_TO_INVENTORY.get(sender);
        } else {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(throwable.getItem().toItemStack());
                MessagesConfig.SUCCESSFULLY_ADDED_THROWABLE_TO_INVENTORY.get(sender);
            } else {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
            }
        }
    }

    @Subcommand(name = "getmelee", usage = "<meleeType> <durability> [player]", permission = true,
            aliases = "melee", description = "Get a melee weapon from the config files.", playerOnly = true)
    public void onGetMelee(CommandInvocation commandInvocation) {
        String[] args = commandInvocation.getArguments();
        CommandSender sender = commandInvocation.getCommandSender();

        if (args.length < 3) {
            this.sendNotEnoughArguments(commandInvocation);
            return;
        }

        String meleeKey = args[1].toLowerCase();
        if (!GunshellPlugin.getInstance().getWeaponRegistry().getMelees().containsKey(meleeKey)) {
            MessagesConfig.ERROR_MELEE_NOT_FOUND.get(sender);
            return;
        }

        if (!PluginUtils.getInstance().isValidInteger(args[2])) {
            MessagesConfig.ERROR_INVALID_INTEGER.get(sender);
            return;
        }

        int durability = Integer.parseInt(args[2]);

        GunshellMelee melee = GunshellPlugin.getInstance().getWeaponRegistry().getMelees().get(meleeKey);

        if (args.length > 3) {
            Player target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
                return;
            }

            target.getInventory().addItem(melee.getItemStack(durability));
            MessagesConfig.SUCCESSFULLY_ADDED_MELEE_TO_INVENTORY.get(sender);
        } else {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(melee.getItemStack(durability));
                MessagesConfig.SUCCESSFULLY_ADDED_MELEE_TO_INVENTORY.get(sender);
            } else {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
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
            Set<String> weaponKeys = GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().keySet();
            return getApplicableTabCompleters(args[1], weaponKeys);
        }

        if (args.length == 2 && Stream.of("getammo", "ammo").anyMatch(cmd -> cmd.equalsIgnoreCase(args[0]))) {
            Set<String> ammoKeys = GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().keySet();
            return getApplicableTabCompleters(args[1], ammoKeys);
        }

        if (args.length == 2 && Stream.of("getthrowable", "getgrenade", "throwable").anyMatch(cmd -> cmd.equalsIgnoreCase(args[0]))) {
            Set<String> throwableKeys = GunshellPlugin.getInstance().getWeaponRegistry().getThrowables().keySet();
            return getApplicableTabCompleters(args[1], throwableKeys);
        }

        if (args.length == 2 && Stream.of("getmelee", "melee").anyMatch(cmd -> cmd.equalsIgnoreCase(args[0]))) {
            Set<String> meleeKeys = GunshellPlugin.getInstance().getWeaponRegistry().getMelees().keySet();
            return getApplicableTabCompleters(args[1], meleeKeys);
        }

        return null;
    }
}