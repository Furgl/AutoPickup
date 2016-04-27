package furgl.autoPickup.event;

import furgl.autoPickup.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemTossEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ItemTossEvent event)
	{																	//can only be called on client side
		if (Config.autoAdd && !event.player.worldObj.isRemote /* && Minecraft.getMinecraft().currentScreen instanceof GuiInventory*/)
		{
			Config.syncFromConfig(event.player.getName());
			ItemStack stack = event.entityItem.getEntityItem();
			String name = stack.getItem().getItemStackDisplayName(stack).replace(" ", "_");
			if (/*CommandBlacklist.getDisplayNames().contains(name) && */!Config.blacklistNames.contains(name))
			{
				Config.blacklistNames.add(name);
				event.player.addChatMessage(new ChatComponentTranslation("[AutoPickup] AutoAdd: Added "+name+" to blacklist.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
				if (Config.firstAutoAdd)
				{
					event.player.addChatMessage(new ChatComponentTranslation("Tip: You can disable AutoAdd using /b AutoAdd false").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.RED)));
					Config.firstAutoAdd = false;
				}
				Config.syncToConfig(event.player.getName());
			}
		}
	}
}
