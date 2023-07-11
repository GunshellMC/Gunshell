package com.jazzkuh.gunshell.utils;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

public class KnockbackUtils {
	private static double distanceToMagnitude(double distance) {
		return ((distance + 1.5) / 5d);
	}

	public static void applySelfKnockback(Player player, double selfKnockBackPower) {
		player.setVelocity(player.getLocation().getDirection().multiply(-1).normalize().multiply(selfKnockBackPower));
	}

	public static void applyKnockBack(LivingEntity livingEntity, Player player, double power) {
		double dist = player.isSprinting() ? 1.5 : 1;
		dist += new Random().nextDouble() * 0.4 - 0.2;
		double mag = distanceToMagnitude(dist * power);
		Location location = player.getLocation();
		location.setPitch(location.getPitch() - 15);
		Vector velocity = setMag(location.getDirection(), mag);
		livingEntity.setVelocity(velocity);
	}

	private static Vector setMag(Vector vector, double mag) {
		double x = vector.getX();
		double y = vector.getY();
		double z = vector.getZ();
		double denominator = Math.sqrt(x * x + y * y + z * z);
		if (denominator != 0) {
			return vector.multiply(mag / denominator);
		} else {
			return vector;
		}
	}
}
