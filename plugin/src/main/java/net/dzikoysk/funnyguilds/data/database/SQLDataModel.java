package net.dzikoysk.funnyguilds.data.database;

import java.sql.SQLException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.element.SQLBasicUtils;
import net.dzikoysk.funnyguilds.data.database.element.SQLTable;
import net.dzikoysk.funnyguilds.data.database.element.SQLType;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseGuildSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseMemberPermissionsSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseRegionSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseUserSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseVaultSerializer;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.FunnyValidator;
import net.dzikoysk.funnyguilds.shared.FunnyValidator.NameResult;
import panda.std.Option;

public class SQLDataModel implements DataModel {

    private final FunnyGuilds plugin;
    private final PluginConfiguration pluginConfiguration;

    private final SQLTable usersTable;
    private final SQLTable guildsTable;
    private final SQLTable regionsTable;
    private final SQLTable memberPermissionsTable;
    private final SQLTable vaultsTable;


    public SQLDataModel(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.pluginConfiguration = plugin.getPluginConfiguration();

        this.usersTable = new SQLTable(this.pluginConfiguration.mysql.usersTableName);
        this.guildsTable = new SQLTable(this.pluginConfiguration.mysql.guildsTableName);
        this.regionsTable = new SQLTable(this.pluginConfiguration.mysql.regionsTableName);
        this.memberPermissionsTable = new SQLTable(this.pluginConfiguration.mysql.memberPermissionsTableName);
        this.vaultsTable = new SQLTable(this.pluginConfiguration.mysql.guildsTableName + "_vaults");

        this.prepareTables();
    }

    public void prepareTables() {
        this.usersTable.add("uuid", SQLType.VARCHAR, 36, true);
        this.usersTable.add("name", SQLType.TEXT, true);
        this.usersTable.add("points", SQLType.INT, true);
        this.usersTable.add("kills", SQLType.INT, true);
        this.usersTable.add("deaths", SQLType.INT, true);
        this.usersTable.add("assists", SQLType.INT, true);
        this.usersTable.add("logouts", SQLType.INT, true);
        this.usersTable.add("ban", SQLType.BIGINT);
        this.usersTable.add("reason", SQLType.TEXT);
        this.usersTable.setPrimaryKey("uuid");

        this.guildsTable.add("uuid", SQLType.VARCHAR, 100, true);
        this.guildsTable.add("name", SQLType.TEXT, true);
        this.guildsTable.add("tag", SQLType.TEXT, true);
        this.guildsTable.add("owner", SQLType.TEXT, true);
        this.guildsTable.add("home", SQLType.TEXT, true);
        this.guildsTable.add("region", SQLType.TEXT, true);
        this.guildsTable.add("regions", SQLType.TEXT, true);
        this.guildsTable.add("members", SQLType.TEXT, true);
        this.guildsTable.add("points", SQLType.INT, true);
        this.guildsTable.add("lives", SQLType.INT, true);
        this.guildsTable.add("ban", SQLType.BIGINT, true);
        this.guildsTable.add("born", SQLType.BIGINT, true);
        this.guildsTable.add("validity", SQLType.BIGINT, true);
        this.guildsTable.add("pvp", SQLType.BOOLEAN, true);
        this.guildsTable.add("protection", SQLType.BIGINT); // Changed from "attacked" in 5.0
        this.guildsTable.add("allies", SQLType.TEXT);
        this.guildsTable.add("enemies", SQLType.TEXT);
        this.guildsTable.add("info", SQLType.TEXT);
        this.guildsTable.add("deputy", SQLType.TEXT);
        this.guildsTable.setPrimaryKey("uuid");

        this.regionsTable.add("name", SQLType.VARCHAR, 100, true);
        this.regionsTable.add("center", SQLType.TEXT, true);
        this.regionsTable.add("size", SQLType.INT, true);
        this.regionsTable.add("enlarge", SQLType.INT, true);
        this.regionsTable.setPrimaryKey("name");
        
        // Member permissions table
        this.memberPermissionsTable.add("id", SQLType.INT, true);
        this.memberPermissionsTable.add("guild_uuid", SQLType.VARCHAR, 36, true);
        this.memberPermissionsTable.add("member_uuid", SQLType.VARCHAR, 36, true);
        this.memberPermissionsTable.add("permission_type", SQLType.VARCHAR, 100, true);
        this.memberPermissionsTable.add("permission_value", SQLType.BOOLEAN, true);
        this.memberPermissionsTable.add("changed_by", SQLType.VARCHAR, 36);
        this.memberPermissionsTable.add("changed_at", SQLType.BIGINT);
        this.memberPermissionsTable.setPrimaryKey("id");
        
        // Vaults table
        this.vaultsTable.add("guild_uuid", SQLType.VARCHAR, 36, true);
        this.vaultsTable.add("balance", SQLType.DOUBLE, true);
        this.vaultsTable.add("items", SQLType.LONGTEXT);
        this.vaultsTable.setPrimaryKey("guild_uuid");
    }

