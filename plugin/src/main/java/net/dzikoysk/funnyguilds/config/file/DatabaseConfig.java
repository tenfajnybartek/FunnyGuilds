package net.dzikoysk.funnyguilds.config.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.annotation.Variable;
import net.dzikoysk.funnyguilds.config.PluginConfiguration.DataModel;
import org.jetbrains.annotations.Nullable;

/**
 * Database configuration file (database.yml)
 * Contains database connection settings and data storage type.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("       FunnyGuilds Database     #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Konfiguracja bazy danych")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class DatabaseConfig extends OkaeriConfig {

    @Comment("")
    @Comment("Typ zapisu danych:")
    @Comment(" FLAT - lokalne pliki")
    @Comment(" MYSQL - baza danych, kompatybilna z MySQL")
    @Comment(" MARIADB - baza danych, kompatybilna z MariaDB")
    public DataModel dataModel = DataModel.FLAT;

    @Comment("")
    @Comment("Dane wymagane do połączenia z bazą")
    @Comment("UWAGA: connectionTimeout jest w milisekundach!")
    @Comment(" ")
    @Comment("Sekcja poolSize odpowiada za liczbę zarezerwowanych połączeń, domyślna wartość 5 powinna wystarczyć")
    @Comment("Aby umożliwić FG automatyczne zarządzanie liczbą połączeń - ustaw poolSize na -1")
    @Comment(" ")
    @Comment("Sekcje usersTableName, guildsTableName i regionsTableName to nazwy tabel z danymi FG w bazie danych")
    @Comment("Najlepiej zmieniać te nazwy tylko wtedy, gdy np. występuje konflikt z innym pluginem")
    @Comment("Aby zmienić nazwy tabel, gdy masz juz w bazie jakieś dane z FG:")
    @Comment("1. Wyłącz serwer")
    @Comment("2. Zmień dane w configu FG")
    @Comment("3. Zmień nazwy tabel w bazie używając np. phpMyAdmin")
    public MysqlConfig mysql = new MysqlConfig();

    @Names(strategy = NameStrategy.IDENTITY)
    public static class MysqlConfig extends OkaeriConfig {

        @Variable("FG_MYSQL_HOSTNAME")
        public String hostname = "localhost";
        @Variable("FG_MYSQL_PORT")
        public int port = 3306;
        @Variable("FG_MYSQL_DATABASE")
        public String database = "db";
        @Variable("FG_MYSQL_USER")
        public String user = "root";
        @Variable("FG_MYSQL_PASSWORD")
        public String password = "passwd";

        @Variable("FG_MYSQL_POOL_SIZE")
        public int poolSize = 5;
        @Variable("FG_MYSQL_CONNECTION_TIMEOUT")
        public int connectionTimeout = 30000;
        @Variable("FG_MYSQL_USE_SSL")
        public boolean useSSL = true;
        @Variable("FG_MYSQL_CHARACTER_ENCODING")
        public String characterEncoding = "";

        @Variable("FG_MYSQL_USERS_TABLE_NAME")
        public String usersTableName = "users";
        @Variable("FG_MYSQL_GUILDS_TABLE_NAME")
        public String guildsTableName = "guilds";
        @Variable("FG_MYSQL_REGIONS_TABLE_NAME")
        public String regionsTableName = "regions";
        @Variable("FG_MYSQL_MEMBER_PERMISSIONS_TABLE_NAME")
        public String memberPermissionsTableName = "member_permissions";

    }

}
