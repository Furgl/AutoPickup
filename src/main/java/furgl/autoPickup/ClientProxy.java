package furgl.autoPickup;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class ClientProxy extends CommonProxy
{
	public KeyBinding ignoreBlacklist = new KeyBinding("Ignore Blacklist", Keyboard.KEY_LMENU, "Auto Pickup");
	
	public void init()
	{
		ClientRegistry.registerKeyBinding(ignoreBlacklist);
	}
}
