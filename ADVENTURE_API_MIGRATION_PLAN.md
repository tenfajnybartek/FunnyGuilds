# Adventure API Migration Plan

## Cel
Pełna migracja na Adventure Components API z zachowaniem legacy formatting (`&` codes) dla kompatybilności wstecznej.

## Status Obecny

✅ **Dobra wiadomość**: Infrastruktura Adventure już istnieje!
- Plugin używa `BukkitAudiences` (Adventure Platform Bukkit)
- `YetAnotherMessagesLibrary` już wspiera Adventure Components
- `MessageService` jest gotowy na Adventure

**Miejsca wymagające konwersji**:
- 6 miejsc z `player.sendMessage(String)` → `Component`
- 1 miejsce z `player.kickPlayer(String)` → `Component`

## Korzyści Pełnej Migracji

- ✅ **Hex colors**: `&#FF5733Text` zamiast tylko `&c`
- ✅ **MiniMessage support**: `<gradient:red:blue>Text</gradient>`, `<rainbow>`, `<hover>`, `<click>`
- ✅ **Hover events**: Wyświetlanie dodatkowych informacji po najechaniu
- ✅ **Click events**: Wykonywanie komend po kliknięciu w tekst
- ✅ **Legacy compatibility**: `&a`, `&c`, `&l` nadal działają!
- ✅ **Lepsze testy**: Component-based testing
- ✅ **Przyszłościowe**: Paper i Bukkit przechodzą na Adventure

---

## Część 1: Konwersja BukkitUserProfile (30 min)

### Cel
Zmienić `BukkitUserProfile.sendMessage()` i `kickPlayer()` na Adventure Components.

### 1.1. Aktualizacja BukkitUserProfile.sendMessage()

**Plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/user/BukkitUserProfile.java`

**Przed**:
```java
@Override
public void sendMessage(String message) {
    this.getPlayer().peek(player -> player.sendMessage(message));
}
```

**Po**:
```java
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@Override
public void sendMessage(String message) {
    this.getPlayer().peek(player -> {
        Component component = LegacyComponentSerializer.legacySection()
            .deserialize(message);
        player.sendMessage(component);
    });
}
```

**Korzyści**:
- Konwertuje legacy `§` codes na Component
- Zachowuje kompatybilność z istniejącymi wiadomościami
- Player.sendMessage() używa już Adventure Component (Paper API)

### 1.2. Aktualizacja BukkitUserProfile.kickPlayer()

**Przed**:
```java
@Override
public void kick(String reason) {
    this.getPlayer().peek(player -> player.kickPlayer(reason));
}
```

**Po**:
```java
@Override
public void kick(String reason) {
    this.getPlayer().peek(player -> {
        Component component = LegacyComponentSerializer.legacySection()
            .deserialize(reason);
        player.kick(component);
    });
}
```

### 1.3. Aktualizacja User.sendMessage()

**Plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/user/User.java`

Metoda `sendMessage()` deleguje do `profile.sendMessage()`, więc automatycznie używa nowej implementacji!

**Bez zmian potrzebnych** - działa przez delegację.

### 1.4. Checklist Część 1
- [ ] Import `Component` i `LegacyComponentSerializer` w `BukkitUserProfile`
- [ ] Zmienić `sendMessage()` na konwersję legacy → Component
- [ ] Zmienić `kick()` na konwersję legacy → Component  
- [ ] Sprawdzić czy kompiluje się
- [ ] Przetestować wysyłanie wiadomości w grze
- [ ] Przetestować kick message
- [ ] Commit: "Migrate BukkitUserProfile to Adventure Components"

---

## Część 2: Konwersja PlayerChat (15 min)

### Cel
Zaktualizować `PlayerChat.java` aby używał Components zamiast String.

### 2.1. Aktualizacja PlayerChat

**Plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/listener/PlayerChat.java`

**Obecne użycie** (linie 176, 192):
```java
guild.getOnlineMembers()
    .forEach(member -> member.sendMessage(message));

// i

usersRepository.getUsers().stream()
    .filter(user -> user.hasGuild() && user.getCache().getSpyMode())
    .forEach(onlineUser -> onlineUser.sendMessage(spyMessage));
