package com.jazzkuh.gunshell.common.commands;

import com.jazzkuh.commandlib.common.annotations.*;
import com.jazzkuh.commandlib.common.annotations.Subcommand;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.utils.command.ExtendedAnnotationCommand;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("gunshell")
@AllArgsConstructor
public class GunshellCommand extends ExtendedAnnotationCommand {
    @Main
    @Alias("gs")
    public void main(CommandSender sender) {
        if (!sender.hasPermission("gunshell.commands.gunshell")) {
            this.onInfo(sender);
            return;
        }

        this.formatUsage(sender);
    }

    @Subcommand("info")
    public void onInfo(CommandSender sender) {
        sendDefaultMessage(sender);
    }

    @Subcommand("reload")
    @Permission("gunshell.commands.gunshell.reload")
    public void onReload(CommandSender sender) {
        try {
            GunshellPlugin.getInstance().getWeaponRegistry().registerFireables("weapons", "builtin.yml");
            MessagesConfig.SUCCESSFULLY_LOADED_TYPE.get(sender,
                    new PlaceHolder("Amount", String.valueOf(GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().size())),
                    new PlaceHolder("Type", "weapon types"));

            GunshellPlugin.getInstance().getWeaponRegistry().registerAmmunition("ammunition", "builtin.yml");
            MessagesConfig.SUCCESSFULLY_LOADED_TYPE.get(sender,
                    new PlaceHolder("Amount", String.valueOf(GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().size())),
                    new PlaceHolder("Type", "ammo types"));

            GunshellPlugin.getInstance().getWeaponRegistry().registerThrowables("throwables", "builtin.yml");
            MessagesConfig.SUCCESSFULLY_LOADED_TYPE.get(sender,
                    new PlaceHolder("Amount", String.valueOf(GunshellPlugin.getInstance().getWeaponRegistry().getThrowables().size())),
                    new PlaceHolder("Type", "throwable types"));

            GunshellPlugin.getInstance().getWeaponRegistry().registerMelees("melee", "builtin.yml");
            MessagesConfig.SUCCESSFULLY_LOADED_TYPE.get(sender,
                    new PlaceHolder("Amount", String.valueOf(GunshellPlugin.getInstance().getWeaponRegistry().getMelees().size())),
                    new PlaceHolder("Type", "melee types"));

            GunshellPlugin.getMessages().reloadConfig();
            GunshellPlugin.getInstance().reloadConfig();

            MessagesConfig.SUCCESSFULLY_RELOADED_CONFIGURATION.get(sender);
        } catch (Exception exception) {
            MessagesConfig.ERROR_WHILST_LOADING_CONFIGURATION.get(sender,
                    new PlaceHolder("Error", exception.getMessage()));
            GunshellPlugin.getInstance().getLogger().severe("An error occurred whilst reloading the configuration.");
        }
    }

    @Subcommand("getweapon")
    @Usage("<weaponType> <durability> [player]")
    @Permission("gunshell.commands.gunshell.getweapon")
    public void onGetWeapon(CommandSender sender, GunshellFireable fireable, int durability, @Optional Player player) {
        if (player == null) {
            if (!(sender instanceof Player)) {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
                return;
            }

            ((Player) sender).getInventory().addItem(fireable.getItemStack(durability));
            MessagesConfig.SUCCESSFULLY_ADDED_TO_INVENTORY.get(sender);
            return;
        }

        player.getInventory().addItem(fireable.getItemStack(durability));
        MessagesConfig.SUCCESSFULLY_ADDED_TO_INVENTORY.get(sender);
    }

    @Subcommand("getammo")
    @Usage("<ammoType> [player]")
    @Permission("gunshell.commands.gunshell.getammo")
    public void onGetAmmo(CommandSender sender, GunshellAmmunition ammunition, @Optional Player player) {
        if (player == null) {
            if (!(sender instanceof Player)) {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
                return;
            }

            ((Player) sender).getInventory().addItem(ammunition.getItem().toItemStack());
            MessagesConfig.SUCCESSFULLY_ADDED_AMMO_TO_INVENTORY.get(sender);
            return;
        }

        player.getInventory().addItem(ammunition.getItem().toItemStack());
        MessagesConfig.SUCCESSFULLY_ADDED_AMMO_TO_INVENTORY.get(sender);
    }

    @Subcommand("getthrowable")
    @Usage("<throwableType> [player]")
    @Permission("gunshell.commands.gunshell.getthrowable")
    public void onGetThrowable(CommandSender sender, GunshellThrowable throwable, @Optional Player player) {
        if (player == null) {
            if (!(sender instanceof Player)) {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
                return;
            }

            ((Player) sender).getInventory().addItem(throwable.getItem().toItemStack());
            MessagesConfig.SUCCESSFULLY_ADDED_THROWABLE_TO_INVENTORY.get(sender);
            return;
        }

        player.getInventory().addItem(throwable.getItem().toItemStack());
        MessagesConfig.SUCCESSFULLY_ADDED_THROWABLE_TO_INVENTORY.get(sender);
    }

    @Subcommand("getmelee")
    @Usage("<meleeType> <durability> [player]")
    @Permission("gunshell.commands.gunshell.getmelee")
    public void onGetMelee(CommandSender sender, GunshellMelee melee, int durability, @Optional Player player) {
        if (player == null) {
            if (!(sender instanceof Player)) {
                MessagesConfig.ERROR_PLAYER_NOT_FOUND.get(sender);
                return;
            }

            ((Player) sender).getInventory().addItem(melee.getItemStack(durability));
            MessagesConfig.SUCCESSFULLY_ADDED_MELEE_TO_INVENTORY.get(sender);
            return;
        }

        player.getInventory().addItem(melee.getItemStack(durability));
        MessagesConfig.SUCCESSFULLY_ADDED_MELEE_TO_INVENTORY.get(sender);
    }
}