package furgl.autoPickup.common.event;

import java.util.ArrayList;
import java.util.List;

import furgl.autoPickup.common.AutoPickup;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DelayedPickupEvent 
{
	private static final int DELAY = 1;
	private static final int PICKUP_RADIUS = 2;
	private static ArrayList<Integer> delays = new ArrayList<Integer>();
	private static ArrayList<EntityPlayer> players = new ArrayList<EntityPlayer>();
	private static ArrayList<AxisAlignedBB> aabb = new ArrayList<AxisAlignedBB>();

	public static void setDelayedPickup(EntityPlayer player, double x, double y, double z)
	{
		if (!(player instanceof FakePlayer)) {
			delays.add(DELAY);
			players.add(player);
			aabb.add(new AxisAlignedBB(x-PICKUP_RADIUS, y-PICKUP_RADIUS, z-PICKUP_RADIUS, x+PICKUP_RADIUS, y+PICKUP_RADIUS, z+PICKUP_RADIUS));
		}
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(TickEvent.WorldTickEvent event)
	{
		if (players.size() > 0 && event.world == players.get(0).world && delays.set(0, delays.get(0)-1) == 1)
		{
			List<EntityItem> items = event.world.getEntitiesWithinAABB(EntityItem.class, aabb.get(0));
			for (EntityItem item : items)
				AutoPickup.addItem(players.get(0), item.getEntityItem(), true);
			List<EntityXPOrb> xpOrbs = event.world.getEntitiesWithinAABB(EntityXPOrb.class, aabb.get(0));
			for (EntityXPOrb xp : xpOrbs)
				xp.setPosition(players.get(0).posX, players.get(0).posY, players.get(0).posZ);
			players.remove(0);
			aabb.remove(0);
			delays.remove(0);
		}
	}

	/** Detect when mob killed and give drops to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingDropsEvent event)
	{
		if (!event.getEntity().world.isRemote && event.getSource().getEntity() instanceof EntityPlayer)
			DelayedPickupEvent.setDelayedPickup((EntityPlayer) event.getSource().getEntity(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ);
	}

	/** Detect when player clicks jukebox and gives record to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(RightClickBlock event)
	{
		if (!event.getEntityPlayer().world.isRemote)
			DelayedPickupEvent.setDelayedPickup(event.getEntityPlayer(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
	}

	/** Detect when mob sheared.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityInteractSpecific event)
	{
		if (!event.getEntityPlayer().world.isRemote)
			DelayedPickupEvent.setDelayedPickup(event.getEntityPlayer(), event.getTarget().posX, event.getTarget().posY, event.getTarget().posZ);
	}

	/** Detect when blocks are broken and give drops to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(BlockEvent.BreakEvent event)
	{
		if (!event.getWorld().isRemote)
			DelayedPickupEvent.setDelayedPickup(event.getPlayer(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
	}

	/** Detect when minecart container/item frame/painting/armor stand/boat destroyed and give contents to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(AttackEntityEvent event)
	{
		if (!event.getEntityPlayer().world.isRemote)
			DelayedPickupEvent.setDelayedPickup(event.getEntityPlayer(), event.getTarget().posX, event.getTarget().posY, event.getTarget().posZ);
	}

	/** Detect when experience is dropped and give to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingExperienceDropEvent event)
	{
		if (!event.getEntity().world.isRemote && event.getAttackingPlayer() != null)
		{
			EntityXPOrb xp = new EntityXPOrb(event.getEntity().world, event.getAttackingPlayer().posX, event.getAttackingPlayer().posY, event.getAttackingPlayer().posZ, event.getDroppedExperience());
			event.getEntity().world.spawnEntity(xp);
			event.setDroppedExperience(0);
		}
	}
}
