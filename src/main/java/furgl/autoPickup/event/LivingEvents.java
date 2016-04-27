package furgl.autoPickup.event;

import furgl.autoPickup.AutoPickup;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
			if (event.source.getEntity() instanceof EntityPlayer && !(event.source.getEntity() instanceof FakePlayer) && event.source.getEntity() != null)
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
					for (int i=0; i<list.tagCount(); i++)//chest items
					{
						if (!AutoPickup.addItem(player, ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)), true))
							AutoPickup.spawnEntityItem(horse.worldObj, horse.getPosition(), ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
					}
					if (horseNbt.hasKey("ArmorItem"))//armor
					{
						if (!AutoPickup.addItem(player, ItemStack.loadItemStackFromNBT(horseNbt.getCompoundTag("ArmorItem")), true))
							AutoPickup.spawnEntityItem(horse.worldObj, horse.getPosition(), ItemStack.loadItemStackFromNBT(horseNbt.getCompoundTag("ArmorItem")));
					}
					if (horseNbt.hasKey("SaddleItem"))//saddle
					{
						if (!AutoPickup.addItem(player, ItemStack.loadItemStackFromNBT(horseNbt.getCompoundTag("SaddleItem")), true))
							AutoPickup.spawnEntityItem(horse.worldObj, horse.getPosition(), ItemStack.loadItemStackFromNBT(horseNbt.getCompoundTag("SaddleItem")));
					}
					if (horse.isChested())//chest
					{
						if (!AutoPickup.addItem(player, new ItemStack(Blocks.chest), true))
							AutoPickup.spawnEntityItem(horse.worldObj, horse.getPosition(), new ItemStack(Blocks.chest));
						horse.setChested(false);
					}
					for (int i=0; i<((horse.getHorseType() == 1 || horse.getHorseType() == 2) ? 17 : 2); i++)
					{
						if (i < 2)
							horse.replaceItemInInventory(i+400, null);
						else
							horse.replaceItemInInventory(i+498, null);
					}
				}
			}
		}
	}

	/** Detect when experience is dropped and give to player.
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(LivingExperienceDropEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.getAttackingPlayer() != null)
		{
			if (MinecraftForge.EVENT_BUS.post(new PlayerPickupXpEvent(event.getAttackingPlayer(), new EntityXPOrb(event.getAttackingPlayer().worldObj, event.getAttackingPlayer().posX, event.getAttackingPlayer().posY, event.getAttackingPlayer().posZ, event.getDroppedExperience())))) return;
			event.getAttackingPlayer().xpCooldown = 2;
			event.getAttackingPlayer().worldObj.playSoundAtEntity(event.getAttackingPlayer(), "random.orb", 0.1F, 0.5F * ((event.getAttackingPlayer().worldObj.rand.nextFloat() - event.getAttackingPlayer().worldObj.rand.nextFloat()) * 0.7F + 1.8F));
			event.getAttackingPlayer().addExperience(event.getDroppedExperience());
			event.setDroppedExperience(0);
		}
	}
}
