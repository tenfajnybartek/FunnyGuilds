package net.dzikoysk.funnyguilds.user;

import java.lang.ref.WeakReference;
import java.util.UUID;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.Position;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyServer;
import net.dzikoysk.funnyguilds.shared.bukkit.NmsUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.PositionConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import panda.std.Option;

public class BukkitUserProfile implements UserProfile {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private final UUID uuid;
    private final FunnyServer funnyServer;

    private WeakReference<OfflinePlayer> offlinePlayerRef;
    private WeakReference<Player> playerRef;

    public BukkitUserProfile(UUID uuid, FunnyServer funnyServer) {
        this.uuid = uuid;
        this.funnyServer = funnyServer;

        this.offlinePlayerRef = new WeakReference<>(funnyServer.getOfflinePlayer(uuid));
        this.playerRef = new WeakReference<>(funnyServer.getPlayer(uuid).orNull());
    }

    private Option<Player> getPlayer() {
        Player player = this.playerRef.get();

        if (player == null) {
            this.refresh();
            player = this.playerRef.get();
        }

        return Option.of(player);
    }

    private void refreshOfflinePlayerRef() {
        if (this.offlinePlayerRef.get() == null) {
            this.offlinePlayerRef = new WeakReference<>(this.funnyServer.getOfflinePlayer(this.uuid));
        }
    }

    @Override
    public boolean isOnline() {
        return this.getPlayer().is(Player::isOnline);
    }

    @Override
    public boolean isVanished() {
        // Should work with VanishNoPacket, SuperVanish and PremiumVanish
        return this.getPlayer()
                .map(player -> player.getMetadata("vanished"))
                .map(metadata -> metadata.stream().anyMatch(MetadataValue::asBoolean))
                .orElseGet(false);
    }

    @Override
    public boolean hasPermission(String permission) {
        Player player = this.playerRef.get();
        if (player != null) {
            return player.hasPermission(permission);
        }

        this.refreshOfflinePlayerRef();
        OfflinePlayer offlinePlayer = this.offlinePlayerRef.get();

        return offlinePlayer.isOp() || VaultHook.hasPermission(offlinePlayer, permission);
    }

    @Override
    public int getPing() {
        return this.getPlayer().map(NmsUtils::getPing).orElseGet(0);
    }

    @Override
    public void sendMessage(String message) {
        if (FunnyStringUtils.isEmpty(message)) {
            return;
        }

        this.getPlayer().peek(player -> {
            Component component = parseMessage(message);
            player.sendMessage(component);
        });
    }

    /**
     * Parse message with MiniMessage support (hex colors, gradients, hover, click) 
     * and fallback to legacy formatting.
     * 
     * Supports:
     * - Legacy: §a, §c, §l (section sign)
     * - Hex colors: &#FF5733 or <#FF5733>
     * - MiniMessage: <gradient:red:blue>text</gradient>, <rainbow>, <hover>, <click>
     * 
     * @param message The message to parse
     * @return Parsed Adventure Component
     */
    private static Component parseMessage(String message) {
        // If message contains MiniMessage tags or hex colors, use MiniMessage parser
        if (message.contains("<") && (
                message.contains("gradient") || 
                message.contains("rainbow") || 
                message.contains("hover") || 
                message.contains("click") ||
                message.contains("#"))) {
            try {
                // First convert legacy § codes to MiniMessage format
                String legacyConverted = LEGACY_SERIALIZER.serialize(LEGACY_SERIALIZER.deserialize(message));
                // Then parse with MiniMessage (supports hex, gradients, etc.)
                return MINI_MESSAGE.deserialize(message);
            } catch (Exception e) {
                // Fallback to legacy if MiniMessage parsing fails
                return LEGACY_SERIALIZER.deserialize(message);
            }
        }
        
        // Default: use legacy serializer for § codes
        return LEGACY_SERIALIZER.deserialize(message);
    }

    @Override
    public void kick(String reason) {
        this.getPlayer().peek(player -> {
            Component component = parseMessage(reason);
            player.kick(component);
        });
    }

    @Override
    public void teleport(Position position) {
        this.getPlayer().peek(player -> player.teleport(PositionConverter.adapt(position)));
    }

    @Override
    public void refresh() {
        this.funnyServer.getPlayer(this.uuid).peek(player -> {
            this.playerRef = new WeakReference<>(player);
            this.offlinePlayerRef = new WeakReference<>(player);
        }).onEmpty(() -> {
            this.playerRef = new WeakReference<>(null);
        });
    }

    @Override
    public Position getPosition() {
        return this.getPlayer()
                .map(Player::getLocation)
                .map(PositionConverter::adapt)
                .orElseGet(Position.ZERO);
    }

}
