package dev.toxicaven;

import net.fabricmc.api.ModInitializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Goosemodder implements ModInitializer {

	@Override
	public void onInitialize() {
		File Stable, PTB, Canary, Development;
		String OS = System.getProperty("os.name").toLowerCase();

		if (OS.contains("win")) { //Windows
			Stable = new File(System.getenv("APPDATA") + File.separator + "discord" + File.separator + "settings.json");
			PTB = new File(System.getenv("APPDATA") + File.separator + "discordptb" + File.separator + "settings.json");
			Canary = new File(System.getenv("APPDATA") + File.separator + "discordcanary" + File.separator + "settings.json");
			Development = new File(System.getenv("APPDATA") + File.separator + "discorddevelopment" + File.separator + "settings.json");
		} else if (OS.contains("nux") || OS.contains("nix")) { //Linux-based
			Stable = new File(System.getProperty("user.home") + "/.config/discord/settings.json");
			PTB = new File(System.getProperty("user.home") + "/.config/discordptb/settings.json");
			Canary = new File(System.getProperty("user.home") + "/.config/discordcanary/settings.json");
			Development = new File(System.getProperty("user.home") + "/.config/discorddevelopment/settings.json");
		} else { //The bad one
			Stable = new File(System.getProperty("user.home") + "/Library/Application Support/discord/settings.json");
			PTB = new File(System.getProperty("user.home") + "/Library/Application Support/discordptb/settings.json");
			Canary = new File(System.getProperty("user.home") + "/Library/Application Support/discordcanary/settings.json");
			Development = new File(System.getProperty("user.home") + "/Library/Application Support/discorddevelopment/settings.json");
		}

		attemptInstall("Stable", Stable);
		attemptInstall("PTB", PTB);
		attemptInstall("Canary", Canary);
		attemptInstall("Development", Development);
	}

	@SuppressWarnings("unchecked")
	private static void attemptInstall(String flavorName, File settingsFile) {

		//Assume the discord flavor does not exist if the settings.json cannot be found
		if (!settingsFile.isFile()) {
			System.out.println("skipped " + flavorName + ", file not found");
			return;
		}

		try {
			Object oldParser = new JSONParser().parse(new FileReader(settingsFile));
			JSONObject inputObject = (JSONObject) oldParser;
			JSONObject buildOutput = new JSONObject();

			//Check if UPDATE_ENDPOINT exists, and assume goose is installed if it exists
			String gooseCheck = (String) inputObject.get("UPDATE_ENDPOINT");
			if (gooseCheck.contains("goosemod")) {
				System.out.println("skipped Discord " + flavorName + ", goose already installed");
				return;
			}

			//Readd old settings details
			Boolean isMinimized = (Boolean) inputObject.get("IS_MINIMIZED");
			JSONObject windowBounds = (JSONObject) inputObject.get("WINDOW_BOUNDS");
			Boolean isMaximized = (Boolean) inputObject.get("IS_MAXIMIZED");
			String backgroundColor = (String) inputObject.get("BACKGROUND_COLOR");
			buildOutput.put("IS_MINIMIZED", isMinimized);
			buildOutput.put("WINDOW_BOUNDS", windowBounds);
			buildOutput.put("IS_MAXIMIZED", isMaximized);
			buildOutput.put("BACKGROUND_COLOR", backgroundColor);

			//Install Goomse!
			buildOutput.put("UPDATE_ENDPOINT", "https://updates.goosemod.com/goosemod");
			buildOutput.put("NEW_UPDATE_ENDPOINT", "https://updates.goosemod.com/goosemod/");

			//Delete the old file, I had some stuff overwrite wrong once
			if (settingsFile.isFile()) settingsFile.delete();

			//Write new, modded settings.json
			FileWriter file = new FileWriter(settingsFile);
			file.write(buildOutput.toJSONString());
			file.flush();
			file.close();
			System.out.println("installed goosemod for Discord " + flavorName + "!");
		} catch (Exception e) {
			System.out.println(e); //Really this should never happen, but ok ig
		}
	}
}
