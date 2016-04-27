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
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class CommandBlacklist extends CommandBase
{
	private static List aliases;
	private static ArrayList<String> actions;
	/**SHOULD NOT BE ACCESSED DIRECTLY - USE GETTER*/
	private static Collection displayNames;

	public CommandBlacklist()
	{
		actions = new ArrayList<String>();
		actions.add("add");
		actions.add("remove");
		actions.add("clear");
		actions.add("autoAdd");
	}

	public static Collection getDisplayNames()
	{
		if (displayNames == null)
			displayNames = convertToDisplayNames(Item.itemRegistry.getKeys());
		return displayNames;
	}

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
				Item item = getItemByText(null, (String) iterator.next());
				ArrayList<ItemStack> subItems = new ArrayList<ItemStack>();
				item.getSubItems(item, item.getCreativeTab(), subItems);
				for (ItemStack stack : subItems)
					displayNames.add((String) item.getItemStackDisplayName(stack).replace(" ", "_"));
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return (Collection) displayNames;
	}

	public List addTabCompletionOptions(ICommandSender sender, String[] args) 
	{
		Config.syncFromConfig(sender.getCommandSenderName());
		if (args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, CommandBlacklist.actions);
		}
		else if (args.length == 2)
		{
			if (args[0].equalsIgnoreCase("add"))
				return getListOfStringsFromIterableMatchingLastWord(args, getDisplayNames());
			else if (args[0].equalsIgnoreCase("remove"))
				return getListOfStringsFromIterableMatchingLastWord(args, Config.blacklistNames);
			else
				return null;
		}
		else
			return null;
	}

	public boolean canCommandSenderUseCommand(ICommandSender sender) 
	{
		return true;
	}

	public void processCommand(ICommandSender sender, String[] args) throws CommandException 
	{
		if (args.length == 2)
			args[1] = this.addCaps(args[1]);
		Config.syncFromConfig(sender.getCommandSenderName());
		if (args.length == 0)
		{
			sender.addChatMessage(new ChatComponentTranslation("[AutoPickup] Blacklist Contains:").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_AQUA)/*.setBold(true)*/));
			for (int i=0; i < Config.blacklistNames.size(); i++)
				sender.addChatMessage(new ChatComponentTranslation("- "+Config.blacklistNames.get(i)).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)));
			return;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("clear"))
		{
			Config.blacklistNames.clear();
			Config.syncToConfig(sender.getCommandSenderName());
			sender.addChatMessage(new ChatComponentTranslation("[AutoPickup] Blacklist cleared.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
			return;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("AutoAdd"))
		{
			sender.addChatMessage(new ChatComponentTranslation("[AutoPickup] AutoAdd is currently "+(Config.autoAdd ? "enabled." : "disabled.")).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
			return;
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("AutoAdd"))
		{
			if (args[1].equalsIgnoreCase("true"))
			{
				Config.autoAdd = true;
				Config.syncToConfig(sender.getCommandSenderName());
				sender.addChatMessage(new ChatComponentTranslation("[AutoPickup] AutoAdd enabled.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				return;
			}
			else if (args[1].equalsIgnoreCase("false"))
			{
				Config.autoAdd = false;
				Config.syncToConfig(sender.getCommandSenderName());
				sender.addChatMessage(new ChatComponentTranslation("[AutoPickup] AutoAdd disabled.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				return;
			}
			else
			{
				sender.addChatMessage(new ChatComponentTranslation("Usage: /b AutoAdd [true/false]").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				return;
			}
		}
		else if (args.length == 2 && getDisplayNames().contains(args[1]))
		{
			if (args[0].equalsIgnoreCase("add")) 
			{
				if (Config.blacklistNames.contains(args[1]))
				{
					sender.addChatMessage(new ChatComponentTranslation("[AutoPickup] Blacklist already contains "+args[1]+".").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					return;
				}
				else
				{
					Config.blacklistNames.add(args[1]);
					Config.syncToConfig(sender.getCommandSenderName());
					sender.addChatMessage(new ChatComponentTranslation("[AutoPickup] Added "+args[1]+" to blacklist.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
					return;
				}
			}
			else if (args[0].equalsIgnoreCase("remove"))
			{
				if (Config.blacklistNames.contains(args[1]))
				{
					Config.blacklistNames.remove(args[1]);
					Config.syncToConfig(sender.getCommandSenderName());
					sender.addChatMessage(new ChatComponentTranslation("[AutoPickup] Removed "+args[1]+" from blacklist.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
					return;
				}
				else
				{
					sender.addChatMessage(new ChatComponentTranslation("[AutoPickup] Blacklist does not contain "+args[1]+".").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					return;
				}
			}
			else
			{
				sender.addChatMessage(new ChatComponentTranslation("Usage: /b <action> [<item>]").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				return;
			}
		}
		sender.addChatMessage(new ChatComponentTranslation("Usage: /b <action> [<item/true/false>]").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
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

	public List getCommandAliases() 
	{
		aliases = new ArrayList();
		aliases.add("blacklist");
		aliases.add("b");
		return aliases;
	}

	public String getCommandUsage(ICommandSender sender) 
	{
		return "/b <action> [<item>]";
	}

	public String getCommandName() 
	{
		return "blacklist";
	}

}
