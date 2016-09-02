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
	{																	//can only be called on client side
		if (Config.autoAdd && !event.getPlayer().field_70170_p.field_72995_K /* && Minecraft.getMinecraft().currentScreen instanceof GuiInventory*/)
		{
			Config.syncFromConfig(event.getPlayer().func_70005_c_());
			ItemStack stack = event.getEntityItem().func_92059_d();
			String name = stack.func_77973_b().func_77653_i(stack).replace(" ", "_");
			if (!Config.blacklistNames.contains(name))
			{
				Config.blacklistNames.add(name);
				event.getPlayer().func_145747_a(new TextComponentString("[AutoPickup] AutoAdd: Added "+name+" to blacklist.").func_150255_a(new Style().func_150238_a(TextFormatting.DARK_GREEN)));
				if (Config.firstAutoAdd)
				{
					event.getPlayer().func_145747_a(new TextComponentString("Tip: You can disable AutoAdd using /b AutoAdd false").func_150255_a(new Style().func_150217_b(true).func_150238_a(TextFormatting.RED)));
					Config.firstAutoAdd = false;
				}
				Config.syncToConfig(event.getPlayer().func_70005_c_());
			}
		}
	}
}
