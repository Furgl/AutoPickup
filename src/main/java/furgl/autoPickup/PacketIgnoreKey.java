package furgl.autoPickup;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

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
			IgnoreKey.isPressed = packet.pressed;
			return null;
		}
	}
}