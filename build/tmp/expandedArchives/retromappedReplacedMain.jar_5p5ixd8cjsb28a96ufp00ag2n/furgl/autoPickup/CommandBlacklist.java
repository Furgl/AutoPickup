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
			displayNames = convertToDisplayNames(Item.field_150901_e.func_148742_b());
		return displayNames;
	}

	@Override
	public int func_82362_a()
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
				Item item = func_147179_f(null, ((ResourceLocation) iterator.next()).toString());
				displayNames.add((String) item.func_77653_i(new ItemStack(item)).replace(" ", "_"));
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return (Collection) displayNames;
	}

	@Override
	public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		Config.syncFromConfig(sender.func_70005_c_());
		if (args.length == 1)
		{
			return func_175762_a(args, CommandBlacklist.actions);
		}
		else if (args.length == 2)
		{
			if (args[0].equalsIgnoreCase("add"))
				return func_175762_a(args, getDisplayNames());
			else if (args[0].equalsIgnoreCase("remove"))
				return func_175762_a(args, Config.blacklistNames);
			else
				return null;
		}
		else
			return null;
	}

	@Override
	public boolean func_184882_a(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}

	@Override
	public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 2)
			args[1] = this.addCaps(args[1]);
		Config.syncFromConfig(sender.func_70005_c_());
		if (args.length == 0)
		{
			sender.func_145747_a(new TextComponentString("[AutoPickup] Blacklist Contains:").func_150255_a(new Style().func_150238_a(TextFormatting.DARK_AQUA)));
			for (int i=0; i < Config.blacklistNames.size(); i++)
				sender.func_145747_a(new TextComponentString("- "+Config.blacklistNames.get(i)).func_150255_a(new Style().func_150238_a(TextFormatting.AQUA)));
			return;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("clear"))
		{
			Config.blacklistNames.clear();
			Config.syncToConfig(sender.func_70005_c_());
			sender.func_145747_a(new TextComponentString("[AutoPickup] Blacklist cleared.").func_150255_a(new Style().func_150238_a(TextFormatting.RED)));
			return;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("AutoAdd"))
		{
			sender.func_145747_a(new TextComponentString("[AutoPickup] AutoAdd is currently "+(Config.autoAdd ? "enabled." : "disabled.")).func_150255_a(new Style().func_150238_a(TextFormatting.RED)));
			return;
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("AutoAdd"))
		{
			if (args[1].equalsIgnoreCase("true"))
			{
				Config.autoAdd = true;
				Config.syncToConfig(sender.func_70005_c_());
				sender.func_145747_a(new TextComponentString("[AutoPickup] AutoAdd enabled.").func_150255_a(new Style().func_150238_a(TextFormatting.RED)));
				return;
			}
			else if (args[1].equalsIgnoreCase("false"))
			{
				Config.autoAdd = false;
				Config.syncToConfig(sender.func_70005_c_());
				sender.func_145747_a(new TextComponentString("[AutoPickup] AutoAdd disabled.").func_150255_a(new Style().func_150238_a(TextFormatting.RED)));
				return;
			}
			else
			{
				sender.func_145747_a(new TextComponentString("Usage: /b AutoAdd [true/false]").func_150255_a(new Style().func_150238_a(TextFormatting.RED)));
				return;
			}
		}
		else if (args.length == 2)
		{
			if (args[0].equalsIgnoreCase("add")) 
			{													
				if (Config.blacklistNames.contains(args[1]))
				{
					sender.func_145747_a(new TextComponentString("[AutoPickup] Blacklist already contains "+args[1]+".").func_150255_a(new Style().func_150238_a(TextFormatting.RED)));
					return;	
				}			
				else if (getDisplayNames().contains(args[1]))
				{
					Config.blacklistNames.add(args[1]);
					Config.syncToConfig(sender.func_70005_c_());
					sender.func_145747_a(new TextComponentString("[AutoPickup] Added "+args[1]+" to blacklist.").func_150255_a(new Style().func_150238_a(TextFormatting.DARK_GREEN)));
					return;
				}
			}
			else if (args[0].equalsIgnoreCase("remove"))
			{
				if (Config.blacklistNames.contains(args[1]))
				{
					Config.blacklistNames.remove(args[1]);
					Config.syncToConfig(sender.func_70005_c_());
					sender.func_145747_a(new TextComponentString("[AutoPickup] Removed "+args[1]+" from blacklist.").func_150255_a(new Style().func_150238_a(TextFormatting.GOLD)));
					return;
				}
				else
				{
					sender.func_145747_a(new TextComponentString("[AutoPickup] Blacklist does not contain "+args[1]+".").func_150255_a(new Style().func_150238_a(TextFormatting.RED)));
					return;
				}
			}
			else
			{
				sender.func_145747_a(new TextComponentString("Usage: /b <action> [<item>]").func_150255_a(new Style().func_150238_a(TextFormatting.RED)));
				return;
			}
		}
		sender.func_145747_a(new TextComponentString("Usage: /b <action> [<item/true/false>]").func_150255_a(new Style().func_150238_a(TextFormatting.RED)));
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
	public List<String> func_71514_a()
	{
		aliases = new ArrayList<String>();
		aliases.add("blacklist");
		aliases.add("b");
		return aliases;
	}

	@Override
	public String func_71518_a(ICommandSender sender) 
	{
		return "/b <action> [<item>]";
	}

	@Override
	public String func_71517_b() 
	{
		return "blacklist";
	}
}
