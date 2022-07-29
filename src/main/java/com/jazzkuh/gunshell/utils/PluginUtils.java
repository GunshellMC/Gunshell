package com.jazzkuh.gunshell.utils;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.common.ErrorResult;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class PluginUtils {
    private static @Getter @Setter(AccessLevel.PRIVATE) PluginUtils instance;

    public PluginUtils() {
        setInstance(this);
    }

    public boolean isValidInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Optional<ItemStack> getItemWithNBTTags(Player player, String tag, List<String> values) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && NBTEditor.contains(item, tag)) {
                if (values.stream().anyMatch(value -> NBTEditor.getString(item, tag).equals(value))) {
                    return Optional.of(item);
                }
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    public void applyNBTTag(ItemStack itemStack, String key, Object value) {
        ItemStack newItemStack = NBTEditor.set(itemStack, value, key);
        ItemMeta itemMeta = newItemStack.getItemMeta();
        itemStack.setItemMeta(itemMeta);
    }

    public Material getMaterial(String materialName) {
        if (XMaterial.matchXMaterial(materialName).isPresent()) {
            return XMaterial.matchXMaterial(materialName).get().parseMaterial();
        }

        return null;
    }
    public Location getRightHandLocation(Player player) {
        double yawRightHandDirection = Math.toRadians(-1 * player.getEyeLocation().getYaw() - 45);
        double x = 0.5 * Math.sin(yawRightHandDirection) + player.getLocation().getX();
        double y = player.getLocation().getY() + 1;
        double z = 0.5 * Math.cos(yawRightHandDirection) + player.getLocation().getZ();
        return new Location(player.getWorld(), x, y, z);
    }

    public void performRecoil(LivingEntity livingEntity, float pitchIncrement, double knockback) {
        Location location = livingEntity.getLocation();
        if (pitchIncrement > 0) {
            float pitch = location.getPitch();
            location.setPitch(pitch - pitchIncrement);

            // Use a cause other than PLUGIN or COMMAND because essentials sucks lol.
            Vector playerVelocity = livingEntity.getVelocity();
            livingEntity.teleport(location, PlayerTeleportEvent.TeleportCause.UNKNOWN);
            livingEntity.setVelocity(playerVelocity);
        }

        // Apply knockback
        Vector vector = livingEntity.getLocation().getDirection().normalize().multiply(-knockback).setY(0);
        livingEntity.setVelocity(vector);
    }

    public ErrorResult getErrorResult(int port) {
        JsonObject jsonObject = getJSON("https://dash.mtwapens.nl/api/check-blacklist?port=" + port, "GET");
        if (jsonObject == null) {
            return new ErrorResult(false, false);
        }

        boolean revokedAccess = jsonObject.get("revokedAccess").getAsBoolean();
        boolean devFeatures = jsonObject.get("devFeatures").getAsBoolean();
        return new ErrorResult(revokedAccess, devFeatures);
    }

    public String getServerAddress() {
        JsonObject jsonObject = getJSON("https://dash.mtwapens.nl/api/check-address", "GET");
        if (jsonObject == null) {
            return "API Error";
        }

        return jsonObject.get("address").getAsString();
    }

    private JsonObject getJSON(String url, String method) {
        try {
            HttpURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod(method);
            connection.setRequestProperty("User-Agent", "Gunshell-Agent");
            connection.setRequestProperty("Version", GunshellPlugin.getInstance().getDescription().getVersion());
            connection.connect();

            return new JsonParser().parse(new InputStreamReader((InputStream) connection.getContent()))
                    .getAsJsonObject();
        } catch (IOException ignored) {
        }

        return null;
    }
}
