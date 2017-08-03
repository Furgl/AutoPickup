package furgl.autoPickup.common.packet;

import java.util.UUID;

import furgl.autoPickup.common.AutoPickup;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncKeys implements IMessage {

	private String key;
	private boolean isKeyPressed;
	private UUID player;

	public PacketSyncKeys() {}

	public PacketSyncKeys(String key, boolean isKeyPressed, UUID player) {
		this.key = key;
		this.isKeyPressed = isKeyPressed;
		this.player = player;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.key = ByteBufUtils.readUTF8String(buf);
		this.isKeyPressed = buf.readBoolean();
		this.player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, key);
		buf.writeBoolean(isKeyPressed);
		ByteBufUtils.writeUTF8String(buf, player.toString());
	}

	public static class Handler implements IMessageHandler<PacketSyncKeys, IMessage> {
		@Override
		public IMessage onMessage(final PacketSyncKeys packet, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() {
					if (packet.key.equalsIgnoreCase("ignore"))
						AutoPickup.keys.ignore.put(packet.player, packet.isKeyPressed);
					else if (packet.key.equalsIgnoreCase("disable"))
						AutoPickup.keys.disable.put(packet.player, packet.isKeyPressed);
				}
			});
			return null;
		}
	}
}