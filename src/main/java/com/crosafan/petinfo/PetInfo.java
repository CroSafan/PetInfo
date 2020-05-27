package com.crosafan.petinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.apache.logging.log4j.Logger;

import com.crosafan.petinfo.commands.PetInfoCommand;
import com.crosafan.petinfo.helpers.Helper;
import com.crosafan.petinfo.helpers.Pet;
import com.crosafan.petinfo.listeners.PlayerListener;
import com.crosafan.petinfo.listeners.RenderListener;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = PetInfo.MODID, version = PetInfo.VERSION, name = PetInfo.MOD_NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")

public class PetInfo {
	public static final String MOD_NAME = "Pet info";
	public static final String MODID = "petinfo";
	public static final String VERSION = "1.0";
	private static final String CONFIG_PATH = "petinfo.properties";

	public static boolean openGui = false;

	public static int[] guiLocation = new int[] { 5, 5 };
	public static Pet currentPet = new Pet();
	public static boolean isInSkyblock = false;
	public static String currentSkill = "";
	public static float currentXp = 0.0f;
	public static float gainedXp = 0.0f;
	public static int tamingLevel = 0;

	public static PetInfo instance;

	public PlayerListener playerListener;
	public RenderListener renderListener;

	private static File configFile;
	public static Logger logger;

	@Mod.EventHandler

	public static void PreInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		loadConfig();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		instance = this;

		ClientCommandHandler.instance.registerCommand(new PetInfoCommand(this));

		playerListener = new PlayerListener(this);
		renderListener = new RenderListener(this);

		MinecraftForge.EVENT_BUS.register(playerListener);
		MinecraftForge.EVENT_BUS.register(renderListener);
		
		

	}

	@Mod.EventHandler

	public static void Postinit(FMLPostInitializationEvent event) {

	}

	public static void loadConfig() {
		File file = new File(CONFIG_PATH); // here you make a filehandler - not a filesystem file.

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (InputStream input = new FileInputStream(CONFIG_PATH)) {

			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			guiLocation[0] = Integer.valueOf(prop.getProperty("petinfo.guixpos", "5"));
			guiLocation[1] = Integer.valueOf(prop.getProperty("petinfo.guiypos", "5"));
			tamingLevel = Integer.valueOf(prop.getProperty("petinfo.taminglevel", "1"));
			currentPet.setDisplayName(prop.getProperty("petinfo.currentSelectedPet", "No pet selected!"));
			currentPet.setHeldItemType(prop.getProperty("petinfo.helditemtype", ""));
			currentPet.setHeldItemPetXpBoost(Float.parseFloat(prop.getProperty("petinfo.helditempetxpboost", "0.0")));
			currentPet.setPetType(prop.getProperty("petinfo.pettype", ""));
			currentPet.setCurrentProgress(Float.parseFloat(prop.getProperty("petinfo.currentprogress", "0.0")));
			currentPet.setCurrentXp(Float.parseFloat(prop.getProperty("petinfo.petcurrentxp", "0.0")));
			currentPet.setPetLevel(Integer.parseInt(prop.getProperty("petinfo.petlevel", "1")));
			currentPet.setXpNeededForNextLevel(Integer.parseInt(prop.getProperty("petinfo.xpNeededForNextLevel", "0")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static void saveConfig() {
		try (OutputStream output = new FileOutputStream(CONFIG_PATH)) {

			Properties prop = new Properties();

			prop.setProperty("petinfo.guixpos", String.valueOf(guiLocation[0]));
			prop.setProperty("petinfo.guiypos", String.valueOf(guiLocation[1]));
			prop.setProperty("petinfo.taminglevel", String.valueOf(tamingLevel));
			prop.setProperty("petinfo.currentSelectedPet", currentPet.getDisplayName());
			prop.setProperty("petinfo.helditemtype", currentPet.getHeldItemType());
			prop.setProperty("petinfo.helditempetxpboost", String.valueOf(currentPet.getHeldItemPetXpBoost()));
			prop.setProperty("petinfo.pettype", currentPet.getPetType());
			prop.setProperty("petinfo.currentprogress", String.valueOf(currentPet.getCurrentProgress()));
			prop.setProperty("petinfo.petcurrentxp", String.valueOf(currentPet.getCurrentXp()));
			prop.setProperty("petinfo.petlevel", String.valueOf(currentPet.getPetLevel()));
			prop.setProperty("petinfo.xpNeededForNextLevel", String.valueOf(currentPet.getXpNeededForNextLevel()));
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		}
	}

}
