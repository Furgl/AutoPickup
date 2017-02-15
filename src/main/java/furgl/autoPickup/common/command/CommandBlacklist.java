package furgl.autoPickup.common.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import furgl.autoPickup.common.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
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
	private static final TextFormatting COLOR_NEUTRAL_DARK = TextFormatting.DARK_AQUA;
	private static final TextFormatting COLOR_NEUTRAL_LIGHT = TextFormatting.AQUA;
	private static final TextFormatting COLOR_SUCCESS = TextFormatting.DARK_GREEN;
	private static final TextFormatting COLOR_FAIL = TextFormatting.RED;
	private static List aliases;
	/**SHOULD NOT BE ACCESSED DIRECTLY - USE GETTER*/
	private static Collection displayNames;
	private static ArrayList<String> actions;
	private static ArrayList<String> presets;
	private static ArrayList<ArrayList<String>> presetItems;
	
	public CommandBlacklist() {
		//initialize actions
		actions = new ArrayList<String>();
		actions.add("add");
		actions.add("remove");
		actions.add("clear");
		actions.add("autoAdd");
		actions.add("preset");
		//initialize presets
		presets = new ArrayList<String>();
		presetItems = new ArrayList<ArrayList<String>>();
		ArrayList<String> items;
		presets.add("Mining");
		items = (getDisplayNames(new ArrayList<Material>() {{add(Material.GROUND); add(Material.SAND);}}));
		items.add(Item.getItemFromBlock(Blocks.COBBLESTONE).getItemStackDisplayName(new ItemStack(Blocks.COBBLESTONE)).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.STONE).getItemStackDisplayName(new ItemStack(Blocks.STONE, 1, EnumType.STONE.getMetadata())).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.STONE).getItemStackDisplayName(new ItemStack(Blocks.STONE, 1, EnumType.GRANITE.getMetadata())).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.STONE).getItemStackDisplayName(new ItemStack(Blocks.STONE, 1, EnumType.ANDESITE.getMetadata())).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.STONE).getItemStackDisplayName(new ItemStack(Blocks.STONE, 1, EnumType.DIORITE.getMetadata())).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.GRASS).getItemStackDisplayName(new ItemStack(Blocks.GRASS)).replace(" ", "_"));
		items.add(Items.FLINT.getItemStackDisplayName(new ItemStack(Items.FLINT)).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.NETHER_BRICK).getItemStackDisplayName(new ItemStack(Blocks.NETHER_BRICK)).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.NETHER_BRICK_FENCE).getItemStackDisplayName(new ItemStack(Blocks.NETHER_BRICK_FENCE)).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.NETHER_BRICK_STAIRS).getItemStackDisplayName(new ItemStack(Blocks.NETHER_BRICK_STAIRS)).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.NETHERRACK).getItemStackDisplayName(new ItemStack(Blocks.NETHERRACK)).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.SANDSTONE).getItemStackDisplayName(new ItemStack(Blocks.SANDSTONE)).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.MAGMA).getItemStackDisplayName(new ItemStack(Blocks.MAGMA)).replace(" ", "_"));
		presetItems.add(items);
		presets.add("Monster_Drops");
		items = new ArrayList<String>();
		items.add(Items.ROTTEN_FLESH.getItemStackDisplayName(new ItemStack(Items.ROTTEN_FLESH)).replace(" ", "_"));
		items.add(Items.BONE.getItemStackDisplayName(new ItemStack(Items.BONE)).replace(" ", "_"));
		items.add(Items.ARROW.getItemStackDisplayName(new ItemStack(Items.ARROW)).replace(" ", "_"));
		items.add(Items.STRING.getItemStackDisplayName(new ItemStack(Items.STRING)).replace(" ", "_"));
		items.add(Items.SPIDER_EYE.getItemStackDisplayName(new ItemStack(Items.SPIDER_EYE)).replace(" ", "_"));
		items.add(Items.GUNPOWDER.getItemStackDisplayName(new ItemStack(Items.GUNPOWDER)).replace(" ", "_"));
		items.add(Items.SLIME_BALL.getItemStackDisplayName(new ItemStack(Items.SLIME_BALL)).replace(" ", "_"));
		items.add(Items.GOLD_NUGGET.getItemStackDisplayName(new ItemStack(Items.GOLD_NUGGET)).replace(" ", "_"));
		items.add(Items.MAGMA_CREAM.getItemStackDisplayName(new ItemStack(Items.MAGMA_CREAM)).replace(" ", "_"));
		items.add(Items.GLASS_BOTTLE.getItemStackDisplayName(new ItemStack(Items.GLASS_BOTTLE)).replace(" ", "_"));
		items.add(Items.STICK.getItemStackDisplayName(new ItemStack(Items.STICK)).replace(" ", "_"));
		items.add(Items.SUGAR.getItemStackDisplayName(new ItemStack(Items.SUGAR)).replace(" ", "_"));
		items.add(Items.CARROT.getItemStackDisplayName(new ItemStack(Items.CARROT)).replace(" ", "_"));
		items.add(Items.POTATO.getItemStackDisplayName(new ItemStack(Items.POTATO)).replace(" ", "_"));
		items.add(Items.BOW.getItemStackDisplayName(new ItemStack(Items.BOW)).replace(" ", "_"));
		items.add(Items.GOLDEN_SWORD.getItemStackDisplayName(new ItemStack(Items.GOLDEN_SWORD)).replace(" ", "_"));
		items.add(Items.STONE_SWORD.getItemStackDisplayName(new ItemStack(Items.STONE_SWORD)).replace(" ", "_"));
		presetItems.add(items);
		presets.add("Animal_Drops");
		items = new ArrayList<String>();
		items.add(Items.CHICKEN.getItemStackDisplayName(new ItemStack(Items.CHICKEN)).replace(" ", "_"));
		items.add(Items.EGG.getItemStackDisplayName(new ItemStack(Items.EGG)).replace(" ", "_"));
		items.add(Items.FEATHER.getItemStackDisplayName(new ItemStack(Items.FEATHER)).replace(" ", "_"));
		items.add(Items.BEEF.getItemStackDisplayName(new ItemStack(Items.BEEF)).replace(" ", "_"));
		items.add(Items.LEATHER.getItemStackDisplayName(new ItemStack(Items.LEATHER)).replace(" ", "_"));
		items.add(Items.PORKCHOP.getItemStackDisplayName(new ItemStack(Items.PORKCHOP)).replace(" ", "_"));
		items.add(Items.RABBIT.getItemStackDisplayName(new ItemStack(Items.RABBIT)).replace(" ", "_"));
		items.add(Items.RABBIT_FOOT.getItemStackDisplayName(new ItemStack(Items.RABBIT_FOOT)).replace(" ", "_"));
		items.add(Items.RABBIT_HIDE.getItemStackDisplayName(new ItemStack(Items.RABBIT_HIDE)).replace(" ", "_"));
		items.add(Item.getItemFromBlock(Blocks.WOOL).getItemStackDisplayName(new ItemStack(Blocks.WOOL)).replace(" ", "_"));
		items.add(Items.MUTTON.getItemStackDisplayName(new ItemStack(Items.MUTTON)).replace(" ", "_"));
		items.add(Items.DYE.getItemStackDisplayName(new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage())).replace(" ", "_"));
		presetItems.add(items);
		presets.add("Vegetation");
		presetItems.add(getDisplayNames(new ArrayList<Material>() {{add(Material.CACTUS); add(Material.GOURD); add(Material.LEAVES); add(Material.PLANTS); add(Material.VINE);}}));
	}
	
	@SuppressWarnings("deprecation")
	private static ArrayList<String> getDisplayNames(ArrayList<Material> materials) {
		ArrayList<String> items = new ArrayList<String>();
		Iterator<ResourceLocation> itr = Block.REGISTRY.getKeys().iterator();
		while (itr.hasNext()) {
			try {
				Block block = getBlockByText(null, (itr.next()).toString());
				String prevName = "";
				for (int i=0; i<99; i++) { //prevent infinite loops
					String newName = block.getItem(null, new BlockPos(0,0,0), block.getStateFromMeta(i)).getItem().getItemStackDisplayName(block.getItem(null, new BlockPos(0,0,0), block.getStateFromMeta(i)));
					Material material = block.getMaterial(block.getStateFromMeta(i));
					if (!prevName.equalsIgnoreCase(newName) && !items.contains(newName.replace(" ", "_")) && materials.contains(material)) {
						items.add(newName.replace(" ", "_"));
						prevName = newName;
					}
					else
						break;
				}
			} 
			catch (Exception e) { }
		}
		return items;
	}

	private static Collection getDisplayNames() {
		if (displayNames == null)
			displayNames = convertToDisplayNames(Item.REGISTRY.getKeys());
		return displayNames;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	private static Collection convertToDisplayNames(Set set) {
		displayNames = new ArrayList<String>();
		Iterator iterator = Item.REGISTRY.getKeys().iterator();
		while (iterator.hasNext()) {
			try {
				Item item = getItemByText(null, (iterator.next()).toString());
				String prevName = "";
				for (int i=0; i<99; i++) { //prevent infinite loops
					String newName = item.getItemStackDisplayName(new ItemStack(item, 1, i));
					if (!prevName.equalsIgnoreCase(newName) && !displayNames.contains(newName.replace(" ", "_"))) {
						displayNames.add(newName.replace(" ", "_"));
						prevName = newName;
					}
				}
			} 
			catch (Exception e) { }
		}
		return (Collection) displayNames;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)	{		
		Config.syncFromConfig(sender.getName());
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, CommandBlacklist.actions);
		else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("add"))
				return getListOfStringsMatchingLastWord(args, getDisplayNames());
			else if (args[0].equalsIgnoreCase("remove"))
				return getListOfStringsMatchingLastWord(args, Config.blacklistNames);
			else if (args[0].equalsIgnoreCase("autoAdd"))
				return getListOfStringsMatchingLastWord(args, new ArrayList<String>() {{add("enable"); add("disable");}});
				else if (args[0].equalsIgnoreCase("preset"))
					return getListOfStringsMatchingLastWord(args, CommandBlacklist.presets);
				else
					return new ArrayList<String>();
		}
		else if (args.length == 3 && args[0].equalsIgnoreCase("preset"))
			return getListOfStringsMatchingLastWord(args, new ArrayList<String>() {{add("enable"); add("disable");}});
		else
			return new ArrayList<String>();
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 2)
			args[1] = this.addCaps(args[1]);
		Config.syncFromConfig(sender.getName());
		if (args.length == 0) {
			sender.sendMessage(new TextComponentString("[AutoPickup] Blacklist Contains:").setStyle(new Style().setColor(COLOR_NEUTRAL_DARK)));
			for (int i=0; i < Config.blacklistNames.size(); i++)
				sender.sendMessage(new TextComponentString("- "+Config.blacklistNames.get(i)).setStyle(new Style().setColor(COLOR_NEUTRAL_LIGHT)));
			return;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("clear"))	{
			Config.blacklistNames.clear();
			Config.syncToConfig(sender.getName());
			sender.sendMessage(new TextComponentString("[AutoPickup] Blacklist cleared.").setStyle(new Style().setColor(COLOR_SUCCESS)));
			return;
		}
		//AUTOADD
		else if (args.length == 1 && args[0].equalsIgnoreCase("autoAdd")) {
			sender.sendMessage(new TextComponentString("[AutoPickup] AutoAdd is currently "+(Config.autoAdd ? "enabled." : "disabled.")).setStyle(new Style().setColor(COLOR_NEUTRAL_LIGHT)));
			return;
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("autoAdd")) {
			if (args[1].equalsIgnoreCase("enable")) {
				Config.autoAdd = true;
				Config.syncToConfig(sender.getName());
				sender.sendMessage(new TextComponentString("[AutoPickup] AutoAdd enabled.").setStyle(new Style().setColor(COLOR_SUCCESS)));
				return;
			}
			else if (args[1].equalsIgnoreCase("disable")) {
				Config.autoAdd = false;
				Config.syncToConfig(sender.getName());
				sender.sendMessage(new TextComponentString("[AutoPickup] AutoAdd disabled.").setStyle(new Style().setColor(COLOR_SUCCESS)));
				return;
			}
			else {
				sender.sendMessage(new TextComponentString("Usage: /b autoAdd [enable/disable]").setStyle(new Style().setColor(COLOR_FAIL)));
				return;
			}
		}
		//PRESETS
		else if (args.length == 1 && args[0].equalsIgnoreCase("preset")) {
			sender.sendMessage(new TextComponentString("[AutoPickup] A preset is a collection of items that can be added to or removed from the blacklist easily.").setStyle(new Style().setColor(COLOR_NEUTRAL_LIGHT)));
			return;
		}
		else if (args.length == 2 && args[0].equalsIgnoreCase("preset")) {
			for (int i=0; i<presets.size(); i++) {
				if (args[1].equalsIgnoreCase(presets.get(i))) {
					sender.sendMessage(new TextComponentString("[AutoPickup] The "+presets.get(i)+" preset contains these items:").setStyle(new Style().setColor(COLOR_NEUTRAL_DARK)));
					for (String item : presetItems.get(i))
						sender.sendMessage(new TextComponentString("- "+item).setStyle(new Style().setColor(COLOR_NEUTRAL_LIGHT)));
					return;
				}
			}
			sender.sendMessage(new TextComponentString("Usage: /b preset [preset] [enable/disable]").setStyle(new Style().setColor(COLOR_FAIL)));
			return;
		}
		else if (args.length == 3 && args[0].equalsIgnoreCase("preset")) {
			for (int i=0; i<presets.size(); i++) {
				if (args[1].equalsIgnoreCase(presets.get(i))) {
					int changedItems = 0;
					if (args[2].equalsIgnoreCase("enable")) {
						for (String item : presetItems.get(i))
							if (!Config.blacklistNames.contains(item)) {
								Config.blacklistNames.add(item);
								changedItems++;
							}
						Config.syncToConfig(sender.getName());
						sender.sendMessage(new TextComponentString("[AutoPickup] Added "+changedItems+" items to blacklist.").setStyle(new Style().setColor(COLOR_SUCCESS)));
						return;
					}
					else if (args[2].equalsIgnoreCase("disable")) {
						for (String item : presetItems.get(i))
							if (Config.blacklistNames.contains(item)) {
								Config.blacklistNames.remove(item);
								changedItems++;
							}
						Config.syncToConfig(sender.getName());
						sender.sendMessage(new TextComponentString("[AutoPickup] Removed "+changedItems+" items from blacklist.").setStyle(new Style().setColor(COLOR_SUCCESS)));
						return;
					}
				}
			}
			sender.sendMessage(new TextComponentString("Usage: /b preset [preset] [enable/disable]").setStyle(new Style().setColor(COLOR_FAIL)));
			return;
		}
		//ADD/REMOVE
		else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("add")) {													
				if (Config.blacklistNames.contains(args[1])) {
					sender.sendMessage(new TextComponentString("[AutoPickup] Blacklist already contains "+args[1]+".").setStyle(new Style().setColor(COLOR_FAIL)));
					return;	
				}			
				else if (getDisplayNames().contains(args[1])) {
					Config.blacklistNames.add(args[1]);
					Config.syncToConfig(sender.getName());
					sender.sendMessage(new TextComponentString("[AutoPickup] Added "+args[1]+" to blacklist.").setStyle(new Style().setColor(COLOR_SUCCESS)));
					return;
				}
			}
			else if (args[0].equalsIgnoreCase("remove")) {
				if (Config.blacklistNames.contains(args[1])) {
					Config.blacklistNames.remove(args[1]);
					Config.syncToConfig(sender.getName());
					sender.sendMessage(new TextComponentString("[AutoPickup] Removed "+args[1]+" from blacklist.").setStyle(new Style().setColor(COLOR_SUCCESS)));
					return;
				}
				else {
					sender.sendMessage(new TextComponentString("[AutoPickup] Blacklist does not contain "+args[1]+".").setStyle(new Style().setColor(COLOR_FAIL)));
					return;
				}
			}
			else {
				sender.sendMessage(new TextComponentString("Usage: /b <action> [<item>]").setStyle(new Style().setColor(COLOR_FAIL)));
				return;
			}
		}
		sender.sendMessage(new TextComponentString("Usage: /b <action> [<item/true/false>]").setStyle(new Style().setColor(COLOR_FAIL)));
	}

	private String addCaps(String string) {
		string = string.substring(0, 1).toUpperCase()+string.substring(1);
		if (string.length() >= string.indexOf("_")+2)
			string = string.substring(0, string.indexOf("_")+1)+string.substring(string.indexOf("_")+1, string.indexOf("_")+2).toUpperCase()+string.substring(string.indexOf("_")+2);
		if (string.length() >= string.lastIndexOf("_")+2)
			string = string.substring(0, string.lastIndexOf("_")+1)+string.substring(string.lastIndexOf("_")+1, string.lastIndexOf("_")+2).toUpperCase()+string.substring(string.lastIndexOf("_")+2);
		return string;
	}

	@Override
	public List<String> getAliases() {
		aliases = new ArrayList<String>();
		aliases.add("blacklist");
		aliases.add("b");
		return aliases;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/b <action> [<item>]";
	}

	@Override
	public String getName() {
		return "blacklist";
	}
}
