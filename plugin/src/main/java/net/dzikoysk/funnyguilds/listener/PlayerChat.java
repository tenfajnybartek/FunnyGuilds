package net.dzikoysk.funnyguilds.listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildChatEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildChatEvent.Type;
import net.dzikoysk.funnyguilds.event.guild.GuildPreChatEvent;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.permission.GenericGuildPermissions;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermission;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionChecker;
import net.dzikoysk.funnyguilds.rank.DefaultTops;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class PlayerChat extends AbstractFunnyListener {

    @Inject
    private GuildManager guildManager;

    @Inject
    private GuildPermissionChecker permissionChecker;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        Option<User> userOption = this.userManager.findByPlayer(player);
        if (userOption.isEmpty()) {
            return;
        }

        // Convert the Component message to plain text for prefix checking (e.g., "!", "!!")
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        User user = userOption.get();
        boolean isGuildChat = user.getGuild()
                .map(guild -> this.sendGuildMessage(user, player, guild, message))
                .orElseGet(false);

        if (isGuildChat) {
            event.setCancelled(true);

            if (this.config.logGuildChat) {
                FunnyGuilds.getPluginLogger().info("[Guild Chat] " + player.getName() + ": " + message);
            }

            return;
        }

        int points = user.getRank().getPoints();
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{RANK}", this.config.chatRank.getValue())
                .register("{RANK}", user.getRank().getPosition(DefaultTops.USER_POINTS_TOP))
                .register("{POINTS}", this.config.chatPoints.getValue())
                .register("{POINTS-FORMAT}", NumberRange.inRangeToString(points, this.config.pointsFormat))
                .register("{POINTS}", points);

        user.getGuild()
                .peek(guild -> {
                    formatter.register("{TAG}", this.config.chatGuild.getValue());
                    formatter.register("{TAG}", guild.getTag());
                    formatter.register("{POS}", this.config.chatPosition.getValue());
                    formatter.register("{POS}", UserUtils.getUserPosition(this.permissionChecker, user));
                })
                .onEmpty(() -> {
                    formatter.register("{TAG}", "");
                    formatter.register("{POS}", "");
                });

        // Apply formatting using the modern renderer API
        // The renderer receives the display name and message as Components
        event.renderer((source, sourceDisplayName, messageComponent, viewer) -> {
            // Start with the default Minecraft format string pattern: "<player>: message"
            // But apply our custom formatting to it
            String defaultFormat = "<%1$s> %2$s";
            String customFormat = formatter.replace(defaultFormat);
            
            // Serialize components to legacy strings for formatting
            String displayNameStr = LegacyComponentSerializer.legacySection().serialize(sourceDisplayName);
            String messageStr = LegacyComponentSerializer.legacySection().serialize(messageComponent);
            
            // Apply the custom format
            String formattedMessage = String.format(customFormat, displayNameStr, messageStr);
            
            // Convert back to Component and return
            return LegacyComponentSerializer.legacySection().deserialize(formattedMessage);
        });
    }

    private boolean sendGuildMessage(User user, Player player, Guild guild, String message) {
        if (this.sendMessageToAllGuilds(user, player, guild, message)) {
            return true;
        }

        if (this.sendMessageToGuildAllies(user, player, guild, message)) {
            return true;
        }

        return this.sendMessageToGuildMembers(user, player, guild, message);
    }

    private boolean sendMessageToGuildMembers(User user, Player player, Guild guild, String message) {
        return this.sendMessageToGuilds(user, player, guild, this.config.chatPrivDesign.getValue(), this.config.chatPriv,
                message, Collections.singleton(guild), Type.PRIVATE);
    }

    private boolean sendMessageToGuildAllies(User user, Player player, Guild guild, String message) {
        Set<Guild> allies = new HashSet<>(guild.getAllies());
        allies.add(guild);

        return this.sendMessageToGuilds(user, player, guild, this.config.chatAllyDesign.getValue(), this.config.chatAlly,
                message, allies, Type.ALLY);
    }

    private boolean sendMessageToAllGuilds(User user, Player player, Guild guild, String message) {
        return this.sendMessageToGuilds(user, player, guild, this.config.chatGlobalDesign.getValue(), this.config.chatGlobal,
                message, this.guildManager.getGuilds(), Type.ALL);
    }

    private boolean sendMessageToGuilds(User user, Player player, Guild playerGuild, String chatDesign, String prefix, String message,
                                        Set<Guild> receivers, Type type) {
        int prefixLength = prefix.length();

        if (message.length() > prefixLength && message.substring(0, prefixLength).equalsIgnoreCase(prefix)) {
            if (!this.handleUsePermission(user, playerGuild, type)) {
                return true;
            }

            String subMessage = message.substring(prefixLength).trim();
            String resultMessage = this.formatChatDesign(user, player, playerGuild, chatDesign, subMessage);

            GuildPreChatEvent preChatEvent = new GuildPreChatEvent(EventCause.USER, user, playerGuild, type, receivers, resultMessage);
            if (!SimpleEventHandler.handle(preChatEvent)) {
                return true;
            }

            this.spy(user, player, playerGuild, subMessage);
            preChatEvent.getReceivers().forEach(guild -> sendMessageToGuild(guild, resultMessage, type));

            SimpleEventHandler.handle(new GuildChatEvent(EventCause.USER, user, playerGuild, type, receivers, resultMessage));

            return true;
        }

        return false;
    }

    private boolean handleUsePermission(User user, Guild guild, Type type) {
        GuildPermission<Boolean> permission = ChatType.getChatType(type).getUsePermission();
        return this.permissionChecker.handlePermission(guild, user, permission);
    }

    private void sendMessageToGuild(Guild guild, String message, Type type) {
        PandaStream.of(guild.getMembers())
                .filterNot(member -> member.getCache().isSpy())
                .filter(member -> this.checkSeePermission(member, guild, type))
                .forEach(member -> member.sendMessage(message));
    }

    private boolean checkSeePermission(User user, Guild guild, Type type) {
        ChatType chatType = ChatType.getChatType(type);
        return this.permissionChecker.getPermissionValue(guild, user, chatType.getSeePermission())
                .orElse(() -> this.permissionChecker.getPermissionValue(guild, user, chatType.getUsePermission()))
                .orElseGet(false);
    }

    private void spy(User user, Player player, Guild playerGuild, String message) {
        String spyMessage = this.formatChatDesign(user, player, playerGuild, this.config.chatSpyDesign.getValue(), message);

        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(onlinePlayer -> this.userManager.findByPlayer(onlinePlayer))
                .filter(onlineUser -> onlineUser.getCache().isSpy())
                .forEach(onlineUser -> onlineUser.sendMessage(spyMessage));
    }

    private String formatChatDesign(User user, Player player, Guild playerGuild, String chatDesign, String message) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", player.getName())
                .register("{TAG}", playerGuild.getTag())
                .register("{POS}", this.config.chatPosition.getValue())
                .register("{POS}", UserUtils.getUserPosition(this.permissionChecker, user))
                .register("{MESSAGE}", message);

        return HookUtils.replacePlaceholders(player, formatter.replace(chatDesign));
    }

    private enum ChatType {
        PRIVATE(GenericGuildPermissions.GUILD_CHAT_USE, GenericGuildPermissions.GUILD_CHAT_SEE),
        ALLY(GenericGuildPermissions.ALLY_CHAT_USE, GenericGuildPermissions.ALLY_CHAT_SEE),
        ALL(GenericGuildPermissions.GLOBAL_CHAT_USE, GenericGuildPermissions.GLOBAL_CHAT_SEE);

        private final GuildPermission<Boolean> usePermission;
        private final GuildPermission<Boolean> seePermission;

        ChatType(
                GuildPermission<Boolean> usePermission,
                GuildPermission<Boolean> seePermission
        ) {
            this.usePermission = usePermission;
            this.seePermission = seePermission;
        }

        private GuildPermission<Boolean> getUsePermission() {
            return this.usePermission;
        }

        private GuildPermission<Boolean> getSeePermission() {
            return this.seePermission;
        }

        private static ChatType getChatType(Type type) {
            return switch (type) {
                case PRIVATE -> PRIVATE;
                case ALLY -> ALLY;
                case ALL -> ALL;
                default -> throw new IllegalArgumentException("Unknown chat type: " + type);
            };
        }
    }
}
