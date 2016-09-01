package furgl.autoPickup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandBlacklist extends CommandBase
{
	private static List aliases;
	private static ArrayList<String> actions;
	/**SHOULD NOT BE ACCESSED DIRECTLY - USE GETTER*/
	private static Collection displayNames;

	static
	{
		actions = new ArrayList<String>();
		actions.add("add");
		actions.add("remove");
		actions.add("clear");
		actions.add("autoAdd");
	}

	private static Collection getDisplayNames()
	{
		if (displayNames == null)
			displayNames = convertToDisplayNames(Item.itemRegistry.getKeys());
		return displayNames;
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	private static Collection convertToDisplayNames(Set set)
	{
		displayNames = new ArrayList<String>();
		Iterator iterator = set.iterator();
		while (iterator.hasNext())
		{
			try 
			{
				Item item = getItemByText(null, ((ResourceLocation) iterator.next()).toString());
				displayNames.add((String) item.getItemStackDisplayName(new ItemStack(item)).replace(" ", "_"));
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return (Collection) displayNames;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		Config.syncFromConfig(sender.getName());
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, CommandBlacklist.actions);
		}
		else if (args.length == 2)
		{
			if (args[0].equalsIgnoreCase("add"))
				return getListOfStringsMatchingLastWord(args, getDisplayNames());
			else if (args[0].equalsIgnoreCase("remove"))
				return getListOfStringsMatchingLastWord(args, Config.blacklistNames);
			else
				return null;
		}
		else
			return null;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 2)
			args[1] = this.addCaps(args[1]);
		Config.syncFromConfig(sender.getName());
		if (args.length == 0)
		{
			sender.addChatMessage(new TextComponentString("[AutoPickup] Blacklist Contains:").setChatStyle(new Style().setColor(TextFormatting.DARK_AQUA)));
			for (int i=0; i < Config.blacklistNames.size(); i++)
				sender.addChatMessage(new TextComponentString("- "+Config.blacklistNames.get(i)).setChatStyle(new Style().setColor(TextFormatting.AQUA)));
			return;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("clear"))
		{
			Config.blacklistNames.clear();
			Config.syncToConfig(sender.getName());
			sender.addChatMessage(new TextComponentString("[AutoPickup] Blacklist cleared.").setChatStyle(new Style().setColor(TextFormatting.RED)));
			return;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("AutoAdd"))
		{
			sender.addChatMessage(new TextComponentString("[AutoPickup] AutoAdd is currently "+(Config.autoAdd ? "enabled." : "disabled.")).setChatStyle(new Style().setColor(TextFormatting.RED)));
			return;
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("AutoAdd"))
		{
			if (args[1].equalsIgnoreCase("true"))
			{
				Config.autoAdd = true;
				Config.syncToConfig(sender.getName());
				sender.addChatMessage(new TextComponentString("[AutoPickup] AutoAdd enabled.").setChatStyle(new Style().setColor(TextFormatting.RED)));
				return;
			}
			else if (args[1].equalsIgnoreCase("false"))
			{
				Config.autoAdd = false;
				Config.syncToConfig(sender.getName());
				sender.addChatMessage(new TextComponentString("[AutoPickup] AutoAdd disabled.").setChatStyle(new Style().setColor(TextFormatting.RED)));
				return;
			}
			else
			{
				sender.addChatMessage(new TextComponentString("Usage: /b AutoAdd [true/false]").setChatStyle(new Style().setColor(TextFormatting.RED)));
				return;
			}
		}
		else if (args.length == 2)
		{
			if (args[0].equalsIgnoreCase("add")) 
			{													
				if (Config.blacklistNames.contains(args[1]))
				{
					sender.addChatMessage(new TextComponentString("[AutoPickup] Blacklist already contains "+args[1]+".").setChatStyle(new Style().setColor(TextFormatting.RED)));
					return;	
				}			
				else if (getDisplayNames().contains(args[1]))
				{
					Config.blacklistNames.add(args[1]);
					Config.syncToConfig(sender.getName());
					sender.addChatMessage(new TextComponentString("[AutoPickup] Added "+args[1]+" to blacklist.").setChatStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
					return;
				}
			}
			else if (args[0].equalsIgnoreCase("remove"))
			{
				if (Config.blacklistNames.contains(args[1]))
				{
					Config.blacklistNames.remove(args[1]);
					Config.syncToConfig(sender.getName());
					sender.addChatMessage(new TextComponentString("[AutoPickup] Removed "+args[1]+" from blacklist.").setChatStyle(new Style().setColor(TextFormatting.GOLD)));
					return;
				}
				else
				{
					sender.addChatMessage(new TextComponentString("[AutoPickup] Blacklist does not contain "+args[1]+".").setChatStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}
			}
			else
			{
				sender.addChatMessage(new TextComponentString("Usage: /b <action> [<item>]").setChatStyle(new Style().setColor(TextFormatting.RED)));
				return;
			}
		}
		sender.addChatMessage(new TextComponentString("Usage: /b <action> [<item/true/false>]").setChatStyle(new Style().setColor(TextFormatting.RED)));
	}

	private String addCaps(String string) 
	{
		string = string.substring(0, 1).toUpperCase()+string.substring(1);
		if (string.length() >= string.indexOf("_")+2)
			string = string.substring(0, string.indexOf("_")+1)+string.substring(string.indexOf("_")+1, string.indexOf("_")+2).toUpperCase()+string.substring(string.indexOf("_")+2);
		if (string.length() >= string.lastIndexOf("_")+2)
			string = string.substring(0, string.lastIndexOf("_")+1)+string.substring(string.lastIndexOf("_")+1, string.lastIndexOf("_")+2).toUpperCase()+string.substring(string.lastIndexOf("_")+2);
		return string;
	}

	@Override
	public List<String> getCommandAliases()
	{
		aliases = new ArrayList<String>();
		aliases.add("blacklist");
		aliases.add("b");
		return aliases;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/b <action> [<item>]";
	}

	@Override
	public String getCommandName() 
	{
		return "blacklist";
	}
}
