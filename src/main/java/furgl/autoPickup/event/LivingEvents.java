package furgl.autoPickup.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import furgl.autoPickup.AutoPickup;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public class LivingEvents 
{
	/** Detect when mob killed and give drops to player.
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingDropsEvent event)
	{
		if (!event.entity.worldObj.isRemote)
		{
			if (event.source.getEntity() instanceof EntityPlayer && event.source.getEntity() != null)
			{
				EntityPlayer player = (EntityPlayer) event.source.getEntity();
				for (int i=0; i<event.drops.size(); i++)
				{
					if (!AutoPickup.addItem(player, event.drops.get(i).getEntityItem(), true))
						event.entity.entityDropItem(event.drops.get(i).getEntityItem(), 0);
				}
				if (event.entity instanceof EntityHorse)
				{
					EntityHorse horse = (EntityHorse) event.entity;
					NBTTagCompound horseNbt = new NBTTagCompound();
					horse.writeToNBT(horseNbt);
					NBTTagList list = horseNbt.getTagList("Items", 10);
					NBTTagList newList = new NBTTagList();
					for (int i=0; i<list.tagCount(); i++)//chest items
					{
						NBTTagCompound newNbt = new NBTTagCompound();
						newNbt.setByte("Slot", list.getCompoundTagAt(i).getByte("Slot"));
						new ItemStack(Blocks.air).writeToNBT(newNbt);
						if (!AutoPickup.addItem(player, ItemStack.loadItemStackFromNBT((NBTTagCompound) list.getCompoundTagAt(i)), true))
							AutoPickup.spawnEntityItem(horse.worldObj, horse.posX, horse.posY, horse.posZ, ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
						newList.appendTag(newNbt);
					}
					horseNbt.setTag("Items", newList);
					if (horseNbt.hasKey("ArmorItem"))//armor
					{
						NBTTagCompound newNbt = horseNbt.getCompoundTag("ArmorItem");
						if (!AutoPickup.addItem(player, ItemStack.loadItemStackFromNBT(horseNbt.getCompoundTag("ArmorItem")), true))
							AutoPickup.spawnEntityItem(horse.worldObj, horse.posX, horse.posY, horse.posZ, ItemStack.loadItemStackFromNBT(horseNbt.getCompoundTag("ArmorItem")));
						newNbt.setByte("Count", (byte) 0);
						horseNbt.removeTag("ArmorItem");
						horseNbt.setTag("ArmorItem", newNbt);
					}
					if (horseNbt.hasKey("SaddleItem"))//saddle
					{
						NBTTagCompound newNbt = horseNbt.getCompoundTag("SaddleItem");
						if (!AutoPickup.addItem(player, ItemStack.loadItemStackFromNBT(horseNbt.getCompoundTag("SaddleItem")), true))
							AutoPickup.spawnEntityItem(horse.worldObj, horse.posX, horse.posY, horse.posZ, ItemStack.loadItemStackFromNBT(horseNbt.getCompoundTag("SaddleItem")));
						newNbt.setByte("Count", (byte) 0);
						horseNbt.removeTag("SaddleItem");
						horseNbt.setTag("SaddleItem", newNbt);
					}
					if (horse.isChested())//chest
					{
						if (!AutoPickup.addItem(player, new ItemStack(Blocks.chest), true))
							AutoPickup.spawnEntityItem(horse.worldObj, horse.posX, horse.posY, horse.posZ, new ItemStack(Blocks.chest));
					}
					horse.readEntityFromNBT(horseNbt);
					horse.setChested(false);
				}
			}
		}
	}
}
