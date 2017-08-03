package furgl.autoPickup.common.event;

import furgl.autoPickup.common.AutoPickup;
import furgl.autoPickup.common.config.Config;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityItemPickupEvents {
	@SubscribeEvent
	public void onEvent(EntityItemPickupEvent event) {
		if (!event.getItem().getEntityData().getBoolean("Fake Item")) {
			Config.syncFromConfig(event.getEntityPlayer().getName());
			if (!AutoPickup.keys.ignore(event.getEntityPlayer()) && 
					Config.blacklistNames.contains(event.getItem().getItem().getItem().getItemStackDisplayName(event.getItem().getItem()).replace(" ", "_"))) {
				event.getItem().setDefaultPickupDelay();
				event.setCanceled(true);		
			}
		}
	}
}