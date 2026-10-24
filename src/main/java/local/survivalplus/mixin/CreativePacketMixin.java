package local.survivalplus.mixin;

import local.survivalplus.ClientState;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiPlayerGameMode.class)
public abstract class CreativePacketMixin {
    @Redirect(method = {"handleCreativeModeItemAdd", "handleCreativeModeItemDrop"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameType;isCreative()Z"))
    private boolean survivalplus$allowInventoryPackets(GameType mode) {
        return mode.isCreative() || (mode == GameType.SURVIVAL && ClientState.enabled);
    }
}
