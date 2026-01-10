# MiniMessage Examples - FunnyGuilds

## Włączone w wersji 5.0+

FunnyGuilds teraz wspiera **MiniMessage** - nowoczesny system formatowania tekstu z pełnym wsparciem dla legacy codes!

## ✅ Wspierane Formaty

### 1. Legacy Formatting (Kompatybilność wsteczna)
```
§aZielony tekst
§cCzerwony tekst
§lPogrubiony §rNormalny
```

**Wszystkie stare kody § nadal działają!**

### 2. Hex Colors
```
&#FF5733Pomarańczowy tekst
<#00FF00>Zielony tekst z tagami</color>
```

**Przykłady**:
- `&#FF0000Czerwony` → Czerwony tekst (hex #FF0000)
- `&#00FF00Zielony` → Zielony tekst (hex #00FF00)
- `&#FFD700Złoty` → Złoty tekst (hex #FFD700)

### 3. Gradient
```
<gradient:red:blue>Tekst z gradientem</gradient>
<gradient:#FF0000:#0000FF>Gradient z hex</gradient>
<gradient:red:yellow:green>Wielokolorowy gradient</gradient>
```

**Przykłady**:
- `<gradient:red:blue>FunnyGuilds</gradient>` → Gradient od czerwonego do niebieskiego
- `<gradient:#FF1744:#00E676>Super gildia!</gradient>` → Custom hex gradient

### 4. Rainbow
```
<rainbow>Tęczowy tekst!</rainbow>
<rainbow:2>Powolny rainbow</rainbow>
<rainbow:!2>Odwrócony rainbow</rainbow>
```

**Przykłady**:
- `<rainbow>FunnyGuilds 5.0</rainbow>` → Animowany rainbow text
- `<rainbow:2>Wolniejszy efekt</rainbow>` → Rainbow z fazą 2

### 5. Hover Events (Podpowiedzi)
```
<hover:show_text:'To jest podpowiedź'>Najedź myszką</hover>
<hover:show_text:'Informacje o gildii'>Gildia XYZ</hover>
```

**Przykłady**:
- `<hover:show_text:'Członków: 15\nPoints: 1000'>Moja Gildia</hover>`
- `<hover:show_text:'Kliknij aby teleportować'>/guild home</hover>`

### 6. Click Events (Kliknięcia)
```
<click:run_command:'/guild info'>Kliknij aby zobaczyć info</click>
<click:suggest_command:'/guild join '>Zaproponuj komendę</click>
<click:open_url:'https://example.com'>Otwórz stronę</click>
<click:copy_to_clipboard:'Skopiowany tekst'>Kopiuj do schowka</click>
```

**Przykłady**:
- `<click:run_command:'/guild home'>Teleportuj się</click>` → Wykonuje komendę
- `<click:suggest_command:'/guild invite '>Zaproś gracza</click>` → Wpisuje w chat
- `<click:open_url:'https://funnyguilds.dzikoysk.net'>Strona pluginu</click>`

### 7. Kombinacje
```
<gradient:red:blue><hover:show_text:'Kliknij!'>
<click:run_command:'/guild info'>FunnyGuilds 5.0</click>
</hover></gradient>
```

**Przykład kompleksowy**:
```
<gradient:#FF1744:#00E676>
  <hover:show_text:'§aGildia założona: 2024\n§bCzłonków: 25\n§ePoints: 15000'>
    <click:run_command:'/guild info MyGuild'>
      §l§nMoja Super Gildia
    </click>
  </hover>
</gradient>
```

## 📝 Zastosowania w Konfiguracjach

### Wiadomości w config.yml
```yaml
messages:
  guild-join: "<gradient:green:blue>Dołączyłeś do gildii <hover:show_text:'Kliknij aby zobaczyć info'><click:run_command:'/guild info'>{GUILD}</click></hover>!</gradient>"
  
  guild-create: "<rainbow>Gratulacje! Utworzono gildię {GUILD}!</rainbow>"
  
  guild-war-start: "&#FF0000§lWOJNA! &#FFFFFFGildia {ATTACKER} atakuje {DEFENDER}!"
  
  rank-up: "<gradient:#FFD700:#FFA500>Awansowałeś na pozycję #{RANK}!</gradient>"
```

### Tablist
```yaml
tablist:
  header: |
    <gradient:red:gold>FunnyGuilds Server</gradient>
    <rainbow>discord.gg/example</rainbow>
    
  player-format: "<hover:show_text:'Gildia: {GUILD}\nPoints: {POINTS}'>{TAG} {PLAYER}</hover>"
```

### Hologramy
```yaml
holograms:
  guild-top:
    - "<gradient:#FFD700:#FFA500>§l§nTOP GILDIE</gradient>"
    - "<hover:show_text:'Kliknij aby zobaczyć szczegóły'><click:run_command:'/guild info {GUILD}'>#{RANK}. {GUILD}</click></hover>"
    - "&#00FF00Points: {POINTS}"
```

## 🔧 Migracja ze Starych Formatów

### Przed (Legacy)
```
§a§lGildia: §r§e{GUILD}
§bPoints: §f{POINTS}
```

### Po (MiniMessage + Legacy)
```
<gradient:green:yellow>§lGildia:</gradient> <hover:show_text:'Założona: 2024'>§e{GUILD}</hover>
&#00FFFFPoints: §f{POINTS}
```

**Oba formaty działają! Nie musisz nic zmieniać, ale możesz używać nowych funkcji.**

## 🎨 Paleta Kolorów (Hex)

Popularne kolory hex do użycia:
- `&#FF1744` - Czerwony Material Design
- `&#E91E63` - Różowy
- `&#9C27B0` - Fioletowy
- `&#3F51B5` - Indygo
- `&#2196F3` - Niebieski
- `&#00BCD4` - Cyjan
- `&#009688` - Morski
- `&#4CAF50` - Zielony
- `&#8BC34A` - Limonkowy
- `&#FFEB3B` - Żółty
- `&#FF9800` - Pomarańczowy
- `&#FF5722` - Głęboki pomarańczowy

## 📚 Więcej Informacji

- [Adventure Docs](https://docs.advntr.dev/)
- [MiniMessage Docs](https://docs.advntr.dev/minimessage/)
- [MiniMessage Web Viewer](https://webui.advntr.dev/) - Testuj formaty online!

## ⚠️ Uwagi

1. **Wydajność**: MiniMessage parsing jest włączony tylko gdy wykryje tagi `<>` - legacy § codes są szybsze
2. **Kompatybilność**: Jeśli MiniMessage parsing się nie powiedzie, automatycznie używa legacy parser
3. **Kolejność**: Legacy codes wewnątrz MiniMessage tags działają (np. `<gradient>§lBold text</gradient>`)
4. **Escape**: Użyj `\<` aby wyświetlić znak `<` dosłownie

## 🚀 Przykładowe Konfiguracje

### Nowoczesny motyw
```yaml
prefix: "<gradient:#667eea:#764ba2>FunnyGuilds</gradient> <gray>»</gray> "

guild-messages:
  join: "{PREFIX}<green>Dołączyłeś do <hover:show_text:'Kliknij aby zobaczyć'><click:run_command:'/g info'><gradient:green:blue>{GUILD}</gradient></click></hover>!"
  
  leave: "{PREFIX}<red>Opuściłeś gildię {GUILD}</red>"
  
  ally-add: "{PREFIX}<gradient:blue:cyan>Gildia {GUILD} została sojusznikiem!</gradient>"
```

### Cyberpunk theme
```yaml
colors:
  primary: "&#00FFFF"
  secondary: "&#FF00FF"
  accent: "&#FFFF00"
  
messages:
  motd: |
    <gradient:{PRIMARY}:{SECONDARY}>
    ╔══════════════════════════════╗
    ║  {SERVER_NAME}  
    ║  <rainbow>CYBERPUNK GUILDS</rainbow>
    ║  <{ACCENT}>discord.gg/example</{ACCENT}>
    ╚══════════════════════════════╝
    </gradient>
```

---

**Ciesz się nowoczesnymi możliwościami formatowania w FunnyGuilds 5.0+!** 🎉
