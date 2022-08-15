package com.jazzkuh.gunshell.common.configuration.lang;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.utils.ChatUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.CommandSender;

public enum MessagesConfig {
    ERROR_NO_PERMISSION("error.no-permission", "&cYou don't have permission to use this command."),
    ERROR_WEAPON_NOT_FOUND("error.weapon-not-found", "&cThe given weapon type could not be found."),
    ERROR_AMMO_NOT_FOUND("error.ammo-not-found", "&cThe given ammo type could not be found."),
    ERROR_THROWABLE_NOT_FOUND("error.throwable-not-found", "&cThe given throwable type could not be found."),
    ERROR_MELEE_NOT_FOUND("error.melee-not-found", "&cThe given melee type could not be found."),
    ERROR_INVALID_INTEGER("error.invalid-integer", "&cThe given integer is invalid."),
    ERROR_PLAYER_NOT_FOUND("error.player-not-found", "&cThe given player could not be found."),
    ERROR_AMMUNITION_NOT_FOUND("error.ammunition-not-found", "&cAmmunition not found for key: <Key>"),
    ERROR_OUT_OF_AMMO("error.out-of-ammo", "&cYour gun is empty!"),
    ERROR_CANNOT_USE_GUNSHELL_WEAPONS_HERE("error.cannot-use-gunshell-weapons-here", "&cYou cannot use Gunshell weapons here!"),
    ERROR_WHILST_LOADING_CONFIGURATION("error.loading-configuration", "&cSomething went wrong while loading the configuration files: <Error>"),
    SUCCESSFULLY_ADDED_TO_INVENTORY("common.success.added-to-inventory", "&aSuccessfully added weapon to inventory."),
    SUCCESSFULLY_ADDED_AMMO_TO_INVENTORY("common.success.added-ammo-to-inventory", "&aSuccessfully added ammo to inventory."),
    SUCCESSFULLY_ADDED_THROWABLE_TO_INVENTORY("common.success.added-throwable-to-inventory", "&aSuccessfully added throwable to inventory."),
    SUCCESSFULLY_ADDED_MELEE_TO_INVENTORY("common.success.added-melee-to-inventory", "&aSuccessfully added melee to inventory."),
    SUCCESSFULLY_RELOADED_CONFIGURATION("common.success.reloaded-configuration", "&aYou successfully reloaded the configuration files."),
    SUCCESSFULLY_LOADED_TYPE("common.success.loaded-type", "&a<Amount> <Type> has been loaded."),
    RELOADING_START("common.reloading.start", "&aYour weapon is now reloading..."),
    RELOADING_FINISHED("common.reloading.finished", "&aYour weapon has been reloaded."),
    UNLOADING_FINISHED("common.unloading.finished", "&aYour weapon has been unloaded."),
    SHOW_AMMO_DURABILITY("common.ammo-durability", "&aDurability: &f<Durability>\n&aAmmo: &7<Ammo>&8/&7<MaxAmmo>"),
    SHOW_DURABILITY("common.durability", "&aDurability: &f<Durability>"),
    BULLET_HIT_OTHER("common.bullet.hit-other", "&cYou've hit &4<Name> &cwith your shot."),
    BULLET_HIT_OTHER_HEADSHOT("common.bullet.hit-other-headshot", "&cYou've hit &4<Name> &cwith a headshot."),
    BULLET_HIT_BY_PLAYER("common.bullet.hit-by-player", "&cYou've been shot by &4<Name>&c."),
    BULLET_SHOT_LAST("common.bullet.shot-last", "&cYou've shot the last bullet in your magazine.");


    private final @Getter String path;
    private final @Getter(AccessLevel.PRIVATE) String message;

    MessagesConfig(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String get() {
        String msg = GunshellPlugin.getMessages().getConfig().getString(this.getPath());
        return ChatUtils.color(msg);
    }

    public void get(CommandSender commandSender) {
        String msg = GunshellPlugin.getMessages().getConfig().getString(this.getPath());
        ChatUtils.sendMessage(commandSender, msg);
    }

    public void get(CommandSender commandSender, PlaceHolder... placeholder) {
        String msg = GunshellPlugin.getMessages().getConfig().getString(this.getPath());
        for (PlaceHolder placeHolder : placeholder) {
            if (msg == null) continue;
            msg = msg.replaceAll(placeHolder.getPlaceholder(), placeHolder.getValue());
        }

        ChatUtils.sendMessage(commandSender, msg);
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
