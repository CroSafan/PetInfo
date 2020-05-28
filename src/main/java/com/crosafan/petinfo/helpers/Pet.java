package com.crosafan.petinfo.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crosafan.petinfo.PetInfo;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;

public class Pet {

	private String displayName = "";
	private String petType = "";
	private float currentXp = 0.0f;
	private float currentProgress = 0.0f;
	private String heldItemType = "";
	private float heldItemPetXpBoost = 0.0f;
	private int petLevel = 1;
	private int xpNeededForNextLevel = 0;

	public Pet() {
		this.displayName = "No pet selected!";
	}

	public Pet(ItemStack hoveredItem) {

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
				if (currentProgress == 0.0f) {
					currentProgress = Helper.matchCurrentProgresForPet(currentLine);
				}
				if (currentXp == 0.0f) {
					currentXp = Helper.matchCurrentXpForPet(currentLine);
				}
				if (heldItemType.isEmpty()) {
					heldItemType = Helper.matchHeldItemTypeForPet(currentLine);
				}
				if (heldItemPetXpBoost == 0.0f) {
					heldItemPetXpBoost = Helper.matchHeldItemPetXpBoosForPet(currentLine);
				}

			}
			// §7[Lvl 74] §6Rabbit
			String rarity = hoveredItem.getDisplayName().split(" ")[2].substring(0, 2);
			int petLevel = Integer.parseInt(hoveredItem.getDisplayName().split(" ")[1].replace("]", ""));
			int nextLevelXp = Helper.getXpToNextLevel(rarity, petLevel + 1);
			this.xpNeededForNextLevel = nextLevelXp;
			this.petLevel = petLevel;
			this.petType = StringUtils.stripControlCodes(petType);
			this.currentProgress = currentProgress;
			this.currentXp = currentXp;
			this.heldItemType = heldItemType;
			this.heldItemPetXpBoost = heldItemPetXpBoost;

		}

		this.displayName = hoveredItem.getDisplayName();

	}

	public int getXpNeededForNextLevel() {
		return xpNeededForNextLevel;
	}

	public void setXpNeededForNextLevel(int xpNeededForNextLevel) {
		this.xpNeededForNextLevel = xpNeededForNextLevel;
	}

	public int getPetLevel() {
		return petLevel;
	}

	public void setPetLevel(int petLevel) {
		this.petLevel = petLevel;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPetType() {
		return petType;
	}

	public void setPetType(String petType) {
		this.petType = petType;
	}

	public float getCurrentXp() {

		return currentXp;
	}

	public void setCurrentXp(float currentXp) {

		this.currentXp = currentXp;
	}

	public float getCurrentProgress() {
		return currentProgress;
	}

	public void setCurrentProgress(float currentProgress) {
		this.currentProgress = Helper.roundToNDecimals(currentProgress, 1);
	}

	public String getHeldItemType() {
		return heldItemType;
	}

	public void setHeldItemType(String heldItemType) {
		this.heldItemType = heldItemType;
	}

	public float getHeldItemPetXpBoost() {
		return heldItemPetXpBoost;
	}

	public void setHeldItemPetXpBoost(float heldItemPetXpBoost) {
		this.heldItemPetXpBoost = heldItemPetXpBoost;
	}

	public void levelUp(String message) {
		int level = Integer.parseInt(message.replaceAll("\\D", ""));
		this.petLevel = level;
		this.displayName = this.displayName.replaceAll("\\[Lvl \\d*\\]", "[Lvl " + level + "]");
		this.currentXp = 0.0f;
		this.xpNeededForNextLevel = Helper.getXpToNextLevel(this.displayName.split(" ")[2].substring(0, 2), this.petLevel + 1);

	}

}
