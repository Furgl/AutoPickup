package furgl.autoPickup.event;

import java.util.ArrayList;
import java.util.List;

import furgl.autoPickup.AutoPickup;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
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
		if (players.size() > 0 && event.world == players.get(0).field_70170_p && delays.set(0, delays.get(0)-1) == 1)
		{
			List<EntityItem> items = event.world.func_72872_a(EntityItem.class, aabb.get(0));
			for (EntityItem item : items)
				AutoPickup.addItem(players.get(0), item.func_92059_d(), true);
			players.remove(0);
			aabb.remove(0);
			delays.remove(0);
		}
	}
	
	/** Detect when mob killed and give drops to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingDropsEvent event)
	{
		if (!event.getEntity().field_70170_p.field_72995_K && event.getSource().func_76346_g() instanceof EntityPlayer)
			DelayedPickupEvent.setDelayedPickup((EntityPlayer) event.getSource().func_76346_g(), event.getEntity().field_70165_t, event.getEntity().field_70163_u, event.getEntity().field_70161_v);
	}
	
	/** Detect when player clicks jukebox and gives record to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(RightClickBlock event)
	{
		if (!event.getEntityPlayer().field_70170_p.field_72995_K)
			DelayedPickupEvent.setDelayedPickup(event.getEntityPlayer(), event.getPos().func_177958_n(), event.getPos().func_177956_o(), event.getPos().func_177952_p());
	}
	
	/** Detect when mob sheared.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityInteractSpecific event)
	{
		if (!event.getEntityPlayer().field_70170_p.field_72995_K)
			DelayedPickupEvent.setDelayedPickup(event.getEntityPlayer(), event.getTarget().field_70165_t, event.getTarget().field_70163_u, event.getTarget().field_70161_v);
	}

	/** Detect when blocks are broken and give drops to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(BlockEvent.BreakEvent event)
	{
		if (!event.getWorld().field_72995_K)
			DelayedPickupEvent.setDelayedPickup(event.getPlayer(), event.getPos().func_177958_n(), event.getPos().func_177956_o(), event.getPos().func_177952_p());
	}
	
	/** Detect when minecart container/item frame/painting/armor stand/boat destroyed and give contents to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(AttackEntityEvent event)
	{
		if (!event.getEntityPlayer().field_70170_p.field_72995_K)
			DelayedPickupEvent.setDelayedPickup(event.getEntityPlayer(), event.getTarget().field_70165_t, event.getTarget().field_70163_u, event.getTarget().field_70161_v);
	}
	
	/** Detect when experience is dropped and give to player.*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingExperienceDropEvent event)
	{
		if (!event.getEntity().field_70170_p.field_72995_K && event.getAttackingPlayer() != null)
		{
			if (MinecraftForge.EVENT_BUS.post(new PlayerPickupXpEvent(event.getAttackingPlayer(), new EntityXPOrb(event.getAttackingPlayer().field_70170_p, event.getAttackingPlayer().field_70165_t, event.getAttackingPlayer().field_70163_u, event.getAttackingPlayer().field_70161_v, event.getDroppedExperience())))) return;
			event.getAttackingPlayer().field_71090_bL = 2;
			event.getAttackingPlayer().field_70170_p.func_184133_a(event.getAttackingPlayer(), new BlockPos(event.getAttackingPlayer()), SoundEvents.field_187604_bf, SoundCategory.AMBIENT, 0.1F, 0.5F * ((event.getAttackingPlayer().field_70170_p.field_73012_v.nextFloat() - event.getAttackingPlayer().field_70170_p.field_73012_v.nextFloat()) * 0.7F + 1.8F));
			event.getAttackingPlayer().func_71023_q(event.getDroppedExperience());
			event.setDroppedExperience(0);
		}
	}
}
