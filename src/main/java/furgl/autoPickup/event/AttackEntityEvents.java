package furgl.autoPickup.event;

import furgl.autoPickup.AutoPickup;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AttackEntityEvents 
{
	/** Detect when minecart container/item frame/painting/armor stand/boat destroyed and give contents to player.
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(AttackEntityEvent event)
	{
		if (!event.entityPlayer.worldObj.isRemote)
		{
			if (event.target instanceof EntityItemFrame)
			{
				EntityItemFrame frame = (EntityItemFrame) event.target;
				if (frame.getDisplayedItem() != null)
				{
					if (!AutoPickup.addItem(event.entityPlayer, frame.getDisplayedItem(), true))
						frame.entityDropItem(frame.getDisplayedItem(), 0.0F);
					frame.setDisplayedItem((ItemStack)null);
					event.setCanceled(true);
				}
				else if (frame.getDisplayedItem() == null)
				{
					if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Items.item_frame), false))
						frame.entityDropItem(new ItemStack(Items.item_frame), 0.0F);
					frame.setDead();
				}
			}

			else if (event.target instanceof EntityPainting)
			{
				EntityPainting painting = (EntityPainting) event.target;
				if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Items.painting), false))
					painting.entityDropItem(new ItemStack(Items.painting), 0.0F);
				painting.setDead();
			}

			else if (event.target instanceof EntityArmorStand)
			{
				EntityArmorStand armorStand = (EntityArmorStand) event.target;
				for (int i = 0; i < armorStand.getInventory().length; ++i)
				{
					if (armorStand.getInventory()[i] != null && armorStand.getInventory()[i].stackSize > 0)
					{     
						if (!AutoPickup.addItem(event.entityPlayer, armorStand.getInventory()[i], true))
							armorStand.entityDropItem(armorStand.getInventory()[i], 0.0F);
						armorStand.getInventory()[i] = null;
					}
				}
				if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Items.armor_stand), false))
					armorStand.entityDropItem(new ItemStack(Items.armor_stand), 0.0F);
				armorStand.setDead();
			}

			else if (event.target instanceof EntityMinecart)
			{
				int damage;
				if (event.entityPlayer.getHeldItem() != null)
					damage = event.entityPlayer.getHeldItem().getMaxDamage();
				else 
					damage = 10;			
				if (event.target instanceof EntityMinecartContainer)
				{
					EntityMinecartContainer minecart = (EntityMinecartContainer) event.target;
					if (minecart.getDataWatcher().getWatchableObjectFloat(19) + damage >= 40)
					{
						for (int i=0; i<minecart.getSizeInventory(); i++)
						{
							if (minecart.getStackInSlot(i) != null && !AutoPickup.addItem(event.entityPlayer, minecart.getStackInSlot(i), true))
								event.entity.entityDropItem(minecart.getStackInSlot(i), 0);
						}
						if (minecart.getMinecartType() == EnumMinecartType.CHEST)
						{
							if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Item.getItemFromBlock(Blocks.chest)), false))
								minecart.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.chest)), 0.0F);
						}
						else if (minecart.getMinecartType() == EnumMinecartType.HOPPER)
						{
							if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Item.getItemFromBlock(Blocks.hopper)), false))
								minecart.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.hopper)), 0.0F);
						}
						if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Items.minecart), false))
							minecart.entityDropItem(new ItemStack(Items.minecart), 0.0F);
						minecart.setDead();
					}
				}
				else
				{
					EntityMinecart minecart = (EntityMinecart) event.target;
					if (minecart.getDataWatcher().getWatchableObjectFloat(19) + damage >= 40)
					{
						if (minecart.getMinecartType() == EnumMinecartType.TNT)
						{
							if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Item.getItemFromBlock(Blocks.tnt)), false))
								minecart.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.tnt)), 0.0F);
						}
						else if (minecart.getMinecartType() == EnumMinecartType.FURNACE)
						{
							if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Item.getItemFromBlock(Blocks.furnace)), false))
								minecart.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.furnace)), 0.0F);
						}
						if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Items.minecart), false))
							minecart.entityDropItem(new ItemStack(Items.minecart), 0.0F);
						minecart.setDead();
					}				
				}			
			}

			else if (event.target instanceof EntityBoat)
			{
				EntityBoat boat = (EntityBoat) event.target;
				int damage;
				if (event.entityPlayer.getHeldItem() != null)
					damage = event.entityPlayer.getHeldItem().getMaxDamage();
				else 
					damage = 10;
				if (boat.getDataWatcher().getWatchableObjectFloat(19) + damage >= 40)
				{
					if (!AutoPickup.addItem(event.entityPlayer, new ItemStack(Items.boat), false))
						boat.entityDropItem(new ItemStack(Items.boat), 0.0F);
					boat.setDead();
				}
			}
		}
	}
}
