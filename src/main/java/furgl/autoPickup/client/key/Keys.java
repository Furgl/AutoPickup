package furgl.autoPickup.client.key;

import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.Maps;

import furgl.autoPickup.common.AutoPickup;
import furgl.autoPickup.common.packet.PacketSyncKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Keys {
	
	@SideOnly(Side.CLIENT)
	public KeyBinding ignoreBlacklist;
	@SideOnly(Side.CLIENT)
	public KeyBinding disableAutoPickup;
	/**True if key is pressed down*/
	public HashMap<UUID, Boolean> ignore = Maps.newHashMap();
	/**True if key is pressed down*/
	public HashMap<UUID, Boolean> disable = Maps.newHashMap();

	public Keys() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public boolean ignore(EntityPlayer player) {
		if (player != null)
			return ignore.containsKey(player.getPersistentID()) ? ignore.get(player.getPersistentID()) : false;
		return false;
	}
	
	public boolean disable(EntityPlayer player) {
		if (player != null)
			return disable.containsKey(player.getPersistentID()) ? disable.get(player.getPersistentID()) : false;
		return false;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void playerTick(ClientTickEvent event) {
		if (event.phase == Phase.END && Minecraft.getMinecraft().thePlayer != null) {
			UUID player = Minecraft.getMinecraft().thePlayer.getPersistentID();
			if (!ignore.containsKey(player) || ignoreBlacklist.isKeyDown() != ignore.get(player)) {
				ignore.put(player, ignoreBlacklist.isKeyDown());
				AutoPickup.network.sendToServer(new PacketSyncKeys("ignore", ignoreBlacklist.isKeyDown(), player));
			}
		}
		
		if (event.phase == Phase.END && Minecraft.getMinecraft().thePlayer != null) {
			UUID player = Minecraft.getMinecraft().thePlayer.getPersistentID();
			if (!disable.containsKey(player) || disableAutoPickup.isKeyDown() != disable.get(player)) {
				disable.put(player, disableAutoPickup.isKeyDown());
				AutoPickup.network.sendToServer(new PacketSyncKeys("disable", disableAutoPickup.isKeyDown(), player));
			}
		}
	}
}