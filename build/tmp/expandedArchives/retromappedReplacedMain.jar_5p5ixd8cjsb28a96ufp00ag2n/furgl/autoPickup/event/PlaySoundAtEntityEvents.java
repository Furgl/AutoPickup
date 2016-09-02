package furgl.autoPickup.event;

import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlaySoundAtEntityEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlaySoundAtEntityEvent event)
	{//NO LONGER WORKS AS OF 1.9 - EVENT.GETENTITY() == NULL
		/*if (event.getEntity() instanceof EntityArrow && !event.getEntity().worldObj.isRemote && !event.getEntity().isDead)
		{
			EntityArrow arrow = (EntityArrow) event.getEntity();
			if (arrow.shootingEntity instanceof EntityPlayer && !(arrow.shootingEntity instanceof FakePlayer))
			{
				try
				{
					Field field = arrow.getClass().getDeclaredField("inTile");
					field.setAccessible(true);
					if (event.getSound() == SoundEvents.entity_arrow_hit && field.get(arrow) instanceof Block && arrow.canBePickedUp == EntityArrow.PickupStatus.ALLOWED)
					{
						if (AutoPickup.addItem((EntityPlayer) arrow.shootingEntity, new ItemStack(Items.arrow), false))
							arrow.setDead();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}*/
	}
}
