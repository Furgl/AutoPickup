package furgl.autoPickup.common.event;

import furgl.autoPickup.common.AutoPickup;
import furgl.autoPickup.common.config.Config;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityItemPickupEvents {
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityItemPickupEvent event) {
		if (!event.getItem().getEntityData().getBoolean("Fake Item")) {
			Config.syncFromConfig(event.getEntityPlayer().getName());
			if (!AutoPickup.key.isKeyDown(event.getEntityPlayer()) && 
					Config.blacklistNames.contains(event.getItem().getEntityItem().getItem().getItemStackDisplayName(event.getItem().getEntityItem()).replace(" ", "_"))) {
				event.getItem().setDefaultPickupDelay();
				event.setCanceled(true);		
			}
		}
	}
}