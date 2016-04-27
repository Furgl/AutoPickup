package furgl.autoPickup.event;

import furgl.autoPickup.AutoPickup;
import net.minecraft.block.BlockJukebox;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerInteractEvents 
{
	/** Detect when jukebox is right clicked and give disc to player.
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(PlayerInteractEvent event)
	{
		if (!event.entityPlayer.worldObj.isRemote)
		{
			if (event.action.equals(Action.RIGHT_CLICK_BLOCK) && event.world.getBlockState(event.pos).getBlock() instanceof BlockJukebox)
			{
				BlockJukebox.TileEntityJukebox tileentityjukebox = (BlockJukebox.TileEntityJukebox)event.world.getTileEntity(event.pos);
				if (tileentityjukebox.getRecord() != null)
				{
					event.world.playAuxSFX(1005, event.pos, 0);
					event.world.playRecord(event.pos, (String)null);
					AutoPickup.addItem(event.entityPlayer, tileentityjukebox.getRecord(), true); //spawning disc normally is handled elsewhere
				}
			}
		}
	}
}
