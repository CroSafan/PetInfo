package com.crosafan.petinfo.listeners;

import com.crosafan.petinfo.PetInfo;
import com.crosafan.petinfo.gui.Gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderListener {
	public FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;

	int textColor = 0xFFFFFF;
	int commonColor = 0xFFFFFF;
	int uncommonColor = 0x3cbd3c;
	int rareColor = 0x5555FF;
	int epicColor = 0xAA00AA;
	int legendaryColor = 0xFFAA00;
	int redColor = 0xFF5555;

	PetInfo petInfo;

	public RenderListener(PetInfo petInfo) {
		this.petInfo = petInfo;
	}

	@SubscribeEvent
	public void renderGameOverlayEvent(RenderGameOverlayEvent.Post event) {

		if ((event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE || event.type == RenderGameOverlayEvent.ElementType.JUMPBAR)) {
			if (petInfo.isInSkyblock) {

				if (petInfo.tamingLevel == 0) {
					renderTamingWarning();
				}

				else if (petInfo.currentPet != null) {
					renderPet();
				}
			}
		}
	}

	@SubscribeEvent
	public void onAttemptedRender(TickEvent.RenderTickEvent e) {
		if (petInfo.openGui) {
			Minecraft.getMinecraft().displayGuiScreen(new Gui());
		}

		petInfo.openGui = false;
	}

	public void renderPet() {
		try {
			renderer.drawString(petInfo.currentPet.getDisplayName() + " §f" + String.valueOf(petInfo.currentPet.getCurrentProgress()) + "%", petInfo.guiLocation[0], petInfo.guiLocation[1], textColor, false);
		} catch (NullPointerException npe) {
			petInfo.logger.error(npe.getLocalizedMessage());

		}

	}

	public void renderTamingWarning() {
		String tamingNotDetected = "TAMING LEVEL NOT DETECTED!";

		int width = renderer.getStringWidth(tamingNotDetected);
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int scaledWidth = sr.getScaledWidth();
		int scaledHeight = sr.getScaledHeight();
		int stringWidth = renderer.getStringWidth(tamingNotDetected);
		GlStateManager.pushMatrix();
		// centering
		GlStateManager.translate((float) (scaledWidth / 2), (float) (scaledHeight / 2), 0.0F);
		GlStateManager.pushMatrix();
		GlStateManager.scale(2.5F, 2.5F, 2.5F);

		renderer.drawString(tamingNotDetected, (-stringWidth / 2), 0, redColor);

		GlStateManager.popMatrix();// for translate
		GlStateManager.popMatrix();// for scale

	}

}
