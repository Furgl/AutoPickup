package furgl.autoPickup.event;

import java.util.ArrayList;
import java.util.List;

import furgl.autoPickup.AutoPickup;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
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
		delays.add(DELAY);
		players.add(player);
		aabb.add(new AxisAlignedBB(x-PICKUP_RADIUS, y-PICKUP_RADIUS, z-PICKUP_RADIUS, x+PICKUP_RADIUS, y+PICKUP_RADIUS, z+PICKUP_RADIUS));
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
			DelayedPickupEvent.setDelayedPickup(event.entityPlayer, event.pos.getX(), event.pos.getY(), event.pos.getZ());
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
			DelayedPickupEvent.setDelayedPickup(event.getPlayer(), event.pos.getX(), event.pos.getY(), event.pos.getZ());
	}
	
	/** Detect when minecart container/item frame/painting/armor stand/boat destroyed and give contents to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(AttackEntityEvent event)
	{
		if (!event.entityPlayer.worldObj.isRemote)
			DelayedPickupEvent.setDelayedPickup(event.entityPlayer, event.target.posX, event.target.posY, event.target.posZ);
	}
	
	/** Detect when experience is dropped and give to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingExperienceDropEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.getAttackingPlayer() != null)
		{
			if (MinecraftForge.EVENT_BUS.post(new PlayerPickupXpEvent(event.getAttackingPlayer(), new EntityXPOrb(event.getAttackingPlayer().worldObj, event.getAttackingPlayer().posX, event.getAttackingPlayer().posY, event.getAttackingPlayer().posZ, event.getDroppedExperience())))) return;
			event.getAttackingPlayer().xpCooldown = 2;
			event.getAttackingPlayer().worldObj.playSoundAtEntity(event.getAttackingPlayer(), "random.orb", 0.1F, 0.5F * ((event.getAttackingPlayer().worldObj.rand.nextFloat() - event.getAttackingPlayer().worldObj.rand.nextFloat()) * 0.7F + 1.8F));
			event.getAttackingPlayer().addExperience(event.getDroppedExperience());
			event.setDroppedExperience(0);
		}
	}
}
