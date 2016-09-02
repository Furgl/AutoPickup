package furgl.autoPickup;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketIgnoreKey implements IMessage
{
	protected boolean pressed;

	public PacketIgnoreKey() 
	{

	}

	public PacketIgnoreKey(boolean pressed) 
	{
		this.pressed = pressed;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.pressed = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeBoolean(pressed);
	}

	public static class Handler implements IMessageHandler<PacketIgnoreKey, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketIgnoreKey packet, final MessageContext ctx) 
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().field_147369_b.field_70170_p;
			mainThread.func_152344_a(new Runnable() 
			{
				@Override
				public void run() 
				{
					IgnoreKey.isPressed = packet.pressed;
				}
			});
			return null;
		}
	}
}
