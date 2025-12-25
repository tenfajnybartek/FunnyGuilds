# Config Refactoring Proposal: Split PluginConfiguration into Separate YAML Files

## Overview

This document outlines a proposal to refactor FunnyGuilds configuration by splitting the monolithic `config.yml` file (1,547 lines) into multiple, focused configuration files. This will improve maintainability, readability, and user experience.

## Current State

### Problem
- **Single massive file**: `PluginConfiguration.java` contains 1,547 lines with all configuration options
- **Difficult to navigate**: Users must scroll through a huge config.yml to find specific settings
- **Poor organization**: Related settings are sometimes scattered throughout the file
- **Harder maintenance**: Changes to one section require loading the entire config

### Existing Structure
The codebase already has configuration sections organized in `config/sections/`:
- `PanelConfiguration.java` - Guild panel/GUI settings
- `PermissionsPanelConfiguration.java` - Permission management UI
- `GuildVaultConfiguration.java` - Vault/bank settings
- `EventLogConfiguration.java` - Event logging configuration
- `DiplomacyConfiguration.java` - Ally/enemy mechanics
- `HeartConfiguration.java` - Guild heart/center block settings
- `TntProtectionConfiguration.java` - TNT protection settings
- `TopConfiguration.java` - Top rankings/leaderboard settings
- `ScoreboardConfiguration.java` - Scoreboard display settings
- `CommandsConfiguration.java` - Command-related settings
- `SecuritySystemConfiguration.java` - Security/protection settings
- `MysqlConfiguration.java` - Database configuration

## Proposed Solution

### Phase 1: Extract to Separate Files

Split `config.yml` into focused, logical files:

#### Core Configuration Files

1. **`config.yml`** (Main/Core)
   - Plugin name, version info
   - Debug mode
   - Update notifications
   - Default locale
   - Data storage type (flat/mysql)
   - Region settings (basic)
   - Guild name/tag requirements
   - Core mechanics (PvP, death handling)
   - Basic guild creation settings
   
2. **`database.yml`** (Database)
   - MySQL connection settings (from `MysqlConfiguration`)
   - Connection pool configuration
   - Data save intervals
   - Backup settings
   - Database schema options

3. **`panel.yml`** (GUI/Panels)
   - Guild panel settings (from `PanelConfiguration`)
   - Permissions panel (from `PermissionsPanelConfiguration`)
   - Guild vault/bank UI (from `GuildVaultConfiguration`)
   - Item configurations
   - Slot layouts
   - GUI titles and messages

4. **`gameplay.yml`** (Game Mechanics)
   - Guild creation costs and requirements
   - Guild limits (members, size)
   - Region mechanics
   - Guild heart settings (from `HeartConfiguration`)
   - TNT protection (from `TntProtectionConfiguration`)
   - PvP mechanics
   - Kill/death point system

5. **`diplomacy.yml`** (Relations)
   - Ally system (from `DiplomacyConfiguration`)
   - Enemy system
   - Relation limits
   - Alliance mechanics

6. **`security.yml`** (Protection)
   - Security system (from `SecuritySystemConfiguration`)
   - Freecam protection
   - Region entry notifications
   - Anti-exploit measures

7. **`display.yml`** (Visual/Display)
   - Top rankings/leaderboards (from `TopConfiguration`)
   - Scoreboard settings (from `ScoreboardConfiguration`)
   - Hologram settings
   - Tab list configuration
   - Name tags and prefixes

8. **`commands.yml`** (Commands)
   - Command settings (from `CommandsConfiguration`)
   - Command cooldowns
   - Command permissions
   - Disabled commands

9. **`events.yml`** (Event Logging)
   - Event log settings (from `EventLogConfiguration`)
   - Broadcast settings
   - Notification configuration

10. **`rank-system.yml`** (Ranking)
    - Point calculation
    - Rank decay
    - Top types enabled
    - Placeholder settings

### Phase 2: Implementation Plan

#### Step 1: Configuration Factory Update
Update `ConfigurationFactory.java` to:
- Load multiple YAML files
- Maintain backward compatibility with old config.yml
- Provide migration utilities

```java
public class ConfigurationFactory {
    public static PluginConfigurationContainer createConfigurations(File dataFolder) {
        File configDir = new File(dataFolder, "config");
        
        // Create config directory if it doesn't exist
        configDir.mkdirs();
        
        // Load individual config files
        CoreConfiguration core = loadConfig(new File(configDir, "config.yml"), CoreConfiguration.class);
        DatabaseConfiguration database = loadConfig(new File(configDir, "database.yml"), DatabaseConfiguration.class);
        PanelConfiguration panel = loadConfig(new File(configDir, "panel.yml"), PanelConfiguration.class);
        // ... etc
        
        return new PluginConfigurationContainer(core, database, panel, ...);
    }
}
```

#### Step 2: Migration Tool
Create automatic migration from old single config.yml:

```java
public class ConfigMigrator {
    public void migrateFromLegacyConfig(File oldConfig, File newConfigDir) {
        // Read old config.yml
        // Split into logical sections
        // Write to new individual files
        // Backup old config as config.yml.backup
    }
}
```

#### Step 3: Documentation Updates
- Update Wiki with new configuration structure
- Provide migration guide
- Document each config file purpose
- Add examples for each file

#### Step 4: Backward Compatibility
- Detect old config.yml format on startup
- Auto-migrate if no new config files exist
- Log migration process
- Warn users about deprecated structure

### Phase 3: Benefits

#### For Users
- **Easier navigation**: Find settings quickly in relevant files
- **Better organization**: Related settings grouped together
- **Faster editing**: Open only the file you need
- **Less intimidating**: Smaller files are easier to understand
- **Clearer purpose**: File names indicate content

#### For Developers
- **Modular code**: Easier to add/modify settings
- **Better testing**: Test individual config sections
- **Reduced conflicts**: Multiple devs can work on different configs
- **Cleaner PRs**: Changes to specific areas more visible
- **Type safety**: Each config can have its own validation

#### For Server Performance
- **Selective loading**: Load only needed configs
- **Faster parsing**: Smaller files parse quicker
- **Memory efficiency**: Can unload unused configs
- **Better caching**: Cache individual config sections

## Implementation Checklist

### Phase 1: Planning & Design
- [ ] Review all configuration options in PluginConfiguration
- [ ] Design optimal file structure and naming
- [ ] Create config section mapping document
- [ ] Design migration strategy
- [ ] Plan backward compatibility approach

### Phase 2: Core Implementation
- [ ] Create new configuration classes for each file
- [ ] Implement ConfigurationFactory multi-file loading
- [ ] Create PluginConfigurationContainer to hold all configs
- [ ] Update FunnyGuilds.java to use new structure
- [ ] Update all references throughout codebase

### Phase 3: Migration & Compatibility
- [ ] Implement ConfigMigrator tool
- [ ] Add legacy config detection
- [ ] Add automatic migration on first startup
- [ ] Create backup mechanism for old config
- [ ] Add config validation for each file

### Phase 4: Testing
- [ ] Unit tests for each config class
- [ ] Integration tests for multi-file loading
- [ ] Migration tests (old → new)
- [ ] Backward compatibility tests
- [ ] Performance benchmarks

### Phase 5: Documentation
- [ ] Update Wiki with new structure
- [ ] Create migration guide
- [ ] Document each config file
- [ ] Add inline comments to default configs
- [ ] Create video tutorial (optional)

### Phase 6: Release
- [ ] Release as beta/RC first
- [ ] Gather community feedback
- [ ] Fix issues found in testing
- [ ] Release stable version
- [ ] Monitor for issues post-release

## Breaking Changes & Migration

### User Impact
- **Low**: Automatic migration handles conversion
- **Backup**: Old config.yml backed up automatically
- **Rollback**: Can restore old config if needed

### API Impact
- **Internal**: Plugin code needs updates
- **External**: Third-party integrations need updates if they access config directly
- **Mitigation**: Provide compatibility layer during transition period

## Technical Considerations

### File Structure
```
plugins/FunnyGuilds/
├── config/
│   ├── config.yml          (core settings)
│   ├── database.yml        (database config)
│   ├── panel.yml           (GUI settings)
│   ├── gameplay.yml        (game mechanics)
│   ├── diplomacy.yml       (relations)
│   ├── security.yml        (protection)
│   ├── display.yml         (visual/display)
│   ├── commands.yml        (commands)
│   ├── events.yml          (event logging)
│   └── rank-system.yml     (ranking)
├── messages_en.yml
├── messages_pl.yml
└── tablist.yml
```

### Config Validation
- Each file gets its own validation rules
- Cross-file validation where needed
- Clear error messages pointing to specific files
- Validation on load and on reload

### Performance Considerations
- Lazy loading where possible
- Cache frequently accessed values
- Reload individual files instead of all
- Watch for file changes efficiently

## Timeline Estimate

- **Phase 1 (Planning)**: 2-3 days
- **Phase 2 (Implementation)**: 1-2 weeks
- **Phase 3 (Migration)**: 3-4 days
- **Phase 4 (Testing)**: 1 week
- **Phase 5 (Documentation)**: 3-4 days
- **Phase 6 (Release & Support)**: 1 week

**Total Estimated Time**: 3-4 weeks

## Open Questions

1. Should we support both old and new config formats long-term, or deprecate old format?
2. What's the migration path for users with custom config management tools?
3. Should config files be in a subdirectory or in the main plugin folder?
4. How to handle config reloading - reload all or per-file?
5. Should there be a command to convert back to single file format?

## Alternatives Considered

### Alternative 1: Keep Single File with Better Organization
**Pros**: No migration needed, simpler maintenance
**Cons**: Doesn't solve navigation issues, still too large

### Alternative 2: Split into Just 3-4 Files
**Pros**: Less complexity than 10 files
**Cons**: Still have large files, less clear organization

### Alternative 3: Dynamic Config Sections (no migration)
**Pros**: No user impact
**Cons**: Doesn't improve user experience

## Conclusion

Splitting the configuration into multiple focused files is a significant improvement that benefits both users and developers. The existing section classes provide a strong foundation for this refactoring. With proper migration tools and backward compatibility, the transition can be smooth and low-risk.

## Related Issues

- Original Issue: #18 (Full refactoring: 1.21.4+ compatibility, deprecated API cleanup & config restructuring)
- This proposal addresses the "config restructuring" portion deferred from PR #XXX

## Next Steps

1. Review and approve this proposal
2. Create detailed implementation plan
3. Assign implementation tasks
4. Begin Phase 1 (Planning & Design)
5. Iterate based on feedback

---

**Author**: GitHub Copilot  
**Date**: 2025-12-25  
**Status**: Proposal / Discussion  
**Related PR**: #XXX (Deprecated code removal)
