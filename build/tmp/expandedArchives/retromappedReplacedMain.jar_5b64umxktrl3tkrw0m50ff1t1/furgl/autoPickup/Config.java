package furgl.autoPickup;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config 
{
	public static Configuration config;
	public static ArrayList<String> blacklistNames;
	public static boolean autoAdd = true;
	/**Resets every session*/
	public static boolean firstAutoAdd = true;

	public static void init(final File file)
	{
		Config.config = new Configuration(file);
		Config.config.load();
		Config.config.save();
	}

	/**Update blacklistNames and blacklistItems from config file*/
	public static void syncFromConfig(String playerName) 
	{
		Property autoAddProp = Config.config.get(playerName, "AutoAdd", true);
		autoAdd = autoAddProp.getBoolean();
		Property itemsProp = Config.config.get(playerName, "Blacklisted Items", new String[0]);
		blacklistNames = new ArrayList<String>();
		String[] names = itemsProp.getStringList();
		for (int i=0; i<names.length; i++)
		{
			blacklistNames.add(names[i]);
		}
		Config.config.save();
	}

	/**Update config file from blacklistNames*/
	public static void syncToConfig(String playerName) 
	{
		Property autoAddProp = Config.config.get(playerName, "AutoAdd", true);
		autoAddProp.set(autoAdd);
		Property itemsProp = Config.config.get(playerName, "Blacklisted Items", new String[0]);
		String[] names = new String[blacklistNames.size()];
		for (int i=0; i<blacklistNames.size(); i++)
			names[i] = blacklistNames.get(i);
		itemsProp.set(names);
		Config.config.save();
	}
}
