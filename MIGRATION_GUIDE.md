# Przewodnik Migracji - FunnyGuilds API Changes

Ten dokument opisuje zmiany w API które mogą wpłynąć na kod używający FunnyGuilds jako zależności lub rozszerzający plugin.

## Usunięte Metody i Ich Zamienniki

### UserCache.getDamageHistory() → User.getDamageState()

**Stara metoda (usunięta)**:
```java
User user = ...;
DamageState damageState = user.getCache().getDamageHistory();
```

**Nowa metoda (zalecana)**:
```java
User user = ...;
DamageState damageState = user.getDamageState();
```

**Alternatywnie (bezpośrednie użycie)**:
```java
UUID uuid = ...;
DamageState damageState = FunnyGuilds.getInstance()
    .getDamageManager()
    .getDamageState(uuid);
```

**Dlaczego zmiana?**
- Lepsza lokalizacja metody (bezpośrednio w User, a nie w Cache)
- Prostszy i bardziej intuicyjny API
- Eliminacja niepotrzebnej warstwy pośredniej

---

### FunnyFormatter.format(String) → FunnyFormatter.replace(String)

**Stara metoda (usunięta)**:
```java
FunnyFormatter formatter = new FunnyFormatter()
    .register("{PLAYER}", player.getName());
String result = formatter.format(message);
```

**Nowa metoda (zalecana)**:
```java
FunnyFormatter formatter = new FunnyFormatter()
    .register("{PLAYER}", player.getName());
String result = formatter.replace(message);
```

**Uwaga**: Statyczna metoda `FunnyFormatter.format()` NIE jest deprecated:
```java
// To nadal działa i jest OK!
String result = FunnyFormatter.format(text, "{PLAYER}", player.getName());
```

**Dlaczego zmiana?**
- Spójność nazewnictwa z interfejsem `Replaceable`
- `replace` lepiej opisuje co metoda robi
- Statyczna metoda `format()` pozostaje dla wygody

---

## Usunięte Metody (Wymaga Migracji)

### User.canManage() → GuildPermissionChecker

**Stara metoda (usunięta w commit 9ec7089)**:
```java
User user = ...;
if (user.canManage()) {
    // użytkownik może zarządzać gildią
}
```

**Nowa metoda (zalecana)**:
```java
GuildPermissionChecker checker = ...;
Guild guild = ...;
User user = ...;

// Sprawdzenie konkretnego uprawnienia
if (checker.hasPermission(guild, user, GenericGuildPermissions.INVITE_MEMBERS)) {
    // użytkownik może zapraszać członków
}

// Lub sprawdzenie wielu uprawnień
if (user.isOwner() || user.isDeputy()) {
    // równoważne do starego canManage()
}
```

**Dlaczego zmiana?**
- `canManage()` była zbyt uproszczona (tylko owner OR deputy)
- Nowy system pozwala na bardziej szczegółową kontrolę uprawnień
- Możliwość customowych uprawnień dla różnych ról
- Brak użyć w kodzie projektu

---

## Deprecated Metody (Już Usunięte)

Te metody były wcześniej deprecated ale zostały już usunięte. Jeśli je używasz, musisz zmigrować:

### ~~User.canManage()~~ - USUNIĘTA

Zobacz sekcję powyżej dla szczegółów migracji.

---

## Deprecated Metody (Nadal Dostępne)

Te metody NIE zostały jeszcze usunięte ale są deprecated. Zalecamy migrację:

### RegionManager.deleteRegion() - Scheduled for 5.0

**Status**: Deprecated, scheduled for removal in version 5.0 (GH-1402)


## Zmiany w Event Handling (Paper)

### AsyncPlayerChatEvent → AsyncChatEvent

Jeśli Twój kod nasłuchuje na chat events:

**Stary kod (deprecated na Paper 1.19+)**:
```java
@EventHandler
public void onChat(AsyncPlayerChatEvent event) {
    String message = event.getMessage();
    event.setFormat("§7[§aGuild§7] " + event.getFormat());
}
```

