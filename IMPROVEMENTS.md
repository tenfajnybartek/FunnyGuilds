# Concrete Code Improvements - FunnyGuilds

This document contains specific, actionable code improvements with examples.

---

## 1. Extract Constants for Magic Numbers

### Issue: Magic numbers reduce code readability

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/listener/region/PlayerMove.java:123`

**Current Code**:
```java
cache.setNotificationTime(System.currentTimeMillis() + 1000L * this.config.regionNotificationCooldown);
```

**Suggested Improvement**:
```java
// Add to a constants class or at the top of the file:
private static final long MILLIS_PER_SECOND = 1000L;

// Then use:
cache.setNotificationTime(System.currentTimeMillis() + MILLIS_PER_SECOND * this.config.regionNotificationCooldown);
```

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/config/serdes/FunnyTimeTransformer.java:31-38`

**Current Code**:
```java
if (value > 86400) {
    return TimeUtils.getDaysAndOther(value);
}
if (value > 1440) {
    return TimeUtils.getHoursAndMinutes(value);
}
```

**Suggested Improvement**:
```java
private static final int SECONDS_PER_DAY = 86400;
private static final int MINUTES_PER_DAY = 1440;

if (value > SECONDS_PER_DAY) {
    return TimeUtils.getDaysAndOther(value);
}
if (value > MINUTES_PER_DAY) {
    return TimeUtils.getHoursAndMinutes(value);
}
```

---

## 2. Refactor Large Configuration File

### Issue: PluginConfiguration.java is 1,529 lines - too large to maintain easily

**Current Structure**:
```java
public class PluginConfiguration {
    // All configuration in one file
    public MysqlConfiguration mysql = new MysqlConfiguration();
    public RegionConfiguration region = new RegionConfiguration();
    public RankingConfiguration ranking = new RankingConfiguration();
    // ... 1500+ more lines
}
```

**Suggested Improvement**:

Create separate configuration modules:

```java
// PluginConfiguration.java (Main aggregator)
public class PluginConfiguration {
    @Comment("Database configuration")
    public DatabaseConfiguration database = new DatabaseConfiguration();
    
    @Comment("Guild-related configuration")
    public GuildConfiguration guild = new GuildConfiguration();
    
    @Comment("Region protection configuration")
    public RegionConfiguration region = new RegionConfiguration();
    
    @Comment("Ranking system configuration")
    public RankingConfiguration ranking = new RankingConfiguration();
    
    @Comment("Combat and points configuration")
    public CombatConfiguration combat = new CombatConfiguration();
}

// GuildConfiguration.java (Extracted)
public class GuildConfiguration {
    // All guild-related settings
    public int minNameLength = 3;
    public int maxNameLength = 16;
    public List<String> forbiddenNames = List.of("admin", "moderator");
    // ... guild-specific configuration
}

// RegionConfiguration.java (Extracted)
public class RegionConfiguration {
    // All region-related settings
    public int minSize = 10;
    public int maxSize = 100;
    public boolean explosionProtection = true;
    // ... region-specific configuration
}
```

**Benefits**:
- Each configuration file is focused and maintainable
- Easier to find and modify settings
- Better IDE navigation
- Clearer ownership of configuration sections

---

## 3. Improve Exception Handling Context

### Issue: Generic exception catching without specific context

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/data/database/serializer/DatabaseMemberPermissionsSerializer.java:60`

**Current Code**:
```java
try {
    // ... parsing logic
    memberPerms.setOverride(permType, value, changedBy);
} catch (Exception e) {
    FunnyGuilds.getPluginLogger().error("Failed to deserialize member permission", e);
}
```

**Suggested Improvement**:
```java
try {
    // ... parsing logic
    memberPerms.setOverride(permType, value, changedBy);
} catch (SQLException e) {
    FunnyGuilds.getPluginLogger().error(
        "Failed to deserialize member permission from database for guild: " + guildUuid + 
        ", member: " + memberUuid + ", permission: " + permTypeStr, e);
} catch (IllegalArgumentException e) {
    FunnyGuilds.getPluginLogger().error(
        "Invalid UUID format in member permission: guild=" + guildUuidStr + 
        ", member=" + memberUuidStr, e);
} catch (Exception e) {
    FunnyGuilds.getPluginLogger().error(
        "Unexpected error deserializing permission for guild: " + guildUuid, e);
}
```

**Benefits**:
- Specific exception types caught first
- More context in error messages
- Easier debugging

---

## 4. Add Javadoc to Public APIs

### Issue: Missing documentation on public methods

**Example from**: `plugin/src/main/java/net/dzikoysk/funnyguilds/guild/Guild.java`

**Current Code**:
```java
public void addMember(User user) {
    this.members.add(user.getUUID());
    user.setGuild(this);
}
```

**Suggested Improvement**:
```java
/**
 * Adds a user as a member of this guild.
 * 
 * <p>This method:
 * <ul>
 *   <li>Adds the user's UUID to the guild's member list</li>
 *   <li>Sets the user's guild reference to this guild</li>
 *   <li>Does not trigger any events (use GuildMemberAddEvent separately)</li>
 * </ul>
 *
 * @param user the user to add as a member, must not be null
 * @throws NullPointerException if user is null
 * @see #removeMember(User)
 * @see GuildMemberAddEvent
 */
