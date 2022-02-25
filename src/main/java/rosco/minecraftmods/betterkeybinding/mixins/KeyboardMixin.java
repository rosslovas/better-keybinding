package rosco.minecraftmods.betterkeybinding.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import rosco.minecraftmods.betterkeybinding.BetterKeyBindingClient;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow
    private MinecraftClient client;

    @Inject(method = "onKey(JIIII)V", at = @At("HEAD"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
        BetterKeyBindingClient.onKey(window, key, scancode, action, modifiers);
    }

    @Redirect(method = "method_1458", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;charTyped(CI)Z"))
    private static boolean onCharTyped1(Element screen, char codePoint, int modifiers) {
        if (!screen.charTyped(codePoint, modifiers)) {
            BetterKeyBindingClient.popKeyBindingPress();
            return false;
        }
        return true;
    }

    @Redirect(method = "method_1473", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Element;charTyped(CI)Z"))
    private static boolean onCharTyped2(Element screen, char codePoint, int modifiers) {
        if (!screen.charTyped(codePoint, modifiers)) {
            BetterKeyBindingClient.popKeyBindingPress();
            return false;
        }
        return true;
    }
}
