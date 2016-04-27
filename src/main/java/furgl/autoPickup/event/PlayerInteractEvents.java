package furgl.autoPickup.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import furgl.autoPickup.AutoPickup;
import net.minecraft.block.BlockJukebox;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

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
			if (event.action.equals(Action.RIGHT_CLICK_BLOCK) && event.world.getBlock(event.x, event.y, event.z) instanceof BlockJukebox)
			{
				BlockJukebox.TileEntityJukebox tileentityjukebox = (BlockJukebox.TileEntityJukebox)event.world.getTileEntity(event.x, event.y, event.z);
				//.getRecord()
				if (tileentityjukebox.func_145856_a() != null)
				{
					event.world.playAuxSFX(1005, event.x, event.y, event.z, 0);
					event.world.playRecord((String)null, event.y, event.z, event.x);
					AutoPickup.addItem(event.entityPlayer, tileentityjukebox.func_145856_a(), true); //spawning disc normally is handled elsewhere
				}
			}
		}
	}
}