public void addMember(User user) {
    Objects.requireNonNull(user, "user cannot be null");
    this.members.add(user.getUUID());
    user.setGuild(this);
}
```

---

## 5. Stream API Refactoring Examples

### Issue: Traditional for-loops where streams would be more readable

**Example Pattern**:
```java
// BEFORE: Traditional filtering
List<Guild> activeGuilds = new ArrayList<>();
for (Guild guild : allGuilds) {
    if (guild.isActive() && guild.getMembersCount() > 5) {
        activeGuilds.add(guild);
    }
}
```

**Suggested Improvement**:
```java
// AFTER: Stream API
List<Guild> activeGuilds = allGuilds.stream()
    .filter(Guild::isActive)
    .filter(guild -> guild.getMembersCount() > 5)
    .collect(Collectors.toList());
```

**Example Pattern 2**:
```java
// BEFORE: Finding maximum
User topUser = null;
int maxPoints = -1;
for (User user : users) {
    if (user.getRank().getPoints() > maxPoints) {
        maxPoints = user.getRank().getPoints();
        topUser = user;
    }
}
```

**Suggested Improvement**:
```java
// AFTER: Stream API
Optional<User> topUser = users.stream()
    .max(Comparator.comparing(user -> user.getRank().getPoints()));
```

**Example Pattern 3**:
```java
// BEFORE: Transformation
List<String> guildNames = new ArrayList<>();
for (Guild guild : guilds) {
    guildNames.add(guild.getName());
}
```

**Suggested Improvement**:
```java
// AFTER: Stream API
List<String> guildNames = guilds.stream()
    .map(Guild::getName)
    .collect(Collectors.toList());
```

**When NOT to use streams**:
- Performance-critical loops (benchmarking required)
- Simple iterations without transformation
- Loops with early exit (break/continue logic)
- Loops modifying external state (side effects)

---

## 6. Null Safety Improvements

### Issue: Inconsistent null handling

**Example**:
```java
// BEFORE: Manual null checks
public String getGuildTag(User user) {
    if (user == null) {
        return "";
    }
    Guild guild = user.getGuild();
    if (guild == null) {
        return "";
    }
    return guild.getTag();
}
```

**Suggested Improvement Option 1 (Current pattern - Option/Result)**:
```java
// AFTER: Using Option type
public String getGuildTag(User user) {
    return Option.of(user)
        .flatMap(User::getGuild)
        .map(Guild::getTag)
        .orElseGet(() -> "");
}
```

**Suggested Improvement Option 2 (Standard Java)**:
```java
// AFTER: Using Optional
public String getGuildTag(User user) {
    return Optional.ofNullable(user)
        .flatMap(u -> Optional.ofNullable(u.getGuild()))
        .map(Guild::getTag)
        .orElse("");
}
```

---

## 7. Database Schema Migration Plan for Version 5.0

### Issue: Field "attacked" should be renamed to "protection"

**Files to Update**:
1. `FlatGuildSerializer.java`
2. `DatabaseGuildSerializer.java`
3. `SQLDataModel.java`

**Migration Strategy**:

**Step 1**: Add new column while keeping old one
```java
// SQLDataModel.java
this.guildsTable.add("attacked", SQLType.BIGINT); // Keep for backward compatibility
this.guildsTable.add("protection", SQLType.BIGINT); // Add new column
```

**Step 2**: Migration query
```java
// Migration class: M0002_Rename_attacked_to_protection.java
public class M0002_RenameAttackedToProtection implements ConfigurationMigration {
    @Override
    public void migrate(Database database) {
        // Copy data from attacked to protection
        String query = "UPDATE guilds SET protection = attacked WHERE protection IS NULL";
        // Execute migration
    }
}
```

**Step 3**: Update serializers to use new field
```java
// DatabaseGuildSerializer.java
// BEFORE:
Instant protection = TimeUtils.positiveOrNullInstant(resultSet.getLong("attacked"));

