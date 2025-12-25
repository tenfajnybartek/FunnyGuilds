package net.dzikoysk.funnyguilds.feature.eventlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;

/**
 * Manages event logs for all guilds.
 */
public class EventLogManager {

    private final PluginConfiguration config;
    private final Map<UUID, LinkedList<EventLogEntry>> guildLogs = new HashMap<>();
    private final int maxEntries;
    private boolean dirty = false;

    public EventLogManager(PluginConfiguration config) {
        this.config = config;
        this.maxEntries = config.eventLog.maxEntries;
    }

    /**
     * Logs an event for a guild.
     */
    public void logEvent(Guild guild, EventLogType type, User actor, String target, String details) {
        if (!config.eventLog.enabled) {
            return;
        }
        
        String actorName = actor != null ? actor.getName() : "System";
        EventLogEntry entry = new EventLogEntry(guild.getUUID(), type, actorName, target, details);
        
        LinkedList<EventLogEntry> logs = this.guildLogs.computeIfAbsent(guild.getUUID(), k -> new LinkedList<>());
        logs.addFirst(entry);
        
        // Trim to max entries
        while (logs.size() > this.maxEntries) {
            logs.removeLast();
        }
        
        this.dirty = true;
    }

    /**
     * Logs an event for a guild with actor name string.
     */
    public void logEvent(Guild guild, EventLogType type, String actorName, String target, String details) {
        if (!config.eventLog.enabled) {
            return;
        }
        
        EventLogEntry entry = new EventLogEntry(guild.getUUID(), type, actorName, target, details);
        
        LinkedList<EventLogEntry> logs = this.guildLogs.computeIfAbsent(guild.getUUID(), k -> new LinkedList<>());
        logs.addFirst(entry);
        
        // Trim to max entries
        while (logs.size() > this.maxEntries) {
            logs.removeLast();
        }
        
        this.dirty = true;
    }

    /**
     * Gets event logs for a guild.
     */
    public List<EventLogEntry> getLogs(Guild guild) {
        LinkedList<EventLogEntry> logs = this.guildLogs.get(guild.getUUID());
        if (logs == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(logs);
    }

    /**
     * Gets event logs for a guild with pagination.
     */
    public List<EventLogEntry> getLogs(Guild guild, int page, int pageSize) {
        List<EventLogEntry> allLogs = this.getLogs(guild);
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allLogs.size());
        
        if (start >= allLogs.size()) {
            return Collections.emptyList();
        }
        
        return allLogs.subList(start, end);
    }

    /**
     * Gets the total number of log entries for a guild.
     */
    public int getLogCount(Guild guild) {
        LinkedList<EventLogEntry> logs = this.guildLogs.get(guild.getUUID());
        return logs != null ? logs.size() : 0;
    }

    /**
     * Clears logs for a specific guild.
     */
    public void clearLogs(Guild guild) {
        this.guildLogs.remove(guild.getUUID());
        this.dirty = true;
    }

    /**
     * Loads logs from storage.
     */
    public void loadLogs(UUID guildUuid, List<EventLogEntry> entries) {
        LinkedList<EventLogEntry> logs = new LinkedList<>(entries);
        // Sort by timestamp (newest first)
        logs.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        // Trim to max entries
        while (logs.size() > this.maxEntries) {
            logs.removeLast();
        }
        this.guildLogs.put(guildUuid, logs);
    }

    /**
     * Gets all logs for persistence.
     */
    public Map<UUID, LinkedList<EventLogEntry>> getAllLogs() {
        return new HashMap<>(this.guildLogs);
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void markClean() {
        this.dirty = false;
    }
}
