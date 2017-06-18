package furgl.autoPickup.common.event;

import java.util.ArrayList;
import java.util.UUID;

import furgl.autoPickup.common.config.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemTossEvents 
{
	/**Resets every session*/
	private static ArrayList<UUID> playersGivenTip = new ArrayList<UUID>();
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(ItemTossEvent event) {
		if (Config.autoAdd && !event.getPlayer().world.isRemote) {
			Config.syncFromConfig(event.getPlayer().getName());
			ItemStack stack = event.getEntityItem().getItem();
			String name = stack.getItem().getItemStackDisplayName(stack).replace(" ", "_");
			if (!Config.blacklistNames.contains(name)) {
				Config.blacklistNames.add(name);
				event.getPlayer().sendMessage(new TextComponentString("[AutoPickup] AutoAdd: Added "+name+" to blacklist.").setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
				if (!playersGivenTip.contains(event.getPlayer().getPersistentID()))	{
					event.getPlayer().sendMessage(new TextComponentString("Tip: You can disable AutoAdd using /b AutoAdd false").setStyle(new Style().setItalic(true).setColor(TextFormatting.RED)));
					playersGivenTip.add(event.getPlayer().getPersistentID());
				}
				Config.syncToConfig(event.getPlayer().getName());
			}
		}
	}
}