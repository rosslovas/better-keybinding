package rosco.minecraftmods.betterkeybinding;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Type;

public class BetterKeyBinding extends KeyBinding {

    public BetterKeyBinding(String translationKey, Type type, int code, String category) {
        super(translationKey, type, code, category);
    }

    private int timesPressed = 0;

    public int enqueuedPress = 0;

    public boolean wasPressed() {
        if (super.wasPressed()) {
            return true;
        } else if (this.timesPressed == 0) {
            return false;
        } else {
            --this.timesPressed;
            return true;
        }
    }

    public void onExtendedPress() {
        ++timesPressed;
    }

    // TODO: reset?

}
