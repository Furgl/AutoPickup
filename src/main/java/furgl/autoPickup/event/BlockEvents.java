package furgl.autoPickup.event;

import java.util.Random;

import furgl.autoPickup.AutoPickup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
			if (event.state.getBlock() instanceof net.minecraftforge.common.IShearable)
			{
				net.minecraftforge.common.IShearable block = (net.minecraftforge.common.IShearable)event.state.getBlock();
				if (block.isShearable(event.getPlayer().getCurrentEquippedItem(), event.getPlayer().worldObj, event.pos) && event.getPlayer().getCurrentEquippedItem() != null && event.getPlayer().getCurrentEquippedItem().getItem() instanceof ItemShears)
				{	
					java.util.List<ItemStack> drops = block.onSheared(event.getPlayer().getCurrentEquippedItem(), event.getPlayer().worldObj, event.pos, net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantment.fortune.effectId, event.getPlayer().getCurrentEquippedItem()));
					for (ItemStack stack : drops)
					{
						if (!AutoPickup.addItem(event.getPlayer(), stack, true))
						{
							AutoPickup.spawnEntityItem(event.world, event.pos, stack);		
						}
						event.world.setBlockToAir(event.pos);
					}
				}
				//If 2 block flower or top of 2 block grass
				else if (event.state.getBlock() instanceof BlockDoublePlant)
				{
					IBlockState state;
					if (event.world.getBlockState(event.pos.down()).getBlock() instanceof BlockDoublePlant)
						state = event.world.getBlockState(event.pos.down());
					else
						state = event.world.getBlockState(event.pos);
					BlockDoublePlant.EnumPlantType enumplanttype = (BlockDoublePlant.EnumPlantType)state.getValue(BlockDoublePlant.VARIANT);
					//If holding shears and is grass or fern (top)
					if (event.getPlayer().getCurrentEquippedItem() != null && event.getPlayer().getCurrentEquippedItem().getItem() instanceof ItemShears && (enumplanttype == BlockDoublePlant.EnumPlantType.GRASS || enumplanttype == BlockDoublePlant.EnumPlantType.FERN))
					{
						int i = (enumplanttype == BlockDoublePlant.EnumPlantType.GRASS ? BlockTallGrass.EnumType.GRASS : BlockTallGrass.EnumType.FERN).getMeta();
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Blocks.tallgrass, 2, i), false))
							Block.spawnAsEntity(event.world, event.pos, new ItemStack(Blocks.tallgrass, 2, i));
						if (event.world.getBlockState(event.pos.down()).getBlock() instanceof BlockDoublePlant)
							event.world.setBlockToAir(event.pos.down());
						else
							event.world.setBlockToAir(event.pos);
					}
					if (enumplanttype != BlockDoublePlant.EnumPlantType.GRASS && enumplanttype != BlockDoublePlant.EnumPlantType.FERN)
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Item.getItemFromBlock(event.state.getBlock()), 1, enumplanttype.getMeta()), false))
							event.state.getBlock().dropBlockAsItem(event.world, event.pos, state, 0);
						if (event.world.getBlockState(event.pos.down()).getBlock() instanceof BlockDoublePlant)
							event.world.setBlockToAir(event.pos.down());
						else
							event.world.setBlockToAir(event.pos);
					}
					//If not holding shears and is double tall grass or ferns
					else if (!(event.getPlayer().getCurrentEquippedItem() != null && event.getPlayer().getCurrentEquippedItem().getItem() instanceof ItemShears))
					{
						if (event.world.rand.nextInt(8) == 0)
						{
							if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Items.wheat_seeds), false))
								AutoPickup.spawnEntityItem(event.world, event.pos, new ItemStack(Items.wheat_seeds));
						}
						if (event.world.getBlockState(event.pos.down()).getBlock() instanceof BlockDoublePlant)
							event.world.setBlockToAir(event.pos.down());
						event.world.setBlockToAir(event.pos);
						if (event.world.getBlockState(event.pos.up()).getBlock() instanceof BlockDoublePlant)
							event.world.setBlockToAir(event.pos.up());
					}
				}
			}
			if (event.state.getBlock() instanceof BlockContainer)
			{
				TileEntity tileentity = event.world.getTileEntity(event.pos);
				if (tileentity instanceof IInventory)
				{
					IInventory inventory = (IInventory) tileentity;
					for (int i=0; i<inventory.getSizeInventory(); i++)
					{
						if (!AutoPickup.addItem(event.getPlayer(), inventory.getStackInSlot(i), true))
							AutoPickup.spawnEntityItem(event.world, event.pos, inventory.getStackInSlot(i));
					}
					if (event.state.getBlock() instanceof BlockBrewingStand)
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Items.brewing_stand), false))
							AutoPickup.spawnEntityItem(event.world, event.pos, new ItemStack(Items.brewing_stand));
					}
					else
					{
						if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Item.getItemFromBlock(event.state.getBlock())), false))
							AutoPickup.spawnEntityItem(event.world, event.pos, new ItemStack(Item.getItemFromBlock(event.state.getBlock())));
					}
					event.world.setBlockToAir(event.pos);
				}

				else if (event.state.getBlock() instanceof BlockBanner)
				{
					ItemStack itemstack = new ItemStack(Items.banner, 1, ((TileEntityBanner)tileentity).getBaseColor());
					if (tileentity instanceof TileEntityBanner)
					{
						NBTTagCompound nbttagcompound = new NBTTagCompound();
						tileentity.writeToNBT(nbttagcompound);
						nbttagcompound.removeTag("x");
						nbttagcompound.removeTag("y");
						nbttagcompound.removeTag("z");
						nbttagcompound.removeTag("id");
						itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
					}
					if (!AutoPickup.addItem(event.getPlayer(), itemstack, false))
						event.state.getBlock().dropBlockAsItem(event.world, event.pos, event.state, 0);
					event.world.setBlockToAir(event.pos);
				}
			}
			if (event.state.getBlock() instanceof BlockPistonExtension)
			{
				EnumFacing enumfacing = ((EnumFacing)event.state.getValue(BlockPistonExtension.FACING)).getOpposite();
				BlockPos pos = event.pos.offset(enumfacing);
				if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Item.getItemFromBlock(event.world.getBlockState(pos).getBlock())), false))
					AutoPickup.spawnEntityItem(event.world, event.pos, new ItemStack(Item.getItemFromBlock(event.world.getBlockState(pos).getBlock())));	
				event.world.setBlockToAir(pos);
			}
			if (event.state.getBlock() instanceof BlockDoor && event.state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER)
			{
				if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(event.world.getBlockState(event.pos.down()).getBlock().getItemDropped(event.world.getBlockState(event.pos.down()), new Random(), 0)), false))
					event.world.getBlockState(event.pos.down()).getBlock().dropBlockAsItem(event.world, event.pos, event.world.getBlockState(event.pos.down()), 0);
				event.world.setBlockToAir(event.pos.down());
			}
			if (event.state.getBlock() instanceof BlockBed && event.state.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD)
			{
				BlockPos pos = event.pos.offset(((EnumFacing)event.state.getValue(BlockDirectional.FACING)).getOpposite());
				if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(event.world.getBlockState(pos).getBlock().getItemDropped(event.world.getBlockState(pos), new Random(), 0)), false))
					event.world.getBlockState(pos).getBlock().dropBlockAsItem(event.world, event.pos, event.world.getBlockState(pos), 0);
				event.world.setBlockToAir(pos);
			}
			if (event.state.getBlock() instanceof BlockJukebox)
			{
				TileEntity tileentity = event.world.getTileEntity(event.pos);
				BlockJukebox.TileEntityJukebox tileentityjukebox = (BlockJukebox.TileEntityJukebox)tileentity;
				event.world.playAuxSFX(1005, event.pos, 0);
				event.world.playRecord(event.pos, (String)null);
				if (!AutoPickup.addItem(event.getPlayer(), tileentityjukebox.getRecord(), true))
					AutoPickup.spawnEntityItem(event.world, event.pos, tileentityjukebox.getRecord());
				tileentityjukebox.setRecord((ItemStack)null);
			}
			if (event.state.getBlock() instanceof BlockSkull)
			{
				TileEntity tileentity = event.world.getTileEntity(event.pos);
				if (tileentity instanceof TileEntitySkull)
				{
					TileEntitySkull tileentityskull = (TileEntitySkull)tileentity;
					ItemStack itemstack = new ItemStack(Items.skull, 1, tileentityskull.getSkullType());
					if (tileentityskull.getSkullType() == 3 && tileentityskull.getPlayerProfile() != null)
					{
						itemstack.setTagCompound(new NBTTagCompound());
						NBTTagCompound nbttagcompound = new NBTTagCompound();
						NBTUtil.writeGameProfile(nbttagcompound, tileentityskull.getPlayerProfile());
						itemstack.getTagCompound().setTag("SkullOwner", nbttagcompound);
					}
					if (!AutoPickup.addItem(event.getPlayer(), itemstack, false))
						event.state.getBlock().dropBlockAsItem(event.world, event.pos, event.state, 0);
				}
				event.world.removeTileEntity(event.pos);
				event.world.setBlockToAir(event.pos);
			}
			if (event.state.getBlock() instanceof BlockCactus)
			{
				BlockPos pos = event.pos;
				int cactiAbove = 0;
				while (event.world.getBlockState(pos.up()).getBlock() instanceof BlockCactus)
				{
					cactiAbove++;
					pos = pos.up();
					if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Item.getItemFromBlock(event.state.getBlock())), false))
						AutoPickup.spawnEntityItem(event.world, event.pos, new ItemStack(Item.getItemFromBlock(event.state.getBlock())));	
				}
				for (int i=cactiAbove; i>0; i--)
				{
					event.world.setBlockToAir(pos);
					pos = pos.down();
				}
			}
			if (event.state.getBlock() instanceof BlockReed)
			{
				BlockPos pos = event.pos;
				int reedsAbove = 0;
				while (event.world.getBlockState(pos.up()).getBlock() instanceof BlockReed)
				{
					reedsAbove++;
					pos = pos.up();
					if (!AutoPickup.addItem(event.getPlayer(), new ItemStack(Items.reeds), false))
						AutoPickup.spawnEntityItem(event.world, event.pos, new ItemStack(Items.reeds));	
				}
				for (int i=reedsAbove; i>0; i--)
				{
					event.world.setBlockToAir(pos);
					pos = pos.down();
				}
			}
			event.getPlayer().addExperience(event.getExpToDrop());
			event.setExpToDrop(0);
		}
	}
}
