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
	private static final Pattern PET_NAME_PATTERN = Pattern.compile("§\\d\\[Lvl \\d+\\] §.+");
	private static final Pattern TAMING_SKILL_LEVEL_UP_PATTERN = Pattern.compile("\\s*§b§lSKILL LEVEL UP §3Taming (.+(§3(.+)))");

	private PetInfo petInfo;

	private Pet tempPet;

	private int tick = 1;

	public PlayerListener(PetInfo petInfo) {
		this.petInfo = petInfo;
	}

	@SubscribeEvent()
	public void onItemTooltip(ItemTooltipEvent e) {
		if (!petInfo.isInSkyblock || !Helper.isPetMenuOpen()) {
			return;
		}

		ItemStack hoveredItem = e.itemStack;
		Matcher matcher = PET_NAME_PATTERN.matcher(hoveredItem.getDisplayName());
		if (matcher.matches()) {
			try {
				tempPet = new Pet(hoveredItem);
			} catch (Exception e1) {
				petInfo.logger.error(e1.getMessage());
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

		} else if (message.contains("levelled up to level") && !petInfo.currentPet.getDisplayName().equals("No pet selected!")) {
			petInfo.currentPet.levelUp(message);
			petInfo.saveConfig();
		} else if (message.contains("You despawned your")) {
			petInfo.currentPet.setDisplayName("No pet selected!");
			petInfo.currentPet.setCurrentProgress(0.0f);
			petInfo.saveConfig();
		} else if (message.contains("Switching to profile")) {
			petInfo.currentPet.setDisplayName("No pet selected!");
			petInfo.currentPet.setCurrentProgress(0.0f);
			petInfo.tamingLevel = 0;
			petInfo.saveConfig();
		}

		else if (message.contains("SKILL LEVEL UP")) {
			Matcher matcher = TAMING_SKILL_LEVEL_UP_PATTERN.matcher(message);
			if (!matcher.matches()) {
				return;
			}
			petInfo.tamingLevel = Integer.parseInt(matcher.group(3));
			petInfo.saveConfig();
		}
		// 2 : 'Status' message, displayed above action bar, where song notifications are.
		else if (e.type == 2 && petInfo.currentPet.getPetLevel() < 100) {

			if (petInfo.currentPet.getDisplayName().equals("No pet selected!")) {
				return;
			}

			// §c1755/1755? §3+5.4 Farming (1,146,767.2/1,200,000) §b354/354? Mana§r
			String[] infoGroups = message.split("     ");
			// §3+5.4 Farming (1,146,335.2/1,200,000)
			if (infoGroups == null || infoGroups.length <= 1) {
				return;
			}
			Matcher matcher = XP_GAIN_AND_SKILL_PATTERN.matcher(infoGroups[1]);
			if (!matcher.matches()) {
				return;
			}

			String previousSkill = petInfo.currentSkill;
			petInfo.currentSkill = matcher.group(2);
			Float currentXp = Float.parseFloat(matcher.group(4).replace(",", ""));
			// don't have current xp loaded or we are switching to a different skill
			if (petInfo.currentXp == 0 || !petInfo.currentSkill.equals(previousSkill)) {
				// for the first time, when we don't have the current xp loaded
				petInfo.gainedXp = Float.parseFloat(matcher.group(1));
				petInfo.currentXp = currentXp;

			} else {
				// prevents adding the same xp gain multiple time
				if (petInfo.currentXp == currentXp) {
					petInfo.gainedXp = 0.0f;

				} else {

					// current xp - previous current xp
					petInfo.gainedXp = currentXp - petInfo.currentXp;
					// setting currentXp for the next iteration
					petInfo.currentXp = currentXp;

				}

			}
			float xpGain = calculateXpGain();
			float newXp = petInfo.currentPet.getCurrentXp() + xpGain;
			petInfo.currentPet.setCurrentXp(newXp);
			float progress = (petInfo.currentPet.getCurrentXp() / petInfo.currentPet.getXpNeededForNextLevel()) * 100.0f;

			petInfo.currentPet.setCurrentProgress(progress);

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
							// 32 index of taming skill slot
							ItemStack tamingSkill = null;
							for (int i = 0; i < inv.getSizeInventory(); i++) {
								if (inv.getStackInSlot(i).getDisplayName().contains("Taming")) {
									tamingSkill = inv.getStackInSlot(i);
									break;
								}
							}

							String[] tamingSplitted = tamingSkill.getDisplayName().trim().split(" ");
							if (tamingSplitted.length <= 1) {
								petInfo.tamingLevel = 1;
							} else {
								petInfo.tamingLevel = Helper.getLevelFromRomanNumerals(tamingSplitted[1]);
							}
							petInfo.saveConfig();
						}
					}
				}

				tick = 1;
			}

		}

	}

}
