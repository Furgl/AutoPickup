package furgl.autoPickup;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	public KeyBinding ignoreBlacklist = new KeyBinding("Ignore Blacklist", Keyboard.KEY_LMENU, "Auto Pickup");
	
	public void init()
	{
		ClientRegistry.registerKeyBinding(ignoreBlacklist);
	}
}
