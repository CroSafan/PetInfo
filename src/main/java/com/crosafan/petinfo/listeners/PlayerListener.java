package com.crosafan.petinfo.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crosafan.petinfo.PetInfo;
import com.crosafan.petinfo.helpers.Helper;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PlayerListener {

	private PetInfo petInfo;

	private ItemStack tempPet;

	private int tick = 1;

	public PlayerListener(PetInfo petInfo) {
		this.petInfo = petInfo;
	}

	@SubscribeEvent()
	public void onItemTooltip(ItemTooltipEvent e) {
		if (petInfo.isInSkyblock) {
			if (Helper.isPetMenuOpen()) {
				ItemStack hoveredItem = e.itemStack;
				if (!Helper.ignoreTooltip(hoveredItem.getDisplayName())) {
					tempPet = hoveredItem;
				}
			}
		}

	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChatReceive(ClientChatReceivedEvent e) {

		String message = e.message.getUnformattedText();
		if (message.contains("You summoned your")) {
			petInfo.currentPetDisplayText = tempPet.getDisplayName();
			petInfo.saveConfig();

		}

		if (message.contains("levelled up to level") && !petInfo.currentPetDisplayText.equals("No pet selected!")) {
			String level = message.replaceAll("\\D", "");
			petInfo.currentPetDisplayText = petInfo.currentPetDisplayText.replaceAll("\\[Lvl \\d*\\]", "[Lvl " + level + "]");
			petInfo.saveConfig();
		}
		
		if(message.contains("You despawned your")) {
			petInfo.currentPetDisplayText="No pet selected!";
			petInfo.saveConfig();
		}

	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTick(TickEvent.ClientTickEvent e) {
		if (e.phase == TickEvent.Phase.START) {
			tick++;
			// check every second
			if (tick >= 20 && Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null) {

				petInfo.isInSkyblock = Helper.isPlayerInSkyblock();

				tick = 1;
			}

		}

	}

}
