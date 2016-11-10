package furgl.autoPickup.event;

import furgl.autoPickup.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemTossEvents 
{
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ItemTossEvent event)
	{
		if (Config.autoAdd && !event.getPlayer().worldObj.isRemote)
		{
			Config.syncFromConfig(event.getPlayer().getName());
			ItemStack stack = event.getEntityItem().getEntityItem();
			String name = stack.getItem().getItemStackDisplayName(stack).replace(" ", "_");
			if (!Config.blacklistNames.contains(name))
			{
				Config.blacklistNames.add(name);
				event.getPlayer().addChatMessage(new TextComponentString("[AutoPickup] AutoAdd: Added "+name+" to blacklist.").setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
				if (Config.firstAutoAdd)
				{
					event.getPlayer().addChatMessage(new TextComponentString("Tip: You can disable AutoAdd using /b AutoAdd false").setStyle(new Style().setItalic(true).setColor(TextFormatting.RED)));
					Config.firstAutoAdd = false;
				}
				Config.syncToConfig(event.getPlayer().getName());
			}
		}
	}
}