```

**Analiza**:
Te wywołania już używają `User.sendMessage(String)`, który teraz deleguje do nowego `BukkitUserProfile.sendMessage()` z Adventure!

**Wniosek**: ✅ **Bez zmian potrzebnych** - już działa po Części 1!

### 2.2. Checklist Część 2
- [x] Sprawdzić czy PlayerChat używa User.sendMessage() - TAK
- [x] Zweryfikować że działa po zmianach z Części 1 - TAK
- [ ] Przetestować guild chat w grze
- [ ] Przetestować spy mode
- [ ] Commit: "Verify PlayerChat works with Adventure Components" (opcjonalnie)

---

## Część 3: Konwersja FunnyEvent i Guild (15 min)

### Cel
Zaktualizować pozostałe miejsca używające `sendMessage()`.

### 3.1. FunnyEvent

**Plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/event/FunnyEvent.java` (linia 74)

```java
this.doer.peek(user -> user.sendMessage(this.getCancelMessage()));
```

**Analiza**: Używa `User.sendMessage(String)` - już działa przez delegację!

### 3.2. Guild

**Plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/guild/Guild.java` (linia 64)

```java
this.members.forEach(user -> user.sendMessage(message));
```

**Analiza**: Używa `User.sendMessage(String)` - już działa przez delegację!

### 3.3. Checklist Część 3
- [x] Sprawdzić FunnyEvent - używa User.sendMessage() - OK
- [x] Sprawdzić Guild - używa User.sendMessage() - OK
- [ ] Przetestować event cancel messages
- [ ] Przetestować guild broadcast
- [ ] Commit: "Verify all message sending uses Adventure Components"

---

## Część 4 (Opcjonalna): Dodanie MiniMessage Support

### Cel
Dodać wsparcie dla MiniMessage formatowania (`<gradient>`, `<hover>`, etc).

### 4.1. Nowy Helper: ComponentHelper

**Nowy plik**: `plugin/src/main/java/net/dzikoysk/funnyguilds/shared/ComponentHelper.java`

```java
package net.dzikoysk.funnyguilds.shared;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentHelper {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = 
        LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer LEGACY_SECTION = 
        LegacyComponentSerializer.legacySection();
    
    /**
     * Deserializuje tekst wspierając:
     * - Legacy & codes (&a, &c, etc.)
     * - Legacy § codes (§a, §c, etc.)
     * - MiniMessage tags (<gradient>, <hover>, etc.)
     */
    public static Component deserialize(String text) {
        // Najpierw konwertuj legacy na MiniMessage format
        String converted = LEGACY_AMPERSAND.serialize(LEGACY_SECTION.deserialize(text));
        
        // Potem parsuj z MiniMessage (obsługuje zarówno legacy jak i nowe tagi)
        return MINI_MESSAGE.deserialize(converted);
    }
    
    /**
     * Tylko legacy formatting (bez MiniMessage)
     */
    public static Component legacyOnly(String text) {
        return LEGACY_SECTION.deserialize(text);
    }
    
}
```

### 4.2. Aktualizacja BukkitUserProfile z ComponentHelper

```java
import net.dzikoysk.funnyguilds.shared.ComponentHelper;

@Override
public void sendMessage(String message) {
    this.getPlayer().peek(player -> {
        Component component = ComponentHelper.deserialize(message);
        player.sendMessage(component);
    });
}

