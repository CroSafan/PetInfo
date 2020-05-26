package com.crosafan.petinfo.helpers;

public class Pet {

	public Pet() {
		this.displayName = "No pet selected!";
	}

	private String displayName = "";
	private String petType = "";
	private float currentXp = 0.0f;
	private float currentProgress = 0.0f;
	private String heldItemType = "";
	private float heldItemPetXpBoost = 0.0f;
	private int petLevel = 1;
	private int xpNeededForNextLevel=0;

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

		this.currentXp = Helper.roundToNDecimals(currentXp, 1);
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

}
