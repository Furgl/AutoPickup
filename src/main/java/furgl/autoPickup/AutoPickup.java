package furgl.autoPickup;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import furgl.autoPickup.event.DelayedPickupEvent;
import furgl.autoPickup.event.EntityItemPickupEvents;
import furgl.autoPickup.event.EntityJoinWorldEvents;
import furgl.autoPickup.event.ItemTossEvents;
import furgl.autoPickup.event.PlaySoundAtEntityEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = AutoPickup.MODID, name = AutoPickup.MODNAME, version = AutoPickup.VERSION)
public class AutoPickup
{
	public static final String MODID = "autopickup";
	public static final String MODNAME = "AutoPickup";
	public static final String VERSION = "2.1";

	public static SimpleNetworkWrapper network;
	@SidedProxy(clientSide = "furgl.autoPickup.ClientProxy", serverSide = "furgl.autoPickup.CommonProxy")
	public static CommonProxy proxy;	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		network = NetworkRegistry.INSTANCE.newSimpleChannel("autoPickupChannel");
		network.registerMessage(PacketIgnoreKey.Handler.class, PacketIgnoreKey.class, 1, Side.SERVER);
		registerEventListeners();
		Config.init(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		AutoPickup.proxy.init();
	}


	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandBlacklist());
	}

	public void registerEventListeners() 
	{
		MinecraftForge.EVENT_BUS.register(new DelayedPickupEvent());
		MinecraftForge.EVENT_BUS.register(new EntityJoinWorldEvents()); 
		MinecraftForge.EVENT_BUS.register(new PlaySoundAtEntityEvents());
		MinecraftForge.EVENT_BUS.register(new EntityItemPickupEvents());
		MinecraftForge.EVENT_BUS.register(new ItemTossEvents());
		FMLCommonHandler.instance().bus().register(new IgnoreKey());
		FMLCommonHandler.instance().bus().register(new DelayedPickupEvent());
	}

	public static boolean addItem(EntityPlayer player, ItemStack itemStack, boolean giveIfCreative)
	{
		if (!giveIfCreative && player.capabilities.isCreativeMode)
			return true;
		Config.syncFromConfig(player.getDisplayName());
		if (itemStack != null && (!Config.blacklistNames.contains(itemStack.getItem().getItemStackDisplayName(itemStack).replace(" ", "_")) || IgnoreKey.isPressed))
		{
			boolean value = player.inventory.addItemStackToInventory(itemStack);
			if (value)
				player.inventoryContainer.detectAndSendChanges();
			return value;
		}
		else
			return false;
	}
}

