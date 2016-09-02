package furgl.autoPickup;

import org.lwjgl.input.Keyboard;

import furgl.autoPickup.event.DelayedPickupEvent;
import furgl.autoPickup.event.EntityItemPickupEvents;
import furgl.autoPickup.event.ItemTossEvents;
import furgl.autoPickup.event.PlaySoundAtEntityEvents;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
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
	public static final String VERSION = "2.0";
	
	public static SimpleNetworkWrapper network;
	public static KeyBinding ignoreBlacklist = new KeyBinding("Ignore Blacklist", Keyboard.KEY_LMENU, "Auto Pickup");

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
		ClientRegistry.registerKeyBinding(ignoreBlacklist);
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
		if (!giveIfCreative && player.capabilities.isCreativeMode)
			return true;
		Config.syncFromConfig(player.getName());
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

