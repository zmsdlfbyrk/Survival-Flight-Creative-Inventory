package local.survivalplus.mixin;

import local.survivalplus.ClientState;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
    private boolean survivalplus$openCreative(MultiPlayerGameMode mode) {
        return mode.hasInfiniteItems() || ClientState.enabled;
    }
}
