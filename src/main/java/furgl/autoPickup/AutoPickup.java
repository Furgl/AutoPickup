package furgl.autoPickup;

import org.lwjgl.input.Keyboard;

import furgl.autoPickup.event.AttackEntityEvents;
import furgl.autoPickup.event.BlockEvents;
import furgl.autoPickup.event.EntityInteractEvents;
import furgl.autoPickup.event.EntityItemPickupEvents;
import furgl.autoPickup.event.ItemTossEvents;
import furgl.autoPickup.event.LivingEvents;
import furgl.autoPickup.event.PlaySoundAtEntityEvents;
import furgl.autoPickup.event.PlayerInteractEvents;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
	public static final String VERSION = "1.1";
	
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
		MinecraftForge.EVENT_BUS.register(new BlockEvents()); 
		MinecraftForge.EVENT_BUS.register(new LivingEvents()); 
		MinecraftForge.EVENT_BUS.register(new PlaySoundAtEntityEvents());
		MinecraftForge.EVENT_BUS.register(new EntityInteractEvents());
		MinecraftForge.EVENT_BUS.register(new AttackEntityEvents());
		MinecraftForge.EVENT_BUS.register(new PlayerInteractEvents());
		MinecraftForge.EVENT_BUS.register(new EntityItemPickupEvents());
		MinecraftForge.EVENT_BUS.register(new ItemTossEvents());
		FMLCommonHandler.instance().bus().register(new IgnoreKey());
	}

	public static boolean addItem(EntityPlayer player, ItemStack itemStack, boolean giveIfCreative)
	{
		if (!giveIfCreative && player.capabilities.isCreativeMode)
			return true;
		Config.syncFromConfig(player.getName());
		if (itemStack != null && !Config.blacklistNames.contains(itemStack.getItem().getItemStackDisplayName(itemStack).replace(" ", "_")))
		{
			boolean value = player.inventory.addItemStackToInventory(itemStack);
			if (value)
				player.inventoryContainer.detectAndSendChanges();
			return value;
		}
		else
			return false;
	}

	public static void spawnEntityItem(World world, BlockPos pos, ItemStack itemStack)
	{
		if (itemStack != null)
		{
			float f = 0.5F;
			double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
			double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
			double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, itemStack);
			entityitem.setDefaultPickupDelay();
			world.spawnEntityInWorld(entityitem);	
		}
	}
}