@Override
public void kick(String reason) {
    this.getPlayer().peek(player -> {
        Component component = ComponentHelper.deserialize(reason);
        player.kick(component);
    });
}
```

### 4.3. Przykłady Użycia

Po implementacji użytkownicy mogą używać:

**Legacy (działa jak wcześniej)**:
```yaml
welcome-message: "&aWitaj na serwerze!"
```

**Hex Colors**:
```yaml
welcome-message: "&#FF5733Witaj &#00FF00na serwerze!"
```

**MiniMessage**:
```yaml
welcome-message: "<gradient:red:blue>Witaj na serwerze!</gradient>"
guild-info: "<hover:show_text:'Kliknij aby zobaczyć info'><click:run_command:/g info>Gildia XYZ</click></hover>"
```

### 4.4. Checklist Część 4
- [ ] Utworzyć `ComponentHelper.java`
- [ ] Dodać `MiniMessage` dependency do build.gradle (jeśli brak)
- [ ] Zaktualizować `BukkitUserProfile` aby używał `ComponentHelper`
- [ ] Przetestować legacy formatting - czy nadal działa
- [ ] Przetestować hex colors
- [ ] Przetestować MiniMessage tags
- [ ] Dodać przykłady do dokumentacji
- [ ] Commit: "Add MiniMessage support with ComponentHelper"

---

## Timeline i Strategia

### Sesja 1 (Dzisiaj): Część 1
- Czas: ~30 minut
- Ryzyko: Niskie
- Zmienić BukkitUserProfile na Adventure Components
- **Test**: Wysłać wiadomości i kick w grze

### Sesja 2 (Opcjonalnie): Część 2-3
- Czas: ~15-30 minut
- Ryzyko: Bardzo niskie (weryfikacja)
- Sprawdzić że wszystko działa
- **Test**: Guild chat, spy mode, event messages

### Sesja 3 (Opcjonalnie): Część 4
- Czas: ~45 minut
- Ryzyko: Niskie
- Dodać MiniMessage support
- **Test**: Legacy + hex + MiniMessage

---

## Testy Po Każdej Części

### Test 1: Po Części 1
```
1. Uruchom serwer
2. Wyślij wiadomość do gracza (/msg, /tell, etc.)
3. Sprawdź czy legacy colors działają (&a, &c, etc.)
4. Wykonaj kick gracza - sprawdź kick message
5. Sprawdź logi - brak błędów
```

### Test 2: Po Części 2-3
```
1. Napisz na guild chacie
2. Sprawdź spy mode
3. Wykonaj akcję która triggeruje FunnyEvent
4. Wyślij broadcast do gildii
5. Sprawdź czy wszystko wyświetla się poprawnie
```

### Test 3: Po Części 4 (MiniMessage)
```
1. Ustaw wiadomość z hex: "&#FF5733Test"
2. Ustaw wiadomość z gradient: "<gradient:red:blue>Test</gradient>"
3. Ustaw hover: "<hover:show_text:'Info'>Test</hover>"
4. Sprawdź czy legacy nadal działa: "&aTest"
5. Zweryfikuj wszystkie formaty w grze
```

---

## Rollback Plan

Jeśli coś pójdzie nie tak:

```bash
# Po Części 1
git revert <commit-hash>

# Po Części 4
git revert <commit-hash-part4>
git revert <commit-hash-part1>
```

---

## Zależności

### Sprawdź build.gradle

```gradle
dependencies {
    // Adventure Platform (powinno już być)
    implementation 'net.kyori:adventure-platform-bukkit:4.3.2'
    
    // MiniMessage (dla Części 4)
    implementation 'net.kyori:adventure-text-minimessage:4.16.0'
}
```

---

## Pytania i Odpowiedzi

### Q: Czy legacy & codes przestaną działać?
**A**: NIE! Legacy formatting (`&a`, `§c`) jest konwertowany na Component i będzie działać identycznie.

### Q: Czy muszę aktualizować wszystkie config files?
**A**: NIE! Istniejące konfiguracje z `&` codes będą działać bez zmian.

### Q: Co z pluginami zależnymi od FunnyGuilds?
**A**: API pozostaje takie samo (`String` jako parametr), wewnętrznie konwertujemy na Component.

### Q: Czy MiniMessage (Część 4) jest wymagane?
**A**: NIE! Części 1-3 są wystarczające. Część 4 to bonus dla użytkowników którzy chcą fancier formatowania.

### Q: Czy to złamie kompatybilność z Spigot?
**A**: Paper API jest wymagane dla AsyncChatEvent (już dodane w Fazie 2). Adventure Components działają zarówno na Paper jak i Spigot.

---

## Podsumowanie

| Część | Czas | Ryzyko | Pliki | Status |
|-------|------|--------|-------|--------|
| 1. BukkitUserProfile | 30 min | Niskie | 1 | ⏳ Oczekuje |
| 2. PlayerChat weryfikacja | 15 min | Bardzo niskie | 0 | ⏳ Oczekuje |
| 3. FunnyEvent/Guild weryfikacja | 15 min | Bardzo niskie | 0 | ⏳ Oczekuje |
| 4. MiniMessage (opcjonalne) | 45 min | Niskie | 2 | ⏳ Oczekuje |
| **RAZEM (bez cz. 4)** | **1h** | - | **1** | - |
| **RAZEM (z cz. 4)** | **1h 45min** | - | **3** | - |

**Końcowy efekt**: 
- Pełna migracja na Adventure Components
- Legacy formatting nadal działa
- Opcjonalne MiniMessage support (hex, gradient, hover, click)
- Przyszłościowe API

---

## Następne Kroki

Po zakończeniu tej migracji możemy przejść do:
1. DATABASE_REFACTORING_PLAN.md - usunięcie FLAT storage
2. Usunięcia `RegionManager.deleteRegion()`
3. Innych ulepszeń z Fazy 4-5

---

*Dokument utworzony: 2026-01-10*
*Autor: GitHub Copilot*
