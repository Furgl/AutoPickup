package furgl.autoPickup.event;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import furgl.autoPickup.AutoPickup;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;

public class DelayedPickupEvent 
{
	private static final int DELAY = 1;
	private static final int PICKUP_RADIUS = 2;
	private static ArrayList<Integer> delays = new ArrayList<Integer>();
	private static ArrayList<EntityPlayer> players = new ArrayList<EntityPlayer>();
	private static ArrayList<AxisAlignedBB> aabb = new ArrayList<AxisAlignedBB>();

	public static void setDelayedPickup(EntityPlayer player, double x, double y, double z)
	{
		delays.add(DELAY);
		players.add(player);
		aabb.add(AxisAlignedBB.getBoundingBox(x-PICKUP_RADIUS, y-PICKUP_RADIUS, z-PICKUP_RADIUS, x+PICKUP_RADIUS, y+PICKUP_RADIUS, z+PICKUP_RADIUS));
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(TickEvent.WorldTickEvent event)
	{
		if (players.size() > 0 && event.world == players.get(0).worldObj && delays.set(0, delays.get(0)-1) == 1)
		{
			List<EntityItem> items = event.world.getEntitiesWithinAABB(EntityItem.class, aabb.get(0));
			for (EntityItem item : items)
				AutoPickup.addItem(players.get(0), item.getEntityItem(), true);
			players.remove(0);
			aabb.remove(0);
			delays.remove(0);
		}
	}
	
	/** Detect when mob killed and give drops to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingDropsEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.source.getEntity() instanceof EntityPlayer)
			DelayedPickupEvent.setDelayedPickup((EntityPlayer) event.source.getEntity(), event.entity.posX, event.entity.posY, event.entity.posZ);
	}
	
	/** Detect when player clicks jukebox and gives record to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerInteractEvent event)
	{
		if (!event.entityPlayer.worldObj.isRemote && event.action == Action.RIGHT_CLICK_BLOCK)
			DelayedPickupEvent.setDelayedPickup(event.entityPlayer, event.x, event.y, event.z);
	}
	
	/** Detect when mob sheared.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityInteractEvent event)
	{
		if (!event.entityPlayer.worldObj.isRemote)
			DelayedPickupEvent.setDelayedPickup(event.entityPlayer, event.target.posX, event.target.posY, event.target.posZ);
	}

	/** Detect when blocks are broken and give drops to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(BlockEvent.BreakEvent event)
	{
		if (!event.world.isRemote)
			DelayedPickupEvent.setDelayedPickup(event.getPlayer(), event.x, event.y, event.z);
	}
	
	/** Detect when minecart container/item frame/painting/armor stand/boat destroyed and give contents to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(AttackEntityEvent event)
	{
		if (!event.entityPlayer.worldObj.isRemote)
			DelayedPickupEvent.setDelayedPickup(event.entityPlayer, event.target.posX, event.target.posY, event.target.posZ);
	}
}
