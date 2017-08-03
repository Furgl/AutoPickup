package furgl.autoPickup.client;

import org.lwjgl.input.Keyboard;

import furgl.autoPickup.common.AutoPickup;
import furgl.autoPickup.common.CommonProxy;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {	
	public void init()	{
		AutoPickup.keys.ignoreBlacklist = new KeyBinding("Ignore Blacklist", Keyboard.KEY_LMENU, AutoPickup.MODNAME);
		AutoPickup.keys.disableAutoPickup = new KeyBinding("Disable Auto Pickup", Keyboard.KEY_X, AutoPickup.MODNAME);
		ClientRegistry.registerKeyBinding(AutoPickup.keys.ignoreBlacklist);
		ClientRegistry.registerKeyBinding(AutoPickup.keys.disableAutoPickup);
	}
}