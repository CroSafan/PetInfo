package com.crosafan.petinfo.listeners;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crosafan.petinfo.PetInfo;
import com.crosafan.petinfo.helpers.Helper;
import com.crosafan.petinfo.helpers.Pet;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerListener {

	private static final Pattern XP_GAIN_AND_SKILL_PATTERN = Pattern.compile("§\\d\\+(\\d*\\.?\\d*) (Farming|Mining|Combat|Foraging|Fishing|Enchanting|Alchemy) (\\(([0-9.,]+)/([0-9.,]+)\\))");
	private static final Pattern PET_NAME_PATTERN = Pattern.compile("§\\d\\[Lvl \\d+\\] §\\d.+");
	private PetInfo petInfo;

	private Pet tempPet;

	private int tick = 1;

	public PlayerListener(PetInfo petInfo) {
		this.petInfo = petInfo;
	}

	@SubscribeEvent()
	public void onItemTooltip(ItemTooltipEvent e) {
		if (petInfo.isInSkyblock) {
			if (Helper.isPetMenuOpen()) {
				ItemStack hoveredItem = e.itemStack;
				System.out.println(hoveredItem.getDisplayName());
				Matcher matcher = PET_NAME_PATTERN.matcher(hoveredItem.getDisplayName());
				if (matcher.matches()) {
					System.out.println("Here ");
					try {
						tempPet = Helper.parseItemStackToPet(hoveredItem);
					} catch (ParseException e1) {
						petInfo.logger.info(e1.getMessage());
					}
				}
			}
		}

	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChatReceive(ClientChatReceivedEvent e) {

		String message = e.message.getUnformattedText();
		if (message.contains("You summoned your")) {
			if (tempPet != null) {
				petInfo.currentPet = tempPet;
			}
			petInfo.saveConfig();

		}

		if (message.contains("levelled up to level") && !petInfo.currentPet.getDisplayName().equals("No pet selected!")) {
			String level = message.replaceAll("\\D", "");
			String displayName = petInfo.currentPet.getDisplayName().replaceAll("\\[Lvl \\d*\\]", "[Lvl " + level + "]");
			petInfo.currentPet.setDisplayName(displayName);
			petInfo.currentXp = 0;
			// int currentLevelXp= Helper.getXpToNextLevel(petInfo.currentPet.getDisplayName().split(" ")[2].substring(0, 2), petInfo.currentPet.getPetLevel());
			// petInfo.currentPet.setCurrentXp(petInfo.currentPet.getCurrentXp()-currentLevelXp);
			petInfo.currentPet.setCurrentXp(0);
			petInfo.currentPet.setPetLevel(Integer.parseInt(level));
			int nextLevelXp = Helper.getXpToNextLevel(petInfo.currentPet.getDisplayName().split(" ")[2].substring(0, 2), petInfo.currentPet.getPetLevel() + 1);
			petInfo.currentPet.setXpNeededForNextLevel(nextLevelXp);
			petInfo.saveConfig();
		}

		if (message.contains("You despawned your")) {
			petInfo.currentPet.setDisplayName("No pet selected!");
			petInfo.saveConfig();
		}
		// 2 : 'Status' message, displayed above action bar, where song notifications are.
		if (e.type == 2 && petInfo.currentPet.getPetLevel() < 100) {
			// §c1755/1755? §3+5.4 Farming (1,146,767.2/1,200,000) §b354/354? Mana§r
			String[] infoGroups = message.split("     ");
			// §3+5.4 Farming (1,146,335.2/1,200,000)
			if (infoGroups != null && infoGroups.length > 1) {
				Matcher matcher = XP_GAIN_AND_SKILL_PATTERN.matcher(infoGroups[1]);
				if (matcher.matches()) {

					String previousSkill = petInfo.currentSkill;
					petInfo.currentSkill = matcher.group(2);
					// don't have current xp loaded or we are switching to a different skill
					if (petInfo.currentXp == 0 || !petInfo.currentSkill.equals(previousSkill)) {
						// for the first time, when we don't have the current xp loaded
						petInfo.gainedXp = Float.parseFloat(matcher.group(1));
						petInfo.currentXp = Float.parseFloat(matcher.group(4).replace(",", ""));

					} else {
						// prevents adding the same xp gain multiple time
						if (petInfo.currentXp == Double.parseDouble(matcher.group(4).replace(",", ""))) {
							petInfo.gainedXp = 0.0f;

						} else {

							// previous saved current xp - current xp
							petInfo.gainedXp = Float.parseFloat(matcher.group(4).replace(",", "")) - petInfo.currentXp;
							// setting currentXp for the next iteration
							petInfo.currentXp = Float.parseFloat(matcher.group(4).replace(",", ""));

						}

					}
					float xpGain = calculateXpGain();
					float currentXp = petInfo.currentPet.getCurrentXp() + xpGain;
					currentXp = Helper.roundToNDecimals(currentXp, 1);
					petInfo.currentPet.setCurrentXp(currentXp);
					float progress = (petInfo.currentPet.getCurrentXp() / petInfo.currentPet.getXpNeededForNextLevel()) * 100.0f;
					progress = Helper.roundToNDecimals(progress, 1);
					petInfo.currentPet.setCurrentProgress(progress);

				}
			}

		}

	}

	private float calculateXpGain() {
		// taming xp
		float tamingXpBonus = 1.0f + (float) (petInfo.tamingLevel / 100.0f);
		float gainWithPercent = petInfo.gainedXp * tamingXpBonus;
		// held item boosts give xp boost based on the rarity, except for the All boost
		if (petInfo.currentPet.getHeldItemType().equals(petInfo.currentSkill) || petInfo.currentPet.getHeldItemType().equals("All")) {
			gainWithPercent = gainWithPercent * (1.0f + petInfo.currentPet.getHeldItemPetXpBoost());
		}
		// Non alchemy pets gain alchemy xp at a 1/12 rate
		if (!petInfo.currentSkill.equals(petInfo.currentPet.getPetType())) {
			if (!petInfo.currentPet.getPetType().equals("Alchemy") && petInfo.currentSkill.equals("Alchemy")) {
				gainWithPercent = gainWithPercent * 0.08f;
			} else {
				// skill xp from a different skill type from the pet gain it a 1/3 rate
				gainWithPercent = gainWithPercent * 0.33f;

			}
		}
		// Mining and Fishing gain 50% more pet xp
		if (petInfo.currentSkill.equals("Mining") || petInfo.currentSkill.equals("Fishing")) {
			gainWithPercent = gainWithPercent + (gainWithPercent * 0.5f);
		}

		return gainWithPercent;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTick(TickEvent.ClientTickEvent e) {
		if (e.phase == TickEvent.Phase.START) {
			tick++;
			// check every second
			if (tick >= 20 && Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null) {

				petInfo.isInSkyblock = Helper.isPlayerInSkyblock();

				if (petInfo.isInSkyblock) {
					if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest) {

						ContainerChest chest = (ContainerChest) Minecraft.getMinecraft().thePlayer.openContainer;

						IInventory inv = chest.getLowerChestInventory();
						if (inv.getName().contains("Your Skills")) {
							ItemStack tamingSkill = inv.getStackInSlot(33);
							petInfo.tamingLevel = Helper.getLevelFromRomanNumerals(tamingSkill.getDisplayName().split(" ")[1]);
						}
					}
				}

				tick = 1;
			}

		}

	}

}
