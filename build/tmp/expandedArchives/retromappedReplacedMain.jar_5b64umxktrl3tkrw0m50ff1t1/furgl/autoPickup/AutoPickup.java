package furgl.autoPickup;

import furgl.autoPickup.event.DelayedPickupEvent;
import furgl.autoPickup.event.EntityItemPickupEvents;
import furgl.autoPickup.event.ItemTossEvents;
import furgl.autoPickup.event.PlaySoundAtEntityEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

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
		Config.init(event.getSuggestedConfigurationFile());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		registerEventListeners();
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
		MinecraftForge.EVENT_BUS.register(new PlaySoundAtEntityEvents());
		MinecraftForge.EVENT_BUS.register(new EntityItemPickupEvents());
		MinecraftForge.EVENT_BUS.register(new ItemTossEvents());
		MinecraftForge.EVENT_BUS.register(new IgnoreKey());
		MinecraftForge.EVENT_BUS.register(new DelayedPickupEvent());
	}

	public static boolean addItem(EntityPlayer player, ItemStack itemStack, boolean giveIfCreative)
	{
		if (!giveIfCreative && player.field_71075_bZ.field_75098_d)
			return true;
		Config.syncFromConfig(player.func_70005_c_());
		if (itemStack != null && (!Config.blacklistNames.contains(itemStack.func_77973_b().func_77653_i(itemStack).replace(" ", "_")) || IgnoreKey.isPressed))
		{
			boolean value = player.field_71071_by.func_70441_a(itemStack);
			if (value)
				player.field_71069_bz.func_75142_b();
			return value;
		}
		else
			return false;
	}
}

