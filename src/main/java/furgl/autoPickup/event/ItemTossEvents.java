package furgl.autoPickup.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import furgl.autoPickup.CommandBlacklist;
import furgl.autoPickup.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.item.ItemTossEvent;

public class ItemTossEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ItemTossEvent event)
	{
		if (Config.autoAdd && event.player.worldObj.isRemote && Minecraft.getMinecraft().currentScreen instanceof GuiInventory)
		{
			Config.syncFromConfig(event.player.getDisplayName());
			ItemStack stack = event.entityItem.getEntityItem();
			String name = stack.getItem().getItemStackDisplayName(stack).replace(" ", "_");
			if (CommandBlacklist.getDisplayNames().contains(name) && !Config.blacklistNames.contains(name))
			{
				Config.blacklistNames.add(name);
				event.player.addChatMessage(new ChatComponentTranslation("[AutoPickup] AutoAdd: Added "+name+" to blacklist.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
				if (Config.firstAutoAdd)
				{
					event.player.addChatMessage(new ChatComponentTranslation("Tip: You can disable AutoAdd using /b AutoAdd false").setChatStyle(new ChatStyle().setItalic(true).setColor(EnumChatFormatting.RED)));
					Config.firstAutoAdd = false;
				}
				Config.syncToConfig(event.player.getDisplayName());
			}
		}
	}
}
