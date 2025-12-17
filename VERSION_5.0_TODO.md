# Version 5.0 Release Checklist

This document tracks all changes planned for the FunnyGuilds 5.0 release.

## Breaking Changes (Must Complete Before 5.0)

### Database Schema Changes

#### 1. Rename "attacked" field to "protection"
**Priority**: HIGH  
**Status**: ⏳ Pending

**Files to Update**:
- [ ] `plugin/src/main/java/net/dzikoysk/funnyguilds/data/flat/seralizer/FlatGuildSerializer.java`
  - Line ~385: Change `wrapper.getLong("attacked")` to `wrapper.getLong("protection")`
  - Line ~390: Change `wrapper.set("attacked", ...)` to `wrapper.set("protection", ...)`
  
- [ ] `plugin/src/main/java/net/dzikoysk/funnyguilds/data/database/SQLDataModel.java`
  - Line ~XXX: Change `this.guildsTable.add("attacked", SQLType.BIGINT)` to `this.guildsTable.add("protection", SQLType.BIGINT)`
  
- [ ] `plugin/src/main/java/net/dzikoysk/funnyguilds/data/database/serializer/DatabaseGuildSerializer.java`
  - Line ~XX: Change `resultSet.getLong("attacked")` to `resultSet.getLong("protection")`
  - Line ~XX: Change `statement.set("attacked", ...)` to `statement.set("protection", ...)`

**Migration Strategy**:
```java
// Create migration: M0002_RenameAttackedToProtection.java
// 1. Add new "protection" column
// 2. Copy data: UPDATE guilds SET protection = attacked
// 3. Update code to use "protection"
// 4. Mark "attacked" as deprecated in 5.0
// 5. Remove "attacked" in 5.1
```

---

### API Deprecations Removal

#### 2. Remove Deprecated Utility Methods
**Priority**: HIGH  
**Status**: ⏳ Pending

**UserUtils.java**:
- [ ] Remove `getUsers()` method (Line 38)
- [ ] Remove `getUserByName()` methods (deprecated in favor of UserManager)
- [ ] Update documentation to point to UserManager alternatives

**UserCache.java**:
- [ ] Review all @Deprecated methods
- [ ] Ensure UserManager has replacements
- [ ] Remove deprecated methods

**RegionUtils.java**:
- [ ] Remove deprecated static utility methods
- [ ] Ensure RegionManager has all necessary instance methods

**GuildUtils.java**:
- [ ] Remove deprecated static utility methods
- [ ] Ensure GuildManager has all necessary instance methods

---

#### 3. Update Placeholder API
**Priority**: MEDIUM  
**Status**: ⏳ Pending

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/feature/hooks/placeholderapi/PlaceholderAPIHook.java`

- [ ] Remove `prefix` placeholder (deprecated)
- [ ] Update documentation with migration guide
- [ ] Add warning in 4.x versions about removal

**Migration Guide**:
```yaml
# OLD (4.x):
placeholder: "{prefix}"

# NEW (5.0+):
placeholder: "{guild_prefix}"  # or whatever the new name is
```

---

#### 4. Migrate PTOP/GTOP Placeholders
**Priority**: MEDIUM  
**Status**: ⏳ Pending

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/rank/placeholders/RankPlaceholdersService.java`

**Current**: `{PTOP-x}` and `{GTOP-x}`  
**Target**: `{PTOP-type-x}` and `{GTOP-type-x}`

- [ ] Implement new placeholder format
- [ ] Keep backward compatibility in 4.x
- [ ] Add deprecation warnings
- [ ] Remove old format in 5.0
- [ ] Update all documentation

---

#### 5. Rename "total-points" to "points"
**Priority**: LOW  
**Status**: ⏳ Pending

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/guild/placeholders/GuildPlaceholdersService.java`

- [ ] Add new "points" placeholder
- [ ] Deprecate "total-points" in 4.x
- [ ] Remove "total-points" in 5.0
- [ ] Update configuration examples

---

### Configuration Cleanup

#### 6. Remove assistsRegionsIgnored Field
**Priority**: LOW  
**Status**: ⏳ Pending

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/config/PluginConfiguration.java:448`

```java
// Line 448: Remove this field
@Deprecated
public Set<String> assistsRegionsIgnored = Collections.emptySet();
```

- [ ] Remove field from PluginConfiguration
- [ ] Remove any code that reads this configuration
- [ ] Update configuration migration guide

---

#### 7. Remove Old Migration Code
**Priority**: LOW  
**Status**: ⏳ Pending

**File**: `plugin/src/main/java/net/dzikoysk/funnyguilds/config/migration/M0001_Migrate_old_region_notification_keys.java`

- [ ] Can be removed if all users have migrated from very old versions
- [ ] Check telemetry for old version usage
- [ ] Document in breaking changes

---

### Code Quality Improvements

#### 8. Add Constants for Magic Numbers
**Priority**: MEDIUM  
**Status**: ⏳ Pending

**Create**: `plugin/src/main/java/net/dzikoysk/funnyguilds/shared/TimeConstants.java`

```java
public final class TimeConstants {
    private TimeConstants() {}
    
    public static final long MILLIS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long MINUTES_PER_HOUR = 60L;
    public static final long HOURS_PER_DAY = 24L;
    
    public static final long SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    
    public static final int MINUTES_PER_DAY = 1440;
}
```

**Files to Update**:
- [ ] `plugin/src/main/java/net/dzikoysk/funnyguilds/listener/region/PlayerMove.java:123`
- [ ] `plugin/src/main/java/net/dzikoysk/funnyguilds/config/serdes/FunnyTimeTransformer.java:31-38`
- [ ] Search for other magic numbers: `grep -rn "1000L\|86400\|1440" --include="*.java"`

---

#### 9. Refactor Large Configuration File
**Priority**: MEDIUM  
**Status**: ⏳ Pending

**Current**: `PluginConfiguration.java` (1,529 lines)

**Target Structure**:
```
config/
├── PluginConfiguration.java (main aggregator)
├── sections/
│   ├── DatabaseConfiguration.java
│   ├── GuildConfiguration.java
│   ├── RegionConfiguration.java
│   ├── RankingConfiguration.java
│   ├── CombatConfiguration.java
│   └── ... (other sections)
```

- [ ] Extract database settings to DatabaseConfiguration
- [ ] Extract guild settings to GuildConfiguration
- [ ] Extract region settings to RegionConfiguration
- [ ] Extract ranking settings to RankingConfiguration
- [ ] Extract combat settings to CombatConfiguration
- [ ] Update all references throughout codebase
- [ ] Test configuration loading

---

## Non-Breaking Improvements (Can Complete Anytime)

### Documentation

#### 10. Add Comprehensive Javadoc
**Priority**: HIGH  
**Status**: ⏳ Pending

**Target Coverage**: 80%+ for public APIs

**Priority Files**:
- [ ] `Guild.java` - All public methods
- [ ] `User.java` - All public methods
- [ ] `GuildManager.java` - All public methods
- [ ] `UserManager.java` - All public methods
- [ ] `RegionManager.java` - All public methods

**Template**:
```java
/**
 * Brief description of what the method does.
 * 
 * <p>More detailed explanation if needed, including:
 * <ul>
 *   <li>Important behavior notes</li>
 *   <li>Side effects</li>
 *   <li>Thread safety considerations</li>
 * </ul>
 *
 * @param paramName description of parameter
 * @return description of return value
 * @throws ExceptionType when this exception is thrown
 * @see RelatedClass
 * @since 5.0.0
 */
```

---

#### 11. Add Package Documentation
**Priority**: MEDIUM  
**Status**: ⏳ Pending

**Create package-info.java for**:
- [ ] `guild/package-info.java`
- [ ] `user/package-info.java`
- [ ] `rank/package-info.java`
- [ ] `feature/package-info.java`
- [ ] `data/package-info.java`
- [ ] `config/package-info.java`

---

#### 12. Create README.md
**Priority**: HIGH  
**Status**: ⏳ Pending

**Content Should Include**:
- [ ] Project description
- [ ] Features list
- [ ] Installation instructions
- [ ] Basic configuration guide
- [ ] Command reference
- [ ] API usage examples
- [ ] Contributing guidelines link
- [ ] License information

---

### Testing

#### 13. Add Unit Tests
**Priority**: HIGH  
**Status**: ⏳ Pending

**Target Coverage**: 70%+ for core business logic

**Priority Test Files**:
- [ ] `GuildTest.java` - Guild entity operations
- [ ] `UserTest.java` - User entity operations
- [ ] `GuildManagerTest.java` - Guild management
- [ ] `UserManagerTest.java` - User management
- [ ] `RankCalculationTest.java` - Ranking calculations
- [ ] `PermissionCheckerTest.java` - Permission checks

**Test Infrastructure**:
- [x] JUnit 5 configured
- [x] Mockito configured
- [ ] Test coverage reporting (JaCoCo)
- [ ] CI/CD integration

---

#### 14. Add Integration Tests
**Priority**: MEDIUM  
**Status**: ⏳ Pending

**Test Scenarios**:
- [ ] Database persistence (save/load cycle)
- [ ] Guild creation workflow
- [ ] Member management workflow
- [ ] Combat points calculation
- [ ] Region protection

**Test Database**:
- [ ] Setup H2 in-memory database for tests
- [ ] Create test data fixtures
- [ ] Add database migration tests

---

### Code Modernization

#### 15. Stream API Refactoring
**Priority**: LOW  
**Status**: ⏳ Pending

