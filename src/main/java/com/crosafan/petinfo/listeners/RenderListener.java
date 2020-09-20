package com.crosafan.petinfo.listeners;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.crosafan.petinfo.PetInfo;
import com.crosafan.petinfo.gui.Gui;
import com.crosafan.petinfo.helpers.Helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderListener {
	public FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;

	private final static ResourceLocation PROGRESS_BAR = new ResourceLocation("petinfo", "elements/progressbar.png");
	private final static ResourceLocation RABBIT = new ResourceLocation("petinfo", "peticons/Rabbit.png");
	private static final double TWICE_PI = Math.PI * 2;

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

	int heightOld = 0;
	int widthOld = 0;

	public void renderPet() {
		try {
			int x = petInfo.guiLocation[0];
			int y = petInfo.guiLocation[1];

			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

			int scaledWidth = sr.getScaledWidth();
			int scaledHeight = sr.getScaledHeight();
			
			int drawX=x;
			int drawY=y;
			
			if(widthOld!=0) {
				drawX = x * scaledWidth / widthOld;
			}
			if(heightOld!=0)
			drawY = y * scaledHeight / heightOld;

			if (petInfo.currentPet.getDisplayName().equals("No pet selected!")) {
				renderer.drawString(PetInfo.currentPet.getDisplayName(), x, y, textColor, true);
				return;
			}
			String[] splittedName = petInfo.currentPet.getDisplayName().split(" ");
			String onlyPetName = splittedName[2];
			if (splittedName.length > 3) {
				onlyPetName += " " + splittedName[3];
			}
			int rarityColor = Helper.getRarityColorFromString(onlyPetName);
			float progress = Helper.roundToNDecimals(petInfo.currentPet.getCurrentProgress(), 1);

			if (petInfo.displayIcon) {
				PetIcon petIcon = PetIcon.getByDisplayname(StringUtils.stripControlCodes(onlyPetName));

				drawCircularProgressBar(drawX + 16, drawY + 16, 16, (petInfo.currentPet.getCurrentProgress() / 100), rarityColor);

				try {
					Minecraft.getMinecraft().getTextureManager().bindTexture(petIcon.location);

				} catch (Exception ex) {
					Minecraft.getMinecraft().getTextureManager().bindTexture(PetIcon.UKNOWN.location);

				}
				Gui.drawModalRectWithCustomSizedTexture(drawX, drawY, 0, 0, 32, 32, 32, 32);
				renderer.drawString("Lvl [" + petInfo.currentPet.getPetLevel() + "]", drawX, drawY + 36, rarityColor, true);

			} else {
				renderer.drawString(petInfo.currentPet.getDisplayName() + " §f" + String.valueOf(progress) + "%", petInfo.guiLocation[0], petInfo.guiLocation[1], textColor, true);

			}
			
			 heightOld = scaledHeight;
			 widthOld = scaledWidth;

		} catch (NullPointerException npe) {
			petInfo.logger.error(npe.getLocalizedMessage());

		}
		
		

	}

	private void drawCircularProgressBar(int x, int y, int radius, float progress, int rarityColor) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(5.0F);
		drawSmoothCircle(x, y, radius, 180, progress, 0.0D);
		GL11.glLineWidth(1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
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

	public enum PetIcon {
		BABY_YETI("Baby Yeti", "Baby_Yeti"), BAT("Bat", "Bat"), BEE("Bee", "Bee"), BLACK_CAT("Black Cat", "Black_Cat"), BLAZE("Blaze", "Blaze"), BLUE_WHALE("Blue Whale", "Blue_Whale"), CHICKEN("Chicken", "Chicken"), DOLPHIN("Dolphin", "Dolphin"),
		ELEPHANT("Elephant", "Elephant"), ENDER_DRAGON("Ender Dragon", "Ender_Dragon"), ENDERMAN("Enderman", "Enderman"), ENDERMITE("Endermite", "Endermite"), FLYING_FISH("Flying Fish", "Flying_Fish"), GHOUL("Ghoul", "Ghoul"),
		GIRAFFE("Giraffe", "Giraffe"), GOLEM("Golem", "Golem"), GUARDIAN("Guardian", "Guardian"), HORSE("Horse", "Horse"), HOUND("Hound", "Hound"), JELLYFISH("Jellyfish", "Jellyfish"), JERRY("Jerry", "Jerry"), LION("Lion", "Lion"),
		MAGMA_CUBE("Magma Cube", "Magma_Cube"), MONKEY("Monkey", "Monkey"), OCELOT("Ocelot", "Ocelot"), PARROT("Parrot", "Parrot"), PHOENIX("Phoenix", "Phoenix"), PIG("Pig", "Pig"), PIGMAN("Pigman", "Pigman"), RABBIT("Rabbit", "Rabbit"),
		ROCK("Rock", "Rock"), SHEEP("Sheep", "Sheep"), SILVERFISH("Silverfish", "Silverfish"), SKELETON("Skeleton", "Skeleton"), SKELETON_HORSE("Skeleton Horse", "Skeleton_horse"), SNOWMAN("Snowman", "Snowman"), SPIDER("Spider", "Spider"),
		SQUID("Squid", "Squid"), TARANTULA("Tarantula", "Tarantula"), TIGER("Tiger", "Tiger"), TURTLE("Turtle", "Turtle"), WITHER_SKELETON("Wither Skeleton", "Wither_Skeleton"), WOLF("Wolf", "Wolf"), ZOMBIE("Zombie", "Zombie"),
		UKNOWN("uknown", "uknown");

		private String name = "";
		private ResourceLocation location;

		private PetIcon(String name, String path) {
			this.name = name;
			this.location = new ResourceLocation("petinfo", "peticons/" + path + ".png");
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ResourceLocation getLocation() {
			return location;
		}

		public void setLocation(ResourceLocation location) {
			this.location = location;
		}

		public static PetIcon getByDisplayname(String name) {
			for (PetIcon petIcon : values()) {
				if (name.equals(petIcon.getName())) {
					return petIcon;
				}
			}
			return PetIcon.UKNOWN;
		}

	}

	// https://github.com/Meldexun/BetterDiving/blob/885332e4f24baa48b7d10cf5626ef81b40e2ec9e/src/main/java/meldexun/better_diving/client/gui/GuiOxygen.java
	public static void drawSmoothCircle(double x, double y, double radius, int sides, double percent, double startAngle) {
		boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
		boolean lineSmooth = GL11.glGetBoolean(GL11.GL_LINE_SMOOTH);

		double rad;
		double sin;
		double cos;

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glBegin(GL11.GL_LINE_STRIP);

		for (int i = 0; i < percent * (double) sides; i++) {
			rad = TWICE_PI * ((double) i / (double) sides + startAngle);
			sin = Math.sin(rad);
			cos = -Math.cos(rad);

			GL11.glVertex2d(x + sin * radius, y + cos * radius);
		}

		rad = TWICE_PI * (percent + startAngle);
		sin = Math.sin(rad);
		cos = -Math.cos(rad);

		GL11.glVertex2d(x + sin * radius, y + cos * radius);

		GL11.glEnd();
		if (!lineSmooth) {
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
		}
		if (!blend) {
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glPopMatrix();
	}

}
