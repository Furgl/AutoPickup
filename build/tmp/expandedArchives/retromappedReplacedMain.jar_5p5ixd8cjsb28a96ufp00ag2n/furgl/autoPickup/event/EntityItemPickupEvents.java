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
		Config.syncFromConfig(event.getEntityPlayer().func_70005_c_());
		if (!IgnoreKey.isPressed && Config.blacklistNames.contains(event.getItem().func_92059_d().func_77973_b().func_77653_i(event.getItem().func_92059_d()).replace(" ", "_")))
		{
			event.getItem().func_174869_p();
			event.setCanceled(true);		
		}
	}
}