    public void load() throws SQLException {
        createTableIfNotExists(this.usersTable);
        createTableIfNotExists(this.regionsTable);
        createTableIfNotExists(this.guildsTable);
        createTableIfNotExists(this.memberPermissionsTable);
        createTableIfNotExists(this.vaultsTable);

        this.loadUsers();
        this.loadRegions();
        this.loadGuilds();

        this.plugin.getIndividualNameTagManager().map(ScoreboardGlobalUpdateSyncTask::new).peek(this.plugin::scheduleFunnyTasks);
    }

    public void loadUsers() {
        SQLBasicUtils.getSelectAll(this.usersTable).executeQuery(result -> {
            while (result.next()) {
                String userName = result.getString("name");

                if (FunnyValidator.validateUsername(this.pluginConfiguration, userName) != NameResult.VALID) {
                    FunnyGuilds.getPluginLogger().warning("Skipping loading of user '" + userName + "' - name is invalid");
                    continue;
                }

                DatabaseUserSerializer.deserialize(result);
            }
        });

        FunnyGuilds.getPluginLogger().info("Loaded users: " + this.plugin.getUserManager().countUsers());
    }

    public void loadGuilds() {
        GuildManager guildManager = this.plugin.getGuildManager();

        SQLBasicUtils.getSelectAll(this.guildsTable).executeQuery(resultAll -> {
            while (resultAll.next()) {
                DatabaseGuildSerializer.deserialize(resultAll);
            }
        });

        SQLBasicUtils.getSelect(this.guildsTable, "tag", "allies", "enemies").executeQuery(result -> {
            while (result.next()) {
                Option<Guild> guildOption = guildManager.findByTag(result.getString("tag"));
                if (guildOption.isEmpty()) {
                    continue;
                }

                Guild guild = guildOption.get();
                String alliesList = result.getString("allies");
                String enemiesList = result.getString("enemies");

                if (alliesList != null && !alliesList.isEmpty()) {
                    guild.setAllies(guildManager.findByNames(FunnyStringUtils.fromString(alliesList)));
                }

                if (enemiesList != null && !enemiesList.isEmpty()) {
                    guild.setEnemies(guildManager.findByNames(FunnyStringUtils.fromString(enemiesList)));
                }
            }
        });

        // Load member permissions for all guilds
        DatabaseMemberPermissionsSerializer.loadAllPermissions(this.memberPermissionsTable, guildManager);
        
        // Load vaults for all guilds
        DatabaseVaultSerializer.loadAllVaults(this.vaultsTable);

        guildManager.getGuilds().stream()
                .filter(guild -> guild.getOwner() == null)
                .forEach(guild -> guildManager.deleteGuild(FunnyGuilds.getInstance(), guild));

        FunnyGuilds.getPluginLogger().info("Loaded guilds: " + guildManager.countGuilds());
    }

    public void loadRegions() {
        if (!this.plugin.getPluginConfiguration().regionsEnabled) {
            FunnyGuilds.getPluginLogger().info("Regions are disabled and thus - not loaded");
            return;
        }

        RegionManager regionManager = this.plugin.getRegionManager();

        SQLBasicUtils.getSelectAll(this.regionsTable).executeQuery(result -> {
            while (result.next()) {
                DatabaseRegionSerializer.deserialize(result).peek(regionManager::addRegion);
            }
        });

        FunnyGuilds.getPluginLogger().info("Loaded regions: " + regionManager.countRegions());
    }

    @Override
    public void save(boolean ignoreNotChanged) {
        this.plugin.getUserManager().getUsers().stream()
                .filter(user -> !ignoreNotChanged || user.wasChanged())
                .forEach(DatabaseUserSerializer::serialize);

        this.plugin.getGuildManager().getGuilds().stream()
                .filter(guild -> !ignoreNotChanged || guild.wasChanged())
                .forEach(guild -> {
                    DatabaseGuildSerializer.serialize(guild);
                    DatabaseMemberPermissionsSerializer.savePermissions(this.memberPermissionsTable, guild);
                });
        
        // Save all vaults (check dirty flag internally)
        DatabaseVaultSerializer.saveAllVaults(this.vaultsTable, ignoreNotChanged);

        if (!this.plugin.getPluginConfiguration().regionsEnabled) {
            return;
        }

        this.plugin.getRegionManager().getRegions().stream()
                .filter(region -> !ignoreNotChanged || region.wasChanged())
                .forEach(DatabaseRegionSerializer::serialize);
    }

    public SQLTable getUsersTable() {
        return this.usersTable;
    }

    public SQLTable getGuildsTable() {
        return this.guildsTable;
    }

    public SQLTable getRegionsTable() {
        return this.regionsTable;
    }
    
    public SQLTable getMemberPermissionsTable() {
        return this.memberPermissionsTable;
    }
    
    public SQLTable getVaultsTable() {
        return this.vaultsTable;
    }

    private static void createTableIfNotExists(SQLTable table) {
        SQLBasicUtils.getCreate(table).executeUpdate();
        table.getSqlElements().forEach(sqlElement -> SQLBasicUtils.getAlter(table, sqlElement).executeUpdate(true));
    }

}
