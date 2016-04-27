package furgl.autoPickup.event;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import furgl.autoPickup.AutoPickup;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class EntityInteractEvents 
{
	/** Detect when sheep sheared and give wool to player.
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(EntityInteractEvent event)
	{
		if (!event.entityPlayer.worldObj.isRemote)
		{
			ItemStack itemstack = event.entityPlayer.inventory.getCurrentItem();
			if (event.target instanceof EntitySheep && itemstack != null && itemstack.getItem() == Items.shears && !((EntitySheep) event.target).getSheared() && !((EntitySheep) event.target).isChild())
			{
				((EntitySheep) event.target).setSheared(true);
				Random rand = new Random();
				int i = 1 + rand.nextInt(3);
				for (int j = 0; j < i; ++j)
				{
					if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, ((EntitySheep) event.target).getFleeceColor()), true))
						event.target.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, ((EntitySheep) event.target).getFleeceColor()), 0);
				}
				itemstack.damageItem(1, event.entityPlayer);
				((EntitySheep) event.target).playSound("mob.sheep.shear", 1.0F, 1.0F);
			}
			if (event.target instanceof EntityMooshroom && itemstack != null && itemstack.getItem() == Items.shears && !((EntityMooshroom) event.target).isChild())
			{
				for (int j = 0; j < 5; ++j)
				{
					if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Blocks.red_mushroom), true))
						event.target.entityDropItem(new ItemStack(Blocks.red_mushroom), 1);
				}
				itemstack.damageItem(1, event.entityPlayer);
				((EntityMooshroom) event.target).playSound("mob.sheep.shear", 1.0F, 1.0F);
				EntityCow entitycow = new EntityCow(event.target.worldObj);
				entitycow.setLocationAndAngles(event.target.posX, event.target.posY, event.target.posZ, event.target.rotationYaw, event.target.rotationPitch);
				entitycow.setHealth(((EntityMooshroom) event.target).getHealth());
				entitycow.renderYawOffset = ((EntityMooshroom) event.target).renderYawOffset;
				event.target.worldObj.spawnEntityInWorld(entitycow);
				event.target.setDead();
				event.setCanceled(true);
			}
		}
	}
}
