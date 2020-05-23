package com.crosafan.petinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.crosafan.commands.PetInfoCommand;
import com.crosafan.petinfo.listeners.PlayerListener;
import com.crosafan.petinfo.listeners.RenderListener;

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
	public static String currentPetDisplayText = "No pet selected!";

	@Instance
	public static PetInfo instance;

	public PlayerListener playerListener;
	public RenderListener renderListener;

	private static File configFile;

	@Mod.EventHandler

	public static void PreInit(FMLPreInitializationEvent event) {
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
			currentPetDisplayText = prop.getProperty("petinfo.currentSelectedPet", "No pet selected!");
			guiLocation[0] = Integer.valueOf(prop.getProperty("petinfo.guixpos", "5"));
			guiLocation[1] = Integer.valueOf(prop.getProperty("petinfo.guiypos", "5"));

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static void saveConfig() {
		try (OutputStream output = new FileOutputStream(CONFIG_PATH)) {

			Properties prop = new Properties();

			prop.setProperty("petinfo.currentSelectedPet", currentPetDisplayText);
			prop.setProperty("petinfo.guixpos", String.valueOf(guiLocation[0]));
			prop.setProperty("petinfo.guiypos", String.valueOf(guiLocation[1]));

			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		}
	}

}
