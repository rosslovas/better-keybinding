package rosco.minecraftmods.betterkeybinding;

import java.util.ArrayList;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;

public class BetterKeyBindingClient implements ClientModInitializer {

	private static MinecraftClient client = MinecraftClient.getInstance();

	private static boolean enqueuedKeyBindingPress = false;

	private static ArrayList<BetterKeyBinding> registeredKeyBindings = new ArrayList<>();

	private static ArrayList<Class<? extends Screen>> excluded = new ArrayList<Class<? extends Screen>>();

	private static boolean reiInstalled = false;

	private static void tryAddExcludedClassByName(String classFullName) {
		try {
			excluded.add(Class.forName(classFullName).asSubclass(Screen.class));
		} catch (ClassNotFoundException e) {
		}
	}

	private static boolean isNotExcluded(Screen screen) {
		for (var excludedScreenType : excluded) {
			if (excludedScreenType.isInstance(screen)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void onInitializeClient() {
		reiInstalled = FabricLoader.getInstance().isModLoaded("roughlyenoughitems");

		tryAddExcludedClassByName("dan200.computercraft.client.gui.ComputerScreenBase");

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (enqueuedKeyBindingPress) {
				enqueuedKeyBindingPress = false;
				for (var binding : registeredKeyBindings) {
					if (binding.enqueuedPress > 1) {
						enqueuedKeyBindingPress = true;
					}
					if (binding.enqueuedPress > 0) {
						--binding.enqueuedPress;
					}
				}
			}
		});
	}

	public static void registerKeyBinding(BetterKeyBinding keyBinding) {
		registeredKeyBindings.add(keyBinding);
	}

	public static void onKey(long window, int keyCode, int scanCode, int action, int modifiers) {
		// 1 or 2: Press or repeat
		if (action == 1 || action == 2) {
			for (var binding : registeredKeyBindings) {
				if (binding.matchesKey(keyCode, scanCode)) {
					if (client.currentScreen != null &&
							client.currentScreen instanceof HandledScreen &&
							isNotExcluded(client.currentScreen)) {
						if (client.currentScreen.passEvents || reiInstalled) {
							enqueuedKeyBindingPress = true;
							binding.enqueuedPress = 2;
						} else {
							binding.onExtendedPress();
						}
					}
				}
			}
		}
	}

	public static void popKeyBindingPress() {
		if (enqueuedKeyBindingPress) {
			for (var binding : registeredKeyBindings) {
				if (binding.enqueuedPress > 0) {
					binding.enqueuedPress = 0;
					binding.onExtendedPress();
				}
			}
			enqueuedKeyBindingPress = false;
		}
	}

	public static void debug(String message) {
		client.inGameHud.addChatMessage(
				MessageType.SYSTEM, new LiteralText(message), client.player.getUuid());
	}
}