**Nowy kod (Paper 1.19+)**:
```java
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@EventHandler
public void onChat(AsyncChatEvent event) {
    // Konwersja Component -> String dla legacy kodu
    String message = LegacyComponentSerializer.legacySection()
        .serialize(event.message());
    
    // Formatowanie za pomocą renderer
    event.renderer((source, sourceDisplayName, messageComponent, viewer) -> {
        // Twoje custom formatowanie
        String formatted = "§7[§aGuild§7] " + 
            LegacyComponentSerializer.legacySection().serialize(sourceDisplayName) + 
            ": " + 
            LegacyComponentSerializer.legacySection().serialize(messageComponent);
        
        return LegacyComponentSerializer.legacySection().deserialize(formatted);
    });
}
```

**Uwaga**: Jeśli używasz Spigot (nie Paper), stary event nadal działa.

---

## Kompatybilność

### Wymagane Wersje

| Komponent | Minimalna Wersja | Zalecana Wersja |
|-----------|-----------------|-----------------|
| Minecraft | 1.21.0 | 1.21.4+ |
| Java | 21 | 21 |
| Paper | 1.21-R0.1 | 1.21.4-R0.1 |

### Paper vs Spigot

- **AsyncChatEvent** wymaga Paper (nie działa na czystym Spigot)
- Jeśli musisz wspierać Spigot, rozważ dual listener pattern:
  
```java
// Paper listener
@EventHandler
public void onChatPaper(AsyncChatEvent event) {
    handleChat(event.getPlayer(), 
        LegacyComponentSerializer.legacySection().serialize(event.message()));
}

// Spigot fallback
@EventHandler
public void onChatSpigot(AsyncPlayerChatEvent event) {
    handleChat(event.getPlayer(), event.getMessage());
}

private void handleChat(Player player, String message) {
    // Wspólna logika
}
```

---

## Pytania i Odpowiedzi

### Q: Czy muszę migrować od razu?

**A**: Nie, dla większości zmian. Metody oznaczone jako `@Deprecated` nadal działają. Jednak:
- Usunięte metody (UserCache.getDamageHistory, FunnyFormatter.format) wymagają migracji
- AsyncChatEvent wymaga Paper i migracji dla 1.19+

### Q: Co się stanie jeśli nie zmigruję?

**A**: 
- Dla usuniętych metod: kod się nie skompiluje
- Dla deprecated metod: kod będzie działać ale z ostrzeżeniami kompilatora
- W FunnyGuilds 5.0 wszystkie deprecated metody mogą zostać usunięte

### Q: Gdzie znaleźć więcej informacji?

**A**: 
- [DEPRECATED_METHODS_ANALYSIS.md](./DEPRECATED_METHODS_ANALYSIS.md) - pełna analiza
- [CONFIG_REFACTORING_PROPOSAL.md](./CONFIG_REFACTORING_PROPOSAL.md) - refaktoryzacja konfiguracji
- [FunnyGuilds Wiki](https://github.com/dzikoysk/FunnyGuilds/wiki) - dokumentacja

---

## Timeline Zmian

### ✅ Wersja 5.0.0-SNAPSHOT (Aktualna)
- ✅ Migracja AsyncChatEvent
- ✅ Usunięcie UserCache.getDamageHistory() → User.getDamageState()
- ✅ Usunięcie FunnyFormatter.format() (instance method)
- ✅ Usunięcie User.canManage() (commit 9ec7089)
- ✅ Dodanie testów dla User.getDamageState()
- ⚠️ Deprecated: RegionManager.deleteRegion()

### 🔮 Planowane w 5.0.0 (Final)
- Usunięcie RegionManager.deleteRegion()
- Refaktoryzacja bazy danych (GH-1402)
- Zmiana pola "attacked" → "protection"
- Usunięcie przestarzałych placeholderów

---

## Wsparcie

Jeśli masz pytania lub problemy z migracją:
1. Sprawdź [Issues na GitHub](https://github.com/dzikoysk/FunnyGuilds/issues)
2. Otwórz nowy Issue z tagiem `migration`
3. Dołącz do [Discord]() jeśli dostępny

---

**Ostatnia aktualizacja**: 2026-01-10  
**Wersja dokumentu**: 1.1  
**FunnyGuilds wersja**: 5.0.0-SNAPSHOT  
**Ostatni commit**: 9ec7089 (User.canManage() removal + tests)
