package furgl.autoPickup;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
			if ((((ClientProxy)AutoPickup.proxy).ignoreBlacklist.isPressed() || ((ClientProxy)AutoPickup.proxy).ignoreBlacklist.isKeyDown()) != isPressed)
			{
				isPressed = ((ClientProxy)AutoPickup.proxy).ignoreBlacklist.isPressed() || ((ClientProxy)AutoPickup.proxy).ignoreBlacklist.isKeyDown();
				AutoPickup.network.sendToServer(new PacketIgnoreKey(isPressed));
			}
		}
	}
}
