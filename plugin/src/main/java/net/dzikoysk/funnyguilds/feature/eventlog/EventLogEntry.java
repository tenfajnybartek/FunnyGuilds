package net.dzikoysk.funnyguilds.feature.eventlog;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single entry in the guild event log.
 */
public class EventLogEntry {

    private final UUID id;
    private final UUID guildUuid;
    private final EventLogType type;
    private final String actor;
    private final String target;
    private final String details;
    private final Instant timestamp;

    public EventLogEntry(UUID guildUuid, EventLogType type, String actor, String target, String details) {
        this.id = UUID.randomUUID();
        this.guildUuid = guildUuid;
        this.type = type;
        this.actor = actor;
        this.target = target;
        this.details = details;
        this.timestamp = Instant.now();
    }

    public EventLogEntry(UUID id, UUID guildUuid, EventLogType type, String actor, String target, String details, Instant timestamp) {
        this.id = id;
        this.guildUuid = guildUuid;
        this.type = type;
        this.actor = actor;
        this.target = target;
        this.details = details;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return this.id;
    }

    public UUID getGuildUuid() {
        return this.guildUuid;
    }

    public EventLogType getType() {
        return this.type;
    }

    public String getActor() {
        return this.actor;
    }

    public String getTarget() {
        return this.target;
    }

    public String getDetails() {
        return this.details;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }
}
