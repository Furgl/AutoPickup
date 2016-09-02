package furgl.autoPickup;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IgnoreKey 
{
	public static boolean isPressed;

	public IgnoreKey()
	{
		
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event)
	{
		if (event.side == Side.CLIENT && event.phase == Phase.START)
		{
			if ((((ClientProxy)AutoPickup.proxy).ignoreBlacklist.isPressed() || ((ClientProxy)AutoPickup.proxy).ignoreBlacklist.getIsKeyPressed()) != isPressed)
			{
				isPressed = ((ClientProxy)AutoPickup.proxy).ignoreBlacklist.isPressed() || ((ClientProxy)AutoPickup.proxy).ignoreBlacklist.getIsKeyPressed();
				AutoPickup.network.sendToServer(new PacketIgnoreKey(isPressed));
			}
		}
	}
}