**Candidates** (~84 for-loops reviewed):

**High Value Conversions**:
- [ ] Collection filtering operations
- [ ] Collection mapping operations
- [ ] Finding max/min values
- [ ] Counting/summing operations

**Keep as For-Loops**:
- [ ] Performance-critical paths
- [ ] Loops with early exit (break/continue)
- [ ] Simple iterations
- [ ] Loops with side effects

---

#### 16. Improve Null Safety
**Priority**: MEDIUM  
**Status**: ⏳ Pending

**Standardize on**:
- Current: Mix of `Option<T>`, `Optional<T>`, and `@Nullable`
- Target: Consistent use of `Option<T>` (panda-std) across codebase

**Actions**:
- [ ] Audit all public methods returning nullable values
- [ ] Convert to Option<T> where appropriate
- [ ] Add @Nullable/@NotNull annotations where Option not used
- [ ] Document null-safety contracts

---

### Performance

#### 17. Add Performance Metrics
**Priority**: LOW  
**Status**: ⏳ Pending

**Metrics to Track**:
- [ ] Guild operations (create, delete, update)
- [ ] Database query performance
- [ ] Combat calculations
- [ ] Placeholder resolution time
- [ ] Tablist update frequency

**Implementation**:
- [ ] Create PerformanceMetrics class
- [ ] Add metric collection points
- [ ] Add /fg metrics command
- [ ] Add metrics export (Prometheus format?)

---

#### 18. Optimize Database Queries
**Priority**: LOW  
**Status**: ⏳ Pending

**Current**: Individual queries for each operation  
**Target**: Batch operations where possible

**Opportunities**:
- [ ] Batch guild loading on startup
- [ ] Batch user loading on startup
- [ ] Batch permission updates
- [ ] Connection pooling analysis

---

## Additional Features (Optional for 5.0)

### 19. Add Configuration Validation
**Priority**: LOW  
**Status**: ⏳ Pending

- [ ] Add validation methods to all configuration classes
- [ ] Validate on plugin startup
- [ ] Provide helpful error messages
- [ ] Add configuration validation tests

---

### 20. Improve Error Messages
**Priority**: LOW  
**Status**: ⏳ Pending

**Review Error Messages**:
- [ ] Database connection errors
- [ ] Configuration errors
- [ ] Command usage errors
- [ ] Permission errors

**Make Messages**:
- User-friendly
- Actionable (tell user how to fix)
- Include relevant context
- Support multiple languages

---

### 21. Add Builder Patterns
**Priority**: LOW  
**Status**: ⏳ Pending

**Candidates**:
- [ ] `CombatPointsChangeEvent` (7 parameters)
- [ ] Complex configuration builders
- [ ] Guild creation (if constructor becomes complex)

---

## Timeline Estimate

### Phase 1: Critical (Pre-5.0 Release)
**Estimated Time**: 2-3 weeks
- Database schema changes (#1)
- API deprecation removals (#2, #3, #4, #5)
- Configuration cleanup (#6, #7)

### Phase 2: Important (Can be in 5.0 or 5.1)
**Estimated Time**: 2-3 weeks
- Documentation (#10, #11, #12)
- Unit tests (#13)
- Constants extraction (#8)

### Phase 3: Improvements (Continuous)
**Estimated Time**: Ongoing
- Integration tests (#14)
- Code modernization (#15, #16)
- Performance improvements (#17, #18)
- Optional features (#19, #20, #21)

---

## Pre-Release Checklist

### Testing
- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] Manual testing on test server
- [ ] Performance testing (load test)
- [ ] Database migration testing (upgrade from 4.x)

### Documentation
- [ ] CHANGELOG.md updated with all changes
- [ ] MIGRATION_GUIDE.md created for 4.x → 5.0
- [ ] API documentation updated
- [ ] Configuration examples updated
- [ ] README.md reviewed and updated

### Code Quality
- [ ] All TODOs addressed or documented
- [ ] Code review completed
- [ ] No deprecated code marked for 5.0 removal
- [ ] License headers present
- [ ] Code formatted consistently

### Release Preparation
- [ ] Version bumped to 5.0.0
- [ ] Release notes prepared
- [ ] GitHub release created
- [ ] Announcement prepared
- [ ] Backup/rollback plan documented

---

## Notes

- **Breaking Changes**: All breaking changes must be documented in MIGRATION_GUIDE.md
- **Backward Compatibility**: Consider providing compatibility shims for 1-2 minor versions
- **Deprecation Policy**: Deprecate in 4.x, remove in 5.0
- **Testing**: Every breaking change must have corresponding tests
- **Documentation**: Every breaking change must be documented

---

**Last Updated**: 2025-12-17  
**Target Release**: TBD  
**Version**: 5.0.0
