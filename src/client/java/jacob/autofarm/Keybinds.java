package jacob.autofarm;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    //private static final String CATEGORY = "AutoFarm";
	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("autofarm", "main"));
    public static KeyMapping openMenuKey;

    public static void register() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "Open Menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                client.setScreen(new AutoFarmMenu());
            }
        });
    }
}
