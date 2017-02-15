package furgl.autoPickup.client.key;

import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.Maps;

import furgl.autoPickup.common.AutoPickup;
import furgl.autoPickup.common.packet.PacketIgnoreKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IgnoreKey {
	
	@SideOnly(Side.CLIENT)
	public KeyBinding ignoreBlacklist;
	/**True if key is pressed down*/
	public HashMap<UUID, Boolean> isKeyDown = Maps.newHashMap();

	public IgnoreKey() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public boolean isKeyDown(EntityPlayer player) {
		if (player != null)
			return isKeyDown.containsKey(player.getPersistentID()) ? isKeyDown.get(player.getPersistentID()) : false;
		return false;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void playerTick(ClientTickEvent event) {
		if (event.phase == Phase.END && Minecraft.getMinecraft().player != null) {
			UUID player = Minecraft.getMinecraft().player.getPersistentID();
			if (!isKeyDown.containsKey(player) || ignoreBlacklist.isKeyDown() != isKeyDown.get(player)) {
				isKeyDown.put(player, ignoreBlacklist.isKeyDown());
				AutoPickup.network.sendToServer(new PacketIgnoreKey(ignoreBlacklist.isKeyDown(), player));
			}
		}
	}
}