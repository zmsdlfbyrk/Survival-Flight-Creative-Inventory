package local.survivalplus.mixin;

import local.survivalplus.SurvivalPlus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerInventoryMixin {
    @Shadow public ServerPlayer player;
    @Redirect(method = "handleSetCreativeModeSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;isCreative()Z"))
    private boolean survivalplus$acceptCreativeInventory(ServerPlayerGameMode mode) {
        return mode.isCreative() || SurvivalPlus.inventoryActive(player);
    }
}
