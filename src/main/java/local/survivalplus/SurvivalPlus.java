package local.survivalplus;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(SurvivalPlus.ID)
public final class SurvivalPlus {
    public static final String ID = "survivalplus";
    private static final java.util.Map<java.util.UUID, Boolean> LAST_STATE = new java.util.HashMap<>();
    private static final String KEY = "survivalplus_enabled";

    public SurvivalPlus(IEventBus bus) {
        bus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::commands);
        NeoForge.EVENT_BUS.addListener(this::tick);
        NeoForge.EVENT_BUS.addListener(this::login);
        NeoForge.EVENT_BUS.addListener(this::respawn);
        NeoForge.EVENT_BUS.addListener(this::dimension);
        NeoForge.EVENT_BUS.addListener(this::logout);
    }

    public static boolean enabled(Player player) {
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getBoolean(KEY);
    }

    public static boolean active(ServerPlayer player) {
        return enabled(player) && player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL
            && !player.server.isDedicatedServer() && player.server.isSingleplayerOwner(player.getGameProfile());
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(StatePayload.TYPE, StatePayload.CODEC,
            (payload, context) -> context.enqueueWork(() -> ClientState.enabled = payload.enabled()));
    }

    private void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("survivalplus")
            .requires(source -> !source.getServer().isDedicatedServer())
            .executes(ctx -> change(ctx.getSource(), null))
            .then(Commands.literal("on").executes(ctx -> change(ctx.getSource(), true)))
            .then(Commands.literal("off").executes(ctx -> change(ctx.getSource(), false))));
    }

    private int change(CommandSourceStack source, Boolean requested) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!player.server.isSingleplayerOwner(player.getGameProfile())) {
            source.sendFailure(Component.literal("Команда доступна владельцу одиночного мира."));
            return 0;
        }
        boolean value = requested == null ? !enabled(player) : requested;
        var data = player.getPersistentData();
        var persisted = data.getCompound(Player.PERSISTED_NBT_TAG);
        persisted.putBoolean(KEY, value);
        data.put(Player.PERSISTED_NBT_TAG, persisted);
        update(player);
        source.sendSuccess(() -> Component.literal(value
            ? "Survival Plus включён: полёт и меню креатива."
            : "Survival Plus выключен."), false);
        return 1;
    }

    private void update(ServerPlayer player) {
        boolean value = active(player);
        LAST_STATE.put(player.getUUID(), value);
        var abilities = player.getAbilities();
        boolean vanillaFlight = player.isCreative() || player.isSpectator();
        abilities.mayfly = value || vanillaFlight;
        if (!abilities.mayfly) {
            abilities.flying = false;
            // Disabling in midair starts a fresh fall, rather than retaining old fall distance.
            player.fallDistance = 0;
        }
        player.onUpdateAbilities();
        PacketDistributor.sendToPlayer(player, new StatePayload(value));
    }

    private void tick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            boolean expected = active(player) || player.isCreative() || player.isSpectator();
            if (player.getAbilities().mayfly != expected || !java.util.Objects.equals(LAST_STATE.get(player.getUUID()), active(player))) update(player);
        }
    }
    private void logout(PlayerEvent.PlayerLoggedOutEvent e) { LAST_STATE.remove(e.getEntity().getUUID()); }
    private void login(PlayerEvent.PlayerLoggedInEvent e) { if (e.getEntity() instanceof ServerPlayer p) update(p); }
    private void respawn(PlayerEvent.PlayerRespawnEvent e) { if (e.getEntity() instanceof ServerPlayer p) update(p); }
    private void dimension(PlayerEvent.PlayerChangedDimensionEvent e) { if (e.getEntity() instanceof ServerPlayer p) update(p); }
}
