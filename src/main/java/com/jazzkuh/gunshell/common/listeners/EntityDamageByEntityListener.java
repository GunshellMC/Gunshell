package com.jazzkuh.gunshell.common.listeners;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.events.FireableFireEvent;
import com.jazzkuh.gunshell.api.events.MeleeDamageEvent;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.common.AmmunitionActionImpl;
import com.jazzkuh.gunshell.common.AmmunitionActionRegistry;
import com.jazzkuh.gunshell.common.MeleeActionImpl;
import com.jazzkuh.gunshell.common.MeleeActionRegistry;
import com.jazzkuh.gunshell.common.configuration.DefaultConfig;
import com.jazzkuh.gunshell.common.configuration.PlaceHolder;
import com.jazzkuh.gunshell.common.configuration.lang.MessagesConfig;
import com.jazzkuh.gunshell.utils.ChatUtils;
import com.jazzkuh.gunshell.utils.PluginUtils;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class EntityDamageByEntityListener implements Listener {
    private final String DURABILITY_KEY = "gunshell_melee_durability";

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (!NBTEditor.contains(itemStack, "gunshell_melee_key")) return;

        String cooldownKey = DefaultConfig.PER_WEAPON_COOLDOWN.asBoolean() ?
                player.getUniqueId() + "_" + player.getInventory().getHeldItemSlot() :
                player.getUniqueId() + "_global";

        String meleeKey = NBTEditor.getString(itemStack, "gunshell_melee_key");
        GunshellMelee melee = GunshellPlugin.getInstance().getWeaponRegistry().getMelees().get(meleeKey);

        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        event.setCancelled(true);
        event.setDamage(0);

        int durability = NBTEditor.getInt(itemStack, DURABILITY_KEY);

        if (durability <= 0) {
            player.getInventory().removeItem(itemStack);
            return;
        }

        if (hasCooldown(cooldownKey, melee) || hasGrabCooldown(player.getUniqueId(), melee)) return;

        MeleeDamageEvent meleeDamageEvent = new MeleeDamageEvent(player, melee);
        if (meleeDamageEvent.isCancelled()) return;
        Bukkit.getPluginManager().callEvent(meleeDamageEvent);

        GunshellPlugin.getInstance().getMeleeCooldownMap().put(cooldownKey, System.currentTimeMillis());
        PluginUtils.getInstance().applyNBTTag(itemStack, DURABILITY_KEY, durability - 1);
        melee.updateItemMeta(itemStack);

        MessagesConfig.SHOW_DURABILITY.get(player,
                new PlaceHolder("Durability", String.valueOf(NBTEditor.getInt(itemStack, DURABILITY_KEY))));

        MeleeActionImpl meleeAction = MeleeActionRegistry.getAction(melee, melee.getActionType());
        if (meleeAction == null) {
            ChatUtils.sendMessage(player, "&cError: &4Melee action not found!");
            return;
        }

        meleeAction.fireAction(entity, player, melee.getConfiguration());
    }

    private boolean hasCooldown(String cooldownKey, GunshellMelee melee) {
        Long lastUsed = GunshellPlugin.getInstance().getMeleeCooldownMap().getOrDefault(cooldownKey, 0L);
        return System.currentTimeMillis() <= (lastUsed + melee.getCooldown());
    }

    private boolean hasGrabCooldown(UUID uniqueId, GunshellMelee melee) {
        Long lastUsed = GunshellPlugin.getInstance().getMeleeGrabCooldownMap().getOrDefault(uniqueId, 0L);
        return System.currentTimeMillis() <= (lastUsed + (melee.getGrabCooldown() * 1000));
    }
}