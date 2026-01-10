# Plan Refaktoryzacji Bazy Danych - Usunięcie FLAT Storage

## Cel
Usunięcie przestarzałego systemu FLAT (pliki YML) i pozostawienie tylko SQL (SQLite + MySQL/MariaDB) jako jedynego storage backend.

## Korzyści
- ✅ Usunięcie ~977 linii kodu (5 plików w `data/flat/`)
- ✅ Eliminacja duplikacji serializerów
- ✅ Jeden storage backend = prostsze testy i maintenance
- ✅ SQLite jest równie lekki jak FLAT, ale z możliwościami SQL
- ✅ Łatwiejsza migracja MySQL ↔ SQLite
- ✅ Brak konieczności utrzymywania dwóch różnych systemów

## Uwagi Wstępne

### System Regeneracji
**Status**: ✅ **Niezależny i bezpieczny**
- System regeneracji używa własnego folderu: `plugins/FunnyGuilds/regeneration/`
- Własne pliki YML: `{guildUUID}.yml` dla każdej gildii
- **Nie wymaga zmian** - działa identycznie dla FLAT i SQL

### Struktura Po Zmianach
```
plugins/FunnyGuilds/
├── database/       # SQL data (SQLite lub MySQL/MariaDB)
└── regeneration/   # YML regeneracji (bez zmian)
```

---

## Część 1: Przygotowanie i Dodanie SQLite (Łatwe - 30 min)

### 1.1. Dodanie SQLite do DataModel Enum
**Plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/config/PluginConfiguration.java`

```java
public enum DataModel {
    SQLITE("org.sqlite.JDBC"),     // NOWE!
    MYSQL("com.mysql.cj.jdbc.Driver"),
    MARIADB("org.mariadb.jdbc.Driver");
    // FLAT usunięte

    private final String jdbcClassName;

    DataModel(String jdbcClassName) {
        this.jdbcClassName = jdbcClassName;
    }

    public boolean isSQL() {
        return this.jdbcClassName != null; // Zawsze true teraz
    }

    public String getJDBCClassName() {
        return jdbcClassName;
    }
}
```

### 1.2. Aktualizacja Konfiguracji
**Plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/config/PluginConfiguration.java`

**Przed**:
```java
@Comment(" FLAT - lokalne pliki")
@Comment(" MYSQL - baza danych MySQL")
@Comment(" MARIADB - baza danych MariaDB")
public DataModel dataModel = DataModel.FLAT;
```

**Po**:
```java
@Comment(" SQLITE - lokalna baza danych SQLite (zalecane dla małych/średnich serwerów)")
@Comment(" MYSQL - baza danych MySQL (zalecane dla dużych serwerów)")
@Comment(" MARIADB - baza danych MariaDB")
public DataModel dataModel = DataModel.SQLITE;
```

### 1.3. Dodanie SQLite Connection String
**Plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/data/database/Database.java`

```java
public Database connect() throws SQLException {
    PluginConfiguration.DataModel modelType = this.config.dataModel;
    
    if (modelType == PluginConfiguration.DataModel.SQLITE) {
        // SQLite - lokalny plik
        File dbFile = new File(plugin.getPluginDataFolder(), "database.db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        this.connection = DriverManager.getConnection(url);
    } else {
        // MySQL/MariaDB - zdalne połączenie
        String url = "jdbc:mysql://" + config.mysqlHostname + ":" 
                   + config.mysqlPort + "/" + config.mysqlDatabase
                   + "?useSSL=" + config.mysqlUseSSL
                   + "&characterEncoding=utf-8";
        this.connection = DriverManager.getConnection(url, 
            config.mysqlUser, config.mysqlPassword);
    }
    
    return this;
}
```

### 1.4. Checklist Część 1
- [ ] Dodać `SQLITE` do enum `DataModel`
- [ ] Usunąć `FLAT` z enum `DataModel`
- [ ] Zmienić domyślną wartość z `FLAT` na `SQLITE`
- [ ] Zaktualizować komentarze w konfiguracji
- [ ] Dodać logikę połączenia SQLite w `Database.java`
- [ ] Przetestować połączenie SQLite
- [ ] Commit: "Add SQLite support and remove FLAT from DataModel enum"

---

## Część 2: Usunięcie FlatDataModel i Factory (Średnie - 1h)

### 2.1. Aktualizacja Factory Method
**Plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/data/DataModel.java`

**Przed**:
```java
static DataModel create(FunnyGuilds plugin, PluginConfiguration.DataModel modelType) {
    if (modelType.isSQL()) {
        return new SQLDataModel(plugin);
    }
    
    return new FlatDataModel(plugin);
}
```

**Po**:
```java
static DataModel create(FunnyGuilds plugin, PluginConfiguration.DataModel modelType) {
    // Wszystkie opcje teraz są SQL-based
    return new SQLDataModel(plugin);
}
```

### 2.2. Usunięcie Importów FlatDataModel
Pliki do sprawdzenia:
- `plugin/src/main/java/net/dzikoysk/funnyguilds/data/DataModel.java`
- `plugin/src/main/java/net/dzikoysk/funnyguilds/FunnyGuilds.java`

Usunąć:
```java
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
```

### 2.3. Usunięcie Całego Pakietu `data/flat/`
Pliki do usunięcia:
```
plugin/src/main/java/net/dzikoysk/funnyguilds/data/flat/
├── FlatDataModel.java (~280 linii)
├── seralizer/
    ├── FlatGuildSerializer.java (~248 linii)
    ├── FlatRegionSerializer.java (~183 linii)
    └── FlatUserSerializer.java (~266 linii)
```

**Łącznie**: ~977 linii kodu usuniętych!

### 2.4. Checklist Część 2
- [ ] Uprościć `DataModel.create()` - zawsze zwraca `SQLDataModel`
- [ ] Usunąć import `FlatDataModel` z `DataModel.java`
- [ ] Usunąć import `FlatDataModel` z `FunnyGuilds.java`
- [ ] Usunąć `FlatDataModel.java`
- [ ] Usunąć `FlatGuildSerializer.java`
- [ ] Usunąć `FlatRegionSerializer.java`
- [ ] Usunąć `FlatUserSerializer.java`
- [ ] Usunąć folder `data/flat/seralizer/`
- [ ] Usunąć folder `data/flat/`
- [ ] Sprawdzić czy kompiluje się bez błędów
- [ ] Commit: "Remove FLAT storage backend and all flat serializers"

---

## Część 3: Migration Guide i Dokumentacja (Łatwe - 30 min)

### 3.1. Utworzenie Migration Guide
**Nowy plik**: `FLAT_TO_SQL_MIGRATION.md`

```markdown
# Migracja z FLAT na SQLite/MySQL

## Dla Użytkowników FLAT

Jeśli aktualnie używasz `dataModel: FLAT` w konfiguracji, musisz przemigrować do SQL.

### Rekomendacja: SQLite (Najprostsze)

SQLite jest równie wydajny jak FLAT dla małych/średnich serwerów i nie wymaga zewnętrznej bazy danych.

### Kroki Migracji FLAT → SQLite

1. **Backup**: Skopiuj folder `plugins/FunnyGuilds/data/` (twoje dane)

2. **Zatrzymaj serwer**

3. **Zmień konfigurację**:
   ```yaml
   # config.yml
   dataModel: SQLITE  # było: FLAT
   ```

4. **Uruchom serwer pierwszy raz** - utworzy pustą bazę SQLite

5. **Zatrzymaj serwer**

6. **Zaimportuj dane** - użyj narzędzia migracji (patrz niżej)

7. **Uruchom serwer** - sprawdź czy wszystko działa

### Narzędzie Migracji

```bash
# TODO: Dodać skrypt migracji lub instrukcje manualne
```

### Alternatywa: MySQL/MariaDB

Jeśli masz dużą społeczność lub współdzielony hosting:

```yaml
dataModel: MYSQL
mysqlHostname: "localhost"
mysqlPort: 3306
mysqlDatabase: "funnyguilds"
mysqlUser: "root"
mysqlPassword: "password"
mysqlUseSSL: false
```
```

### 3.2. Aktualizacja Głównego README
Dodać sekcję o storage:

```markdown
## Storage Options

FunnyGuilds supports SQL-based storage:

- **SQLite** (recommended for small/medium servers) - Local file database, no setup required
- **MySQL** (recommended for large servers) - Remote database with better performance at scale
- **MariaDB** - MySQL alternative
```

### 3.3. Aktualizacja DEPRECATED_METHODS_ANALYSIS.md
Dodać do sekcji Faza 3:

```markdown
### ✅ ZAKOŃCZONE: Usunięcie FLAT Storage Backend

**Data**: 2026-01-10
**Commits**: [TODO]

**Zmiany:**
- Usunięto `DataModel.FLAT` enum value
- Usunięto cały pakiet `data/flat/` (~977 linii)
- Dodano `DataModel.SQLITE` jako domyślny i zalecany backend
- Domyślna konfiguracja: SQLite zamiast FLAT

**Breaking Changes:**
- Użytkownicy z `dataModel: FLAT` muszą przemigrować do SQLite lub MySQL
- Patrz: FLAT_TO_SQL_MIGRATION.md

**Korzyści:**
- -977 linii kodu
- Jeden storage backend = prostsze testy
- Brak duplikacji serializerów
- SQLite równie wydajny jak FLAT, ale z SQL
```

### 3.4. Checklist Część 3
- [ ] Utworzyć `FLAT_TO_SQL_MIGRATION.md` z instrukcjami
- [ ] Zaktualizować README.md o sekcję Storage Options
- [ ] Zaktualizować DEPRECATED_METHODS_ANALYSIS.md
- [ ] Zaktualizować CHANGELOG_SUMMARY.md
- [ ] Commit: "Add migration guide and update documentation for FLAT removal"

---

## Timeline i Strategia

### Sesja 1 (Dzisiaj): Część 1
- Czas: ~30 minut
- Ryzyko: Niskie
- Dodać SQLite, usunąć FLAT z enum
- **Test**: Uruchomić serwer z SQLite

### Sesja 2: Część 2
- Czas: ~1 godzina
- Ryzyko: Średnie
- Usunąć FlatDataModel i wszystkie flat serializers
- **Test**: Kompilacja + uruchomienie z SQLite

### Sesja 3: Część 3
- Czas: ~30 minut
- Ryzyko: Niskie
- Dokumentacja i migration guide
- **Test**: Code review

---

## Testy Po Każdej Części

### Test 1: Po Części 1
```bash
# Sprawdź czy kompiluje się
./gradlew build -x test

# Sprawdź czy serwer startuje z SQLite
# 1. Zmień config na SQLITE
# 2. Uruchom serwer
# 3. Sprawdź logi - czy połączył się z SQLite
# 4. Wykonaj podstawowe operacje (create guild, etc.)
```

### Test 2: Po Części 2
```bash
# Sprawdź czy kompiluje się bez flat/
./gradlew build -x test

# Sprawdź czy serwer działa bez FLAT
# 1. Uruchom serwer z SQLite
# 2. Sprawdź pełną funkcjonalność
# 3. Restart serwera - czy dane się ładują
```

### Test 3: Po Części 3
```bash
# Code review dokumentacji
# Sprawdź czy wszystkie linki działają
# Sprawdź czy migration guide jest kompletny
```

---

## Rollback Plan

Jeśli coś pójdzie nie tak:

### Po Części 1
```bash
git revert <commit-hash>
# Przywróci FLAT jako opcję
```

### Po Części 2
```bash
git revert <commit-hash-part2>
git revert <commit-hash-part1>
# Przywróci cały FLAT backend
```

---

## Pytania i Odpowiedzi

### Q: Czy SQLite jest wystarczająco wydajny?
**A**: Tak! SQLite radzi sobie z tysiącami operacji/s. Dla serwerów do 500 graczy jest idealny.

### Q: Co z istniejącymi użytkownikami FLAT?
**A**: Muszą przemigrować do SQLite (zalecane) lub MySQL. Dostarczymy migration guide.

### Q: Czy mogę wrócić do FLAT?
**A**: Nie w najnowszej wersji. Musisz zostać na starszej wersji pluginu lub przemigrować.

### Q: Jak działa migracja danych?
**A**: TODO - dodać skrypt lub instrukcje manualne w następnej sesji.

---

## Podsumowanie

| Część | Czas | Ryzyko | Pliki | Linie | Status |
|-------|------|--------|-------|-------|--------|
| 1. SQLite + Remove FLAT enum | 30 min | Niskie | 3 | +50/-10 | ⏳ Oczekuje |
| 2. Remove FlatDataModel | 1h | Średnie | 6 | +5/-977 | ⏳ Oczekuje |
| 3. Documentation | 30 min | Niskie | 4 | +200/-0 | ⏳ Oczekuje |
| **RAZEM** | **2h** | - | **13** | **-732** | - |

**Końcowy efekt**: -732 linii kodu (netto), prostszy system, jeden backend!

---

## Następne Kroki

Po zakończeniu tej refaktoryzacji możemy przejść do:
1. Usunięcia `RegionManager.deleteRegion()` (teraz prostsze!)
2. Opcjonalnej migracji Adventure API (Faza 4)
3. Innych ulepszeń z roadmapy

---

*Dokument utworzony: 2026-01-10*
*Autor: GitHub Copilot*
