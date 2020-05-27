package com.crosafan.petinfo.helpers;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crosafan.petinfo.PetInfo;
import com.ibm.icu.text.NumberFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;

public class Helper {

	private static final Pattern PET_LEVEL_PROGRESS_PATTERN = Pattern.compile("§7Progress to Level (\\d+): §e(\\d*\\.?\\d*)%");
	private static final Pattern PET_CURRENT_XP_PATTERN = Pattern.compile(".* (§e([0-9.,]+)§6/§e([0-9.,]+)k?)");
	private static final Pattern PET_HELD_ITEM = Pattern.compile("§6Held Item: (§f|§a|§9|§5)(Farming|Mining|Combat|Foraging|Fishing|All) (Skills )?Exp Boost");
	private static int[][] petLevelTable = { { 0, 0, 0, 0, 0 }, { 100, 175, 275, 440, 660 }, { 110, 190, 300, 490, 730 }, { 120, 210, 330, 540, 800 }, { 130, 230, 360, 600, 880 }, { 145, 250, 400, 660, 960 }, { 160, 275, 440, 730, 1050 },
			{ 175, 300, 490, 800, 1150 }, { 190, 330, 540, 880, 1260 }, { 210, 360, 600, 960, 1380 }, { 230, 400, 660, 1050, 1510 }, { 250, 440, 730, 1150, 1650 }, { 275, 490, 800, 1260, 1800 }, { 300, 540, 880, 1380, 1960 },
			{ 330, 600, 960, 1510, 2130 }, { 360, 660, 1050, 1650, 2310 }, { 400, 730, 1150, 1800, 2500 }, { 440, 800, 1260, 1960, 2700 }, { 490, 880, 1380, 2130, 2920 }, { 540, 960, 1510, 2310, 3160 }, { 600, 1050, 1650, 2500, 3420 },
			{ 660, 1150, 1800, 2700, 3700 }, { 730, 1260, 1960, 2920, 4000 }, { 800, 1380, 2130, 3160, 4350 }, { 880, 1510, 2310, 3420, 4750 }, { 960, 1650, 2500, 3700, 5200 }, { 1050, 1800, 2700, 4000, 5700 }, { 1150, 1960, 2920, 4350, 6300 },
			{ 1260, 2130, 3160, 4750, 7000 }, { 1380, 2310, 3420, 5200, 7800 }, { 1510, 2500, 3700, 5700, 8700 }, { 1650, 2700, 4000, 6300, 9700 }, { 1800, 2920, 4350, 7000, 10800 }, { 1960, 3160, 4750, 7800, 12000 },
			{ 2130, 3420, 5200, 8700, 13300 }, { 2310, 3700, 5700, 9700, 14700 }, { 2500, 4000, 6300, 10800, 16200 }, { 2700, 4350, 7000, 12000, 17800 }, { 2920, 4750, 7800, 13300, 19500 }, { 3160, 5200, 8700, 14700, 21300 },
			{ 3420, 5700, 9700, 16200, 23200 }, { 3700, 6300, 10800, 17800, 25200 }, { 4000, 7000, 12000, 19500, 27400 }, { 4350, 7800, 13300, 21300, 29800 }, { 4750, 8700, 14700, 23200, 32400 }, { 5200, 9700, 16200, 25200, 35200 },
			{ 5700, 10800, 17800, 27400, 38200 }, { 6300, 12000, 19500, 29800, 41400 }, { 7000, 13300, 21300, 32400, 44800 }, { 7800, 14700, 23200, 35200, 48400 }, { 8700, 16200, 25200, 38200, 52200 }, { 9700, 17800, 27400, 41400, 56200 },
			{ 10800, 19500, 29800, 44800, 60400 }, { 12000, 21300, 32400, 48400, 64800 }, { 13300, 23200, 35200, 52200, 69400 }, { 14700, 25200, 38200, 56200, 74200 }, { 16200, 27400, 41400, 60400, 79200 }, { 17800, 29800, 44800, 64800, 84700 },
			{ 19500, 32400, 48400, 69400, 90700 }, { 21300, 35200, 52200, 74200, 97200 }, { 23200, 38200, 56200, 79200, 104200 }, { 25200, 41400, 60400, 84700, 111700 }, { 27400, 44800, 64800, 90700, 119700 }, { 29800, 48400, 69400, 97200, 128200 },
			{ 32400, 52200, 74200, 104200, 137200 }, { 35200, 56200, 79200, 111700, 146700 }, { 38200, 60400, 84700, 119700, 156700 }, { 41400, 64800, 90700, 128200, 167700 }, { 44800, 69400, 97200, 137200, 179700 },
			{ 48400, 74200, 104200, 146700, 192700 }, { 52200, 79200, 111700, 156700, 206700 }, { 56200, 84700, 119700, 167700, 221700 }, { 60400, 90700, 128200, 179700, 237700 }, { 64800, 97200, 137200, 192700, 254700 },
			{ 69400, 104200, 146700, 206700, 272700 }, { 74200, 111700, 156700, 221700, 291700 }, { 79200, 119700, 167700, 237700, 311700 }, { 84700, 128200, 179700, 254700, 333700 }, { 90700, 137200, 192700, 272700, 357700 },
			{ 97200, 146700, 206700, 291700, 383700 }, { 104200, 156700, 221700, 311700, 411700 }, { 111700, 167700, 237700, 333700, 441700 }, { 119700, 179700, 254700, 357700, 476700 }, { 128200, 192700, 272700, 383700, 516700 },
			{ 137200, 206700, 291700, 411700, 561700 }, { 146700, 221700, 311700, 441700, 611700 }, { 156700, 237700, 333700, 476700, 666700 }, { 167700, 254700, 357700, 516700, 726700 }, { 179700, 272700, 383700, 561700, 791700 },
			{ 192700, 291700, 411700, 611700, 861700 }, { 206700, 311700, 441700, 666700, 936700 }, { 221700, 333700, 476700, 726700, 1016700 }, { 237700, 357700, 516700, 791700, 1101700 }, { 254700, 383700, 561700, 861700, 1191700 },
			{ 272700, 411700, 611700, 936700, 1286700 }, { 291700, 441700, 666700, 1016700, 1386700 }, { 311700, 476700, 726700, 1101700, 1496700 }, { 333700, 516700, 791700, 1191700, 1616700 }, { 357700, 561700, 861700, 1286700, 1746700 },
			{ 383700, 611700, 936700, 1386700, 1886700 } };

