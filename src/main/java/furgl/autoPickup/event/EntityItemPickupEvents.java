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
		Config.syncFromConfig(event.entityPlayer.getName());
		if (!IgnoreKey.isPressed && Config.blacklistNames.contains(event.item.getEntityItem().getItem().getItemStackDisplayName(event.item.getEntityItem()).replace(" ", "_")))
		{
			event.item.setDefaultPickupDelay();
			event.setCanceled(true);		
		}
	}
}
