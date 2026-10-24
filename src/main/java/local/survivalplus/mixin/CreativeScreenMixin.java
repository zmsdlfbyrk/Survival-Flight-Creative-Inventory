package local.survivalplus.mixin;

import local.survivalplus.ClientState;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeScreenMixin {
    @Redirect(method = {"init", "containerTick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
    private boolean survivalplus$keepCreativeOpen(MultiPlayerGameMode mode) {
        return mode.hasInfiniteItems() || ClientState.enabled;
    }
}