	private static String[] romanNumeralsTo50 = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI", "XXVII", "XXVIII",
			"XXIX", "XXX", "XXXI", "XXXII", "XXXIII", "XXXIV", "XXXV", "XXXVI", "XXXVII", "XXXVIII", "XXXIX", "XL", "XLI", "XLII", "XLIII", "XLIV", "XLV", "XLVI", "XLVII", "XLVIII", "XLIX", "L" };

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

	public static boolean isPlayerInSkyblock() {
		boolean isInSkyblock = false;
		if (Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1) != null) {
			if ((StringUtils.stripControlCodes(Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName()).contains("SKYBLOCK"))) {
				isInSkyblock = true;
			}
		}
		return isInSkyblock;
	}

	public static Pet parseItemStackToPet(ItemStack hoveredItem) throws ParseException {
		Pet pet = new Pet();
		try {

			NBTTagCompound display = hoveredItem.getSubCompound("display", false);

			if (display.hasKey("Lore")) {
				NBTTagList lore = display.getTagList("Lore", Constants.NBT.TAG_STRING);
				String petType = lore.getStringTagAt(0).split(" ")[0];
				float currentProgress = 0.0f;
				float currentXp = 0.0f;
				String heldItemType = "";
				float heldItemPetXpBoost = 0.0f;

				for (int i = 0; i < lore.tagCount(); i++) {
					String currentLine = lore.getStringTagAt(i);

					Matcher matcher = PET_LEVEL_PROGRESS_PATTERN.matcher(currentLine);
					if (matcher.matches()) {
						currentProgress = Float.parseFloat(matcher.group(2));
					}
					matcher.reset();
					matcher = PET_CURRENT_XP_PATTERN.matcher(currentLine);
					if (matcher.matches()) {
						currentXp = Float.parseFloat(matcher.group(2).replace(",", ""));
					}
					matcher.reset();
					matcher = PET_HELD_ITEM.matcher(currentLine);
					if (matcher.matches()) {
						heldItemType = matcher.group(2);
						// §f|§a|§9|§5
						if (matcher.group(1).equals("§f")) {
							heldItemPetXpBoost = 0.20f;
						} else if (matcher.group(1).equals("§a")) {
							heldItemPetXpBoost = 0.30f;
						} else if (matcher.group(1).equals("§9")) {
							heldItemPetXpBoost = 0.40f;
						} else if (matcher.group(1).equals("§5")) {
							heldItemPetXpBoost = 0.50f;
						}
						if (heldItemType.equals("All")) {
							heldItemPetXpBoost = 0.10f;
						}

					}

				}
				String rarity = hoveredItem.getDisplayName().split(" ")[2].substring(0, 2);
				int petLevel = Integer.parseInt(hoveredItem.getDisplayName().split(" ")[1].replace("]", ""));
				int nextLevelXp = Helper.getXpToNextLevel(rarity, petLevel + 1);
				pet.setXpNeededForNextLevel(nextLevelXp);
				pet.setPetLevel(petLevel);

				pet.setPetType(StringUtils.stripControlCodes(petType));
				pet.setCurrentProgress(currentProgress);
				pet.setCurrentXp(currentXp);
				pet.setHeldItemType(heldItemType);
				pet.setHeldItemPetXpBoost(heldItemPetXpBoost);
			}

			pet.setDisplayName(hoveredItem.getDisplayName());
		} catch (Exception ex) {
			PetInfo.logger.info(ex.getLocalizedMessage());
			PetInfo.logger.error(ex.getLocalizedMessage());

		}
		return pet;
	}

	public static int getLevelFromRomanNumerals(String romanNumeral) {
		for (int i = 0; i < romanNumeralsTo50.length; i++) {
			if (romanNumeralsTo50[i].equals(romanNumeral)) {
				return i + 1;
			}
		}
		return 0;
	}

	public static float roundToNDecimals(float input, int decimalPlaces) {
		// rounding to one decimal point
		input = input * (float) Math.pow(10, decimalPlaces);
		input = Math.round(input);
		input = input / (float) Math.pow(10, decimalPlaces);

		return input;
	}

	public static int getXpToNextLevel(String rarity, int level) {
		int rarityIndex = 0;
		if (rarity.equals("§f")) {
			rarityIndex = 0;
		} else if (rarity.equals("§a")) {
			rarityIndex = 1;
		} else if (rarity.equals("§9")) {
			rarityIndex = 2;
		} else if (rarity.equals("§5")) {
			rarityIndex = 3;
		} else if (rarity.equals("§6")) {
			rarityIndex = 4;
		}

		return petLevelTable[level - 1][rarityIndex];

	}

}