// AFTER (5.0):
Inst protection = TimeUtils.positiveOrNullInstant(resultSet.getLong("protection"));
```

**Step 4**: Remove old column in version 5.1+
```java
// After several releases, drop old column
String query = "ALTER TABLE guilds DROP COLUMN attacked";
```

---

## 8. Add Builder Pattern for Complex Objects

### Issue: Complex constructors with many parameters

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/event/rank/CombatPointsChangeEvent.java:34`

**Current Code**:
```java
public CombatPointsChangeEvent(EventCause eventCause, User attacker, User victim, 
                                int attackerPointsChange, int victimPointsChange, 
                                Map<User, Assist> assistsMap) {
    // ...
}
```

**Suggested Improvement**:
```java
// Add Builder
public class CombatPointsChangeEvent extends Event {
    // ... fields
    
    private CombatPointsChangeEvent(Builder builder) {
        this.eventCause = builder.eventCause;
        this.attacker = builder.attacker;
        this.victim = builder.victim;
        this.attackerPointsChange = builder.attackerPointsChange;
        this.victimPointsChange = builder.victimPointsChange;
        this.assistsMap = builder.assistsMap;
    }
    
    public static class Builder {
        private EventCause eventCause;
        private User attacker;
        private User victim;
        private int attackerPointsChange;
        private int victimPointsChange;
        private Map<User, Assist> assistsMap = new HashMap<>();
        
        public Builder eventCause(EventCause eventCause) {
            this.eventCause = eventCause;
            return this;
        }
        
        public Builder attacker(User attacker) {
            this.attacker = attacker;
            return this;
        }
        
        public Builder victim(User victim) {
            this.victim = victim;
            return this;
        }
        
        public Builder attackerPointsChange(int points) {
            this.attackerPointsChange = points;
            return this;
        }
        
        public Builder victimPointsChange(int points) {
            this.victimPointsChange = points;
            return this;
        }
        
        public Builder assists(Map<User, Assist> assists) {
            this.assistsMap = assists;
            return this;
        }
        
        public CombatPointsChangeEvent build() {
            Objects.requireNonNull(eventCause, "eventCause is required");
            Objects.requireNonNull(attacker, "attacker is required");
            Objects.requireNonNull(victim, "victim is required");
            return new CombatPointsChangeEvent(this);
        }
    }
}

// Usage:
CombatPointsChangeEvent event = new CombatPointsChangeEvent.Builder()
    .eventCause(EventCause.COMBAT)
    .attacker(attacker)
    .victim(victim)
    .attackerPointsChange(50)
    .victimPointsChange(-50)
    .assists(assistMap)
    .build();
```

---

## 9. Add Package Documentation

### Issue: Missing package-info.java files

**Create**: `plugin/src/main/java/net/dzikoysk/funnyguilds/guild/package-info.java`

```java
/**
 * Core guild management system.
 * 
 * <p>This package contains the fundamental classes for managing guilds in FunnyGuilds:
 * <ul>
 *   <li>{@link net.dzikoysk.funnyguilds.guild.Guild} - Main guild entity</li>
 *   <li>{@link net.dzikoysk.funnyguilds.guild.GuildManager} - Guild lifecycle management</li>
 *   <li>{@link net.dzikoysk.funnyguilds.guild.RegionManager} - Region protection management</li>
 *   <li>{@link net.dzikoysk.funnyguilds.guild.GuildRankManager} - Guild ranking system</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create a new guild
 * Result<Guild, String> result = GuildManager.getInstance()
 *     .create("MyGuild", "TAG", owner, location);
 * 
 * if (result.isOk()) {
 *     Guild guild = result.get();
 *     guild.addMember(newMember);
 * }
 * }</pre>
 *
 * @see net.dzikoysk.funnyguilds.user User management
 * @see net.dzikoysk.funnyguilds.rank Ranking system
 * @since 1.0.0
 */
package net.dzikoysk.funnyguilds.guild;
```

---

## 10. Improve Test Coverage

### Issue: No test files found in the repository

**Create Example Test**:

**File**: `plugin/src/test/java/net/dzikoysk/funnyguilds/guild/GuildTest.java`

```java
package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Guild Tests")
class GuildTest {

    @Mock
    private User mockUser;
    
    private Guild guild;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        guild = new Guild(UUID.randomUUID(), "TestGuild", "TG");
        when(mockUser.getUUID()).thenReturn(UUID.randomUUID());
    }
    
    @Test
    @DisplayName("Should add member to guild")
    void shouldAddMember() {
        // When
        guild.addMember(mockUser);
        
        // Then
        assertTrue(guild.getMembers().contains(mockUser.getUUID()));
        verify(mockUser).setGuild(guild);
    }
    
    @Test
    @DisplayName("Should throw NPE when adding null member")
    void shouldThrowWhenAddingNullMember() {
        // Then
        assertThrows(NullPointerException.class, () -> {
            guild.addMember(null);
        });
    }
    
    @Test
    @DisplayName("Should get correct member count")
    void shouldGetMemberCount() {
        // Given
        guild.addMember(mockUser);
        
        // When
        int count = guild.getMembersCount();
        
        // Then
        assertEquals(1, count);
    }
}
```

