package furgl.autoPickup.event;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import furgl.autoPickup.AutoPickup;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.Facing;
import net.minecraftforge.event.world.BlockEvent;

public class BlockEvents 
{
	/** Detect when blocks are broken and give drops to player.
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(BlockEvent.HarvestDropsEvent event)
	{
		if (!event.world.isRemote)
		{
			if (event.harvester != null)
			{
				for (int i=0; i<event.drops.size(); i++)
				{
					AutoPickup.addItem(event.harvester, event.drops.get(i), false);
				}
			}
		}
	}

	/** Detect when shearable blocks/block with inventories/blocks with experience/jukebox/skull/door/bed 
	 *  are broken and give contents to player.
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(BlockEvent.BreakEvent event)
	{
		if (!event.world.isRemote)
		{
			if (event.block instanceof net.minecraftforge.common.IShearable)
			{
				net.minecraftforge.common.IShearable block = (net.minecraftforge.common.IShearable)event.block;
				if (block.isShearable(event.getPlayer().getCurrentEquippedItem(), event.getPlayer().worldObj, event.x, event.y, event.z) && event.getPlayer().getCurrentEquippedItem() != null && event.getPlayer().getCurrentEquippedItem().getItem() instanceof ItemShears)
				{	
					java.util.List<ItemStack> drops = block.onSheared(event.getPlayer().getCurrentEquippedItem(), event.getPlayer().worldObj, event.x, event.y, event.z, net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantment.fortune.effectId, event.getPlayer().getCurrentEquippedItem()));
					for (ItemStack stack : drops)
					{
						if (!AutoPickup.addItem(event.getPlayer(), stack, true))
						{
							AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, stack);		
						}
						event.world.setBlockToAir(event.x, event.y, event.z);
					}
				}
				else if (event.block instanceof BlockDoublePlant)
				{
					//Values: SUNFLOWER=0 LILAC=1 GRASS=2 FERNS=3 ROSES=4 PEONY=5
					int value = event.block.getDamageValue(event.world, event.x, event.y, event.z);
					if (value != 2 && value != 3)
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Blocks.double_plant, 1, value), false))
							AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, new ItemStack(Blocks.double_plant, 1, value));
					}
					else if (event.getPlayer().getCurrentEquippedItem() != null && event.getPlayer().getCurrentEquippedItem().getItem() instanceof ItemShears)
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Blocks.tallgrass, 2, value-1), false))
							AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, new ItemStack(Blocks.tallgrass, 2, value-1));
					}
					else if (event.world.rand.nextInt(8) == 0)
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Items.wheat_seeds), false))
							AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, new ItemStack(Items.wheat_seeds));
					}
					if (event.world.getBlock(event.x, event.y-1, event.z) instanceof BlockDoublePlant)
						event.world.setBlockToAir(event.x, event.y-1, event.z);
					else 
						event.world.setBlockToAir(event.x, event.y, event.z);
				}
			}
			if (event.block instanceof BlockContainer)
			{
				TileEntity tileentity = event.world.getTileEntity(event.x, event.y, event.z);
				if (tileentity instanceof IInventory)
				{
					IInventory inventory = (IInventory) tileentity;
					for (int i=0; i<inventory.getSizeInventory(); i++)
					{
						if (!AutoPickup.addItem(event.getPlayer(), inventory.getStackInSlot(i), true))
							AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, inventory.getStackInSlot(i));
					}
					if (event.block instanceof BlockBrewingStand)
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Items.brewing_stand), false))
							AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, new ItemStack(Items.brewing_stand));
					}
					else
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Item.getItemFromBlock(event.block)), false))
							AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, new ItemStack(Item.getItemFromBlock(event.block)));
					}
					event.world.setBlockToAir(event.x, event.y, event.z);
				}
			}
			if (event.block instanceof BlockPistonExtension)
			{
				if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(((BlockPistonExtension)event.block).getItem(event.world, event.x, event.y, event.z)), false))
				{
					AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, new ItemStack(((BlockPistonExtension)event.block).getItem(event.world, event.x, event.y, event.z)));	
				}
				int i = BlockPistonExtension.getDirectionMeta(event.blockMetadata);
				event.world.setBlockToAir(event.x - Facing.offsetsXForSide[i], event.y - Facing.offsetsYForSide[i], event.z - Facing.offsetsZForSide[i]);
			}
			if (event.block instanceof BlockDoor)
			{
				Item item = ((BlockDoor)event.block).getItemDropped(event.blockMetadata, new Random(), 0);
				if (item == null)//if top of door
				{
					item = ((BlockDoor)event.block).getItemDropped(event.world.getBlockMetadata(event.x, event.y-1, event.z), new Random(), 0);
					if (!event.getPlayer().capabilities.isCreativeMode)
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(item), false))
							event.block.dropBlockAsItem(event.world, event.x, event.y-1, event.z, event.world.getBlockMetadata(event.x, event.y-1, event.z), 0);
					}
					event.world.setBlockToAir(event.x, event.y-1, event.z);
				}
				else //if bottom of door
				{
					if (!event.getPlayer().capabilities.isCreativeMode)
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(item), false))
							event.block.dropBlockAsItem(event.world, event.x, event.y, event.z, event.world.getBlockMetadata(event.x, event.y, event.z), 0);				
					}
				}
				event.world.setBlockToAir(event.x, event.y, event.z);
			}
			if (event.block instanceof BlockBed && event.block.isBedFoot(event.world, event.x, event.y, event.z))//if head of bed (foot works)
			{
				if (!event.getPlayer().capabilities.isCreativeMode)
				{
					if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Items.bed), false))
						AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, new ItemStack(Items.bed));				
				}
				switch (event.block.getBedDirection(event.world, event.x, event.y, event.z))
				{
				case 0:
					event.world.setBlockToAir(event.x, event.y, event.z-1);
					break;
				case 1:
					event.world.setBlockToAir(event.x+1, event.y, event.z);
					break;
				case 2:
					event.world.setBlockToAir(event.x, event.y, event.z+1);
					break;
				case 3:
					event.world.setBlockToAir(event.x-1, event.y, event.z);
					break;
				}
				event.world.setBlockToAir(event.x, event.y, event.z);
			}

			if (event.block instanceof BlockJukebox)
			{
				TileEntity tileentity = event.world.getTileEntity(event.x, event.y, event.z);
				BlockJukebox.TileEntityJukebox tileentityjukebox = (BlockJukebox.TileEntityJukebox)tileentity;
				event.world.playAuxSFX(1005, event.x, event.y, event.z, 0);
				event.world.playRecord((String)null, event.y, event.z, event.x);
				if (!AutoPickup.addItem(event.getPlayer(), tileentityjukebox.func_145856_a(), true))
					AutoPickup.spawnEntityItem(event.world, event.x, event.y, event.z, tileentityjukebox.func_145856_a());
				tileentityjukebox.func_145857_a((ItemStack)null);
			}
			if (event.block instanceof BlockSkull)
			{
				TileEntity tileentity = event.world.getTileEntity(event.x, event.y, event.z);
				if (tileentity instanceof TileEntitySkull)
				{
					TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
					ItemStack itemstack = new ItemStack(Items.skull, 1, tileentityskull.func_145904_a());
					if (tileentityskull.func_145904_a() == 3 && tileentityskull.func_152108_a() != null)
					{
						itemstack.setTagCompound(new NBTTagCompound());
						NBTTagCompound nbttagcompound = new NBTTagCompound();
						NBTUtil.func_152460_a(nbttagcompound, tileentityskull.func_152108_a());
						itemstack.getTagCompound().setTag("SkullOwner", nbttagcompound);
					}
					if (!AutoPickup.addItem(event.getPlayer(), itemstack, false))
						event.block.dropBlockAsItem(event.world, event.x, event.y, event.z, event.blockMetadata, 0);
				}
				event.world.removeTileEntity(event.x, event.y, event.z);
				event.world.setBlockToAir(event.x, event.y, event.z);
			}
			if (event.block instanceof BlockCactus)
			{
				int y = event.y;
				int cactiAbove = 0;
				while (event.world.getBlock(event.x, y+1, event.z) instanceof BlockCactus)
				{
					cactiAbove++;
					y++;
					if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Item.getItemFromBlock(event.block)), false))
						AutoPickup.spawnEntityItem(event.world, event.x, y, event.z, new ItemStack(Item.getItemFromBlock(event.block)));	
				}
				for (int i=cactiAbove; i>0; i--)
				{
					event.world.setBlockToAir(event.x, y, event.z);
					y--;
				}
			}
			if (event.block instanceof BlockReed)
			{
				int y = event.y;
				int reedsAbove = 0;
				while (event.world.getBlock(event.x, y+1, event.z) instanceof BlockReed)
				{
					reedsAbove++;
					y++;
					if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Items.reeds), false))
						AutoPickup.spawnEntityItem(event.world, event.x, y, event.z, new ItemStack(Items.reeds));	
				}
				for (int i=reedsAbove; i>0; i--)
				{
					event.world.setBlockToAir(event.x, y, event.z);
					y--;
				}
			}
			event.getPlayer().addExperience(event.getExpToDrop());
			event.setExpToDrop(0);
		}
	}
}
