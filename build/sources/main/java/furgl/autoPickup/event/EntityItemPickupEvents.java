package furgl.autoPickup.event;

import furgl.autoPickup.Config;
import furgl.autoPickup.IgnoreKey;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityItemPickupEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityItemPickupEvent event)
	{
		Config.syncFromConfig(event.getEntityPlayer().getName());
		if (!IgnoreKey.isPressed && Config.blacklistNames.contains(event.getItem().getEntityItem().getItem().getItemStackDisplayName(event.getItem().getEntityItem()).replace(" ", "_")))
		{
			event.getItem().setDefaultPickupDelay();
			event.setCanceled(true);		
		}
	}
}
