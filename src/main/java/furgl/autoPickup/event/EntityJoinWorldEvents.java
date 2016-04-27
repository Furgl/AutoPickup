package furgl.autoPickup.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

public class EntityJoinWorldEvents 
{
	/** Detect when experience is dropped and give to closest player.
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote)
		{
			if (event.entity instanceof EntityXPOrb)
			{
				EntityPlayer player;
				if ((player = event.world.getClosestPlayerToEntity(event.entity, -1)) != null)
				{
					if (MinecraftForge.EVENT_BUS.post(new PlayerPickupXpEvent(player, (EntityXPOrb) event.entity))) return;
					player.xpCooldown = 2;
					event.world.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((event.world.rand.nextFloat() - event.world.rand.nextFloat()) * 0.7F + 1.8F));
					player.addExperience(((EntityXPOrb)event.entity).xpValue);
					event.entity.setDead();
					event.setCanceled(true);
				}
			}
		}
	}
}