**Integration Test Example**:

**File**: `plugin/src/test/java/net/dzikoysk/funnyguilds/data/database/DatabaseIntegrationTest.java`

```java
@DisplayName("Database Integration Tests")
class DatabaseIntegrationTest {

    private Database database;
    
    @BeforeEach
    void setUp() throws ClassNotFoundException {
        // Setup H2 in-memory database for testing
        PluginConfiguration config = new PluginConfiguration();
        config.mysql.hostname = "mem:test";
        database = new Database(config);
    }
    
    @AfterEach
    void tearDown() {
        database.shutdown();
    }
    
    @Test
    @DisplayName("Should save and load guild from database")
    void shouldSaveAndLoadGuild() {
        // Given
        Guild guild = new Guild(UUID.randomUUID(), "TestGuild", "TG");
        
        // When
        // Save guild
        // Load guild
        
        // Then
        assertNotNull(loadedGuild);
        assertEquals("TestGuild", loadedGuild.getName());
    }
}
```

---

## 11. Configuration Validation

### Issue: Missing validation for configuration values

**Example**:
```java
// BEFORE: No validation
public class MysqlConfiguration {
    public String hostname = "localhost";
    public int port = 3306;
    public int poolSize = 10;
}
```

**Suggested Improvement**:
```java
// AFTER: With validation
public class MysqlConfiguration {
    
    @Comment("MySQL server hostname")
    public String hostname = "localhost";
    
    @Comment("MySQL server port (1-65535)")
    @Range(min = 1, max = 65535)
    public int port = 3306;
    
    @Comment("Connection pool size (1-50)")
    @Range(min = 1, max = 50)
    public int poolSize = 10;
    
    /**
     * Validates the configuration values.
     * 
     * @throws IllegalArgumentException if any value is invalid
     */
    public void validate() {
        if (hostname == null || hostname.trim().isEmpty()) {
            throw new IllegalArgumentException("MySQL hostname cannot be empty");
        }
        
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("MySQL port must be between 1 and 65535");
        }
        
        if (poolSize < 1 || poolSize > 50) {
            throw new IllegalArgumentException("Pool size must be between 1 and 50");
        }
    }
}
```

---

## 12. Add Metrics and Monitoring

### Issue: Limited observability

**Suggested Addition**:

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/metrics/PerformanceMetrics.java`

```java
/**
 * Performance metrics for monitoring plugin health.
 */
public class PerformanceMetrics {
    
    private final AtomicLong guildCreations = new AtomicLong(0);
    private final AtomicLong databaseQueries = new AtomicLong(0);
    private final AtomicLong failedQueries = new AtomicLong(0);
    private final ConcurrentHashMap<String, Long> operationTimes = new ConcurrentHashMap<>();
    
    public void recordGuildCreation() {
        guildCreations.incrementAndGet();
    }
    
    public void recordDatabaseQuery(boolean success, long durationMs) {
        databaseQueries.incrementAndGet();
        if (!success) {
            failedQueries.incrementAndGet();
        }
        operationTimes.merge("database_query", durationMs, Long::sum);
    }
    
    public Map<String, Object> getMetrics() {
        return Map.of(
            "guild_creations", guildCreations.get(),
            "database_queries", databaseQueries.get(),
            "failed_queries", failedQueries.get(),
            "avg_query_time_ms", calculateAverageQueryTime()
        );
    }
    
    private long calculateAverageQueryTime() {
        long total = operationTimes.getOrDefault("database_query", 0L);
        long queries = databaseQueries.get();
        return queries > 0 ? total / queries : 0;
    }
}
```

---

## Summary of Priority Improvements

### High Priority (Do First)
1. ✅ Extract magic numbers to constants
2. ✅ Add comprehensive Javadoc to public APIs
3. ✅ Create Version 5.0 migration plan
4. ✅ Add basic unit tests (target: key business logic)

### Medium Priority (Do Soon)
5. ✅ Refactor large configuration files
6. ✅ Improve exception handling with context
7. ✅ Add configuration validation
8. ✅ Modernize with Stream API (selected cases)

### Low Priority (Nice to Have)
9. ✅ Add Builder patterns for complex objects
10. ✅ Create package-info.java files
11. ✅ Add performance metrics
12. ✅ Increase test coverage to 70%+

---

**Generated**: 2025-12-17  
**For Version**: 5.0.0-SNAPSHOT
