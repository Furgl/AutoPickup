package furgl.autoPickup.client;

import org.lwjgl.input.Keyboard;

import furgl.autoPickup.common.AutoPickup;
import furgl.autoPickup.common.CommonProxy;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {	
	public void init()	{
		AutoPickup.key.ignoreBlacklist = new KeyBinding("Ignore Blacklist", Keyboard.KEY_LMENU, "Auto Pickup");
		ClientRegistry.registerKeyBinding(AutoPickup.key.ignoreBlacklist);
	}
}