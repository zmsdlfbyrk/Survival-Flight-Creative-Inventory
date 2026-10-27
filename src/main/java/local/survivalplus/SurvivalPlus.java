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
    private static final java.util.Map<java.util.UUID, FeatureState> LAST_STATE = new java.util.HashMap<>();
    private static final String KEY = "survivalplus_enabled";

    private static final String FLIGHT_KEY = "survivalplus_flight";
    private static final String INVENTORY_KEY = "survivalplus_inventory";

    public SurvivalPlus(IEventBus bus) {
        bus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::commands);
        NeoForge.EVENT_BUS.addListener(this::tick);
        NeoForge.EVENT_BUS.addListener(this::login);
        NeoForge.EVENT_BUS.addListener(this::respawn);
        NeoForge.EVENT_BUS.addListener(this::dimension);
        NeoForge.EVENT_BUS.addListener(this::logout);
    }

    private static FeatureState savedState(Player player) {
        var persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        boolean legacy = persisted.getBoolean(KEY);
        return new FeatureState(
            persisted.contains(FLIGHT_KEY) ? persisted.getBoolean(FLIGHT_KEY) : legacy,
            persisted.contains(INVENTORY_KEY) ? persisted.getBoolean(INVENTORY_KEY) : legacy);
    }

    private static FeatureState activeState(ServerPlayer player) {
        boolean allowed = player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL
            && !player.server.isDedicatedServer() && player.server.isSingleplayerOwner(player.getGameProfile());
        return allowed ? savedState(player) : new FeatureState(false, false);
    }

    public static boolean inventoryActive(ServerPlayer player) {
        return activeState(player).inventory();
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("2").playToClient(StatePayload.TYPE, StatePayload.CODEC,
            (payload, context) -> context.enqueueWork(() -> ClientState.inventoryEnabled = payload.inventoryEnabled()));
    }

    private void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("survivalplus")
            .requires(source -> !source.getServer().isDedicatedServer())
            .executes(ctx -> change(ctx.getSource(), null, null))
            .then(Commands.literal("on").executes(ctx -> change(ctx.getSource(), null, true)))
            .then(Commands.literal("off").executes(ctx -> change(ctx.getSource(), null, false)))
            .then(featureCommand("flight"))
            .then(featureCommand("inventory")));
    }

    private com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> featureCommand(String feature) {
        return Commands.literal(feature)
            .executes(ctx -> change(ctx.getSource(), feature, null))
            .then(Commands.literal("on").executes(ctx -> change(ctx.getSource(), feature, true)))
            .then(Commands.literal("off").executes(ctx -> change(ctx.getSource(), feature, false)));
    }

    private int change(CommandSourceStack source, String feature, Boolean requested) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!player.server.isSingleplayerOwner(player.getGameProfile())) {
            source.sendFailure(Component.literal("Команда доступна владельцу одиночного мира."));
            return 0;
        }
        FeatureState next = savedState(player).change(feature, requested);
        var data = player.getPersistentData();
        var persisted = data.getCompound(Player.PERSISTED_NBT_TAG);
        persisted.putBoolean(FLIGHT_KEY, next.flight());
        persisted.putBoolean(INVENTORY_KEY, next.inventory());
        persisted.remove(KEY);
        data.put(Player.PERSISTED_NBT_TAG, persisted);
        update(player);
        source.sendSuccess(() -> Component.literal("Survival Plus: полёт — "
            + (next.flight() ? "включён" : "выключен") + ", меню креатива — "
            + (next.inventory() ? "включено" : "выключено") + "."), false);
        return 1;
    }

    private void update(ServerPlayer player) {
        FeatureState state = activeState(player);
        LAST_STATE.put(player.getUUID(), state);
        var abilities = player.getAbilities();
        boolean vanillaFlight = player.isCreative() || player.isSpectator();
        boolean hadFlight = abilities.mayfly;
        abilities.mayfly = state.flight() || vanillaFlight;
        if (!abilities.mayfly) {
            abilities.flying = false;
            // Disabling in midair starts a fresh fall, rather than retaining old fall distance.
            if (hadFlight) player.fallDistance = 0;
        }
        player.onUpdateAbilities();
        PacketDistributor.sendToPlayer(player, new StatePayload(state.inventory()));
    }

    private void tick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FeatureState state = activeState(player);
            boolean expected = state.flight() || player.isCreative() || player.isSpectator();
            if (player.getAbilities().mayfly != expected || !java.util.Objects.equals(LAST_STATE.get(player.getUUID()), state)) update(player);
        }
    }
    private void logout(PlayerEvent.PlayerLoggedOutEvent e) { LAST_STATE.remove(e.getEntity().getUUID()); }
    private void login(PlayerEvent.PlayerLoggedInEvent e) { if (e.getEntity() instanceof ServerPlayer p) update(p); }
    private void respawn(PlayerEvent.PlayerRespawnEvent e) { if (e.getEntity() instanceof ServerPlayer p) update(p); }
    private void dimension(PlayerEvent.PlayerChangedDimensionEvent e) { if (e.getEntity() instanceof ServerPlayer p) update(p); }
}
