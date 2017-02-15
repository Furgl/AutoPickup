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

public class PacketIgnoreKey implements IMessage {
	
	private boolean isKeyPressed;
	private UUID player;

	public PacketIgnoreKey() {}

	public PacketIgnoreKey(boolean isKeyPressed, UUID player) {
		this.isKeyPressed = isKeyPressed;
		this.player = player;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.isKeyPressed = buf.readBoolean();
		this.player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.isKeyPressed);
		ByteBufUtils.writeUTF8String(buf, player.toString());
	}

	public static class Handler implements IMessageHandler<PacketIgnoreKey, IMessage> {
		@Override
		public IMessage onMessage(final PacketIgnoreKey packet, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() {
					AutoPickup.key.isKeyDown.put(packet.player, packet.isKeyPressed);
				}
			});
			return null;
		}
	}
}