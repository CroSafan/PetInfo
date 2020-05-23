package com.crosafan.petinfo.gui;

import java.awt.Color;
import java.io.IOException;

import com.crosafan.petinfo.PetInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class Gui extends GuiScreen {

	int moveX = PetInfo.instance.guiLocation[0];
	int moveY = PetInfo.instance.guiLocation[1];
	boolean moving = true;
	long pressTime = 0;

	@Override
	public void initGui() {

		super.initGui();
	}

	public void onGuiClosed() {
		super.onGuiClosed();
		PetInfo.instance.saveConfig();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 125).getRGB());

		PetInfo.instance.renderListener.renderPet();
		GlStateManager.color(1, 1, 1, 0F);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// Left click 0, Right click 1, Middle click 2
		if (mouseButton == 0) {
			moving = true;
		}

	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		if (moving) {
			moveX = mouseX;
			moveY = mouseY;
			PetInfo.instance.guiLocation = new int[] { moveX, moveY };
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		moving = false;
	}

}
