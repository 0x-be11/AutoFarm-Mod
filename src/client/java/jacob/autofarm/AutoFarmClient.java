package jacob.autofarm;

import jacob.autofarm.manager.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import java.io.IOException;

import static com.mojang.text2speech.Narrator.LOGGER;

public class AutoFarmClient implements ClientModInitializer {
	private static final Minecraft client = Minecraft.getInstance();
	private long lastHit;
	private boolean isEating = false;
	private int previousSlot = -1;
	private long startEatingTime = 0;

	@Override
	public void onInitializeClient() {
		Keybinds.register();

		// Register the tick event
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (AutoFarm.enabled && client.player != null) {
				checkHealth();
				if (!isEating) {
					checkAutoAttack();
				}
				checkHunger();
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (Keybinds.openMenuKey.wasPressed()) {
				toggleMod();
			}
		});

        try {
            ConfigManager.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("AutoFarm Client Initialized!");
	}

	public static void toggleMod() {
		AutoFarm.enabled = !AutoFarm.enabled;
		if (client.player != null) {
			client.player.sendSystemMessage(Component.nullToEmpty("AutoFarm " + (AutoFarm.enabled ? "enabled" : "disabled")));
		}
	}

	private void checkHealth() {
		if (client.player.getHealth() <= Config.logoutHealth) {
			client.player.connection.getConnection().disconnect(Component.nullToEmpty("AutoLogout triggered, you were at " + Config.logoutHealth +  " HP!"));
			AutoFarm.enabled = false;
		}
	}

	private void checkAutoAttack() {
		long currentTime = System.currentTimeMillis();
		long timeSinceAttack = currentTime - lastHit;
		if (Config.swingDelay == 0) return;

		if (client.player.getAttackStrengthScale(0) >= 1.0F && timeSinceAttack >= Config.swingDelay * 50) {
				simulateAttack();
				lastHit = currentTime;
			}
		}
	private void simulateAttack() {
		if (client.player == null || client.gameMode == null) return;

		if (client.hitResult instanceof EntityHitResult entityHit) {
			Entity target = entityHit.getEntity();

			if (AutoFarmMenu.hostileMob && target instanceof AgeableMob || target instanceof Player){
				return;
			}
			client.gameMode.attack(client.player, entityHit.getEntity());

			client.player.swing(InteractionHand.MAIN_HAND);
		}
	}

	private void checkHunger() {
		if (client.player == null) return;

		if (isEating) {
			if (System.currentTimeMillis() - startEatingTime >= 1600) {
				finishEating();
				return;
			}

			client.options.keyUse.setDown(true);
			return;
		}

		if (client.player.getFoodData().getFoodLevel() <= Config.eatHunger) {
			int foodSlot = findFoodInHotbar();
			if (foodSlot != -1) {
				startEating(foodSlot);
			}
		}
	}

	private void startEating(int foodSlot) {
		if (client.player == null) return;

		previousSlot = client.player.getInventory().getSelectedSlot();
		client.player.getInventory().setSelectedSlot(foodSlot);
		isEating = true;
		startEatingTime = System.currentTimeMillis();
		client.options.keyUse.setDown(true);

		// Log for debugging
		LOGGER.info("Started eating from slot " + foodSlot);
	}

	private void finishEating() {
		if (client.player == null) return;

		client.options.keyUse.setDown(false);

		if (previousSlot != -1) {
			client.player.getInventory().setSelectedSlot(previousSlot);
			previousSlot = -1;
		}

		isEating = false;

		// Log for debugging
		LOGGER.info("Finished eating");
	}

	private int findFoodInHotbar() {
		if (client.player == null) return -1;

		for (int i = 0; i < 9; i++) {
			ItemStack stack = client.player.getInventory().getItem(i);
			if (!stack.isEmpty() && stack.getItem().components().has(DataComponents.FOOD)) {
				return i;
			}
		}
		return -1;
	}
}
