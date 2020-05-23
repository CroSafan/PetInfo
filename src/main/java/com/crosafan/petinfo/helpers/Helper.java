package com.crosafan.petinfo.helpers;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.StringUtils;

public class Helper {

	public static String[] ignoreTooltips = { "§aPet Score Rewards", "§aGo Back", "§cClose", "§aConvert Pet to an Item", "§cHide Pets", "§aNext Page", "§a§aPets" };

	public static boolean isPetMenuOpen() {
		if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest) {
			ContainerChest chest = (ContainerChest) Minecraft.getMinecraft().thePlayer.openContainer;
			IInventory inv = chest.getLowerChestInventory();
			if (inv.getName().contains("Pets")) {
				return true;
			}
		}
		return false;
	}

	public static boolean ignoreTooltip(String tooltip) {
		if (tooltip.isEmpty()) {
			return true;
		}

		// if it matches it shouldnt be displayed
		return Arrays.stream(ignoreTooltips).anyMatch(tooltip::equals);
	}

	public static boolean isPlayerInSkyblock() {
		boolean isInSkyblock = false;
		if (Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1) != null) {
			if ((StringUtils.stripControlCodes(Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName()).contains("SKYBLOCK"))) {
				isInSkyblock = true;
			}
		}
		return isInSkyblock;
	}

}
