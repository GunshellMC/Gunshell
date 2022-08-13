package com.jazzkuh.lancaster.common.tasks;

import com.jazzkuh.core.utils.ChatUtils;
import com.jazzkuh.lancaster.LancasterPlugin;
import com.jazzkuh.lancaster.api.objects.LancasterAmmunition;
import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import com.jazzkuh.lancaster.common.configuration.DefaultConfig;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerActionbarTask implements Runnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();

            if (NBTEditor.contains(itemStack, "lancaster_weapon_key")) {
                String weaponKey = NBTEditor.getString(itemStack, "lancaster_weapon_key");
                LancasterFireable lancasterFireable = LancasterPlugin.getInstance().getWeaponRegistry().getWeapons().get(weaponKey);
                LancasterAmmunition lancasterAmmunition = LancasterPlugin.getInstance().getWeaponRegistry().getAmmunition()
                        .get(NBTEditor.getString(itemStack, "lancaster_weapon_ammotype"));

                int ammo = NBTEditor.getInt(itemStack, "lancaster_weapon_ammo");
                String ammoColor = ammo <= 0 ? "<color:#d41936>" : "<white>";
                Component component = ChatUtils.color("<font:serif>" + ammoColor + ammo + "</font>" +
                        "<font:serif-superscript><gray>" + lancasterFireable.getMaxAmmo() + "</font>" +
                        "<white> " + lancasterAmmunition.getAmmoIcon() + "</white>");

                if (LancasterPlugin.getInstance().getReloadingSet().contains(player.getUniqueId())) {
                    component = component.append(ChatUtils.color(" <error><font:serif-superscript>R</font></error>"));
                }

                player.sendActionBar(component);
            }
        }
    }
}
