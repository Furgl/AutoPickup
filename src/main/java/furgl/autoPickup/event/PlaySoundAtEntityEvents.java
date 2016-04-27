package furgl.autoPickup.event;

import java.lang.reflect.Field;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import furgl.autoPickup.AutoPickup;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

public class PlaySoundAtEntityEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlaySoundAtEntityEvent event)
	{
		if (event.entity instanceof EntityArrow && !event.entity.worldObj.isRemote && !event.entity.isDead)
		{
			EntityArrow arrow = (EntityArrow) event.entity;
			if (arrow.shootingEntity instanceof EntityPlayer && !(arrow.shootingEntity instanceof FakePlayer))
			{
				try
				{
					Field field = arrow.getClass().getDeclaredField("field_145790_g");
					field.setAccessible(true);
					if (event.name.equals("random.bowhit") && field.get(arrow) instanceof Block && arrow.canBePickedUp == 1)
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
		}
	}
}
