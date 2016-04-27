package furgl.autoPickup.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import furgl.autoPickup.Config;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class EntityItemPickupEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityItemPickupEvent event)
	{
		Config.syncFromConfig(event.entityPlayer.getDisplayName());
		if (Config.blacklistNames.contains(event.item.getEntityItem().getItem().getItemStackDisplayName(event.item.getEntityItem()).replace(" ", "_")))
		{
			event.item.delayBeforeCanPickup = 10;
			event.setCanceled(true);		
		}
	}
}
