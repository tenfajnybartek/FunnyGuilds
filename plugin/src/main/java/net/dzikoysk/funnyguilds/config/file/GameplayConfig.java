package net.dzikoysk.funnyguilds.config.file;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;
import eu.okaeri.validator.annotation.Min;
import eu.okaeri.validator.annotation.Positive;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.DefaultRegex;
import net.dzikoysk.funnyguilds.config.FunnyPattern;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.config.sections.TntProtectionConfiguration;
import net.dzikoysk.funnyguilds.shared.LegacyUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * Gameplay configuration file (gameplay.yml)
 * Contains guild creation costs, limits, region mechanics, PvP settings, etc.
 */
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("      FunnyGuilds Gameplay      #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("Konfiguracja mechaniki gry - gildii, regionów, PvP")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GameplayConfig extends OkaeriConfig {

    // ===== Guild Creation =====

    @Comment("")
    @Comment("Czy tworzenie regionów gildii, oraz inne związane z nimi rzeczy, mają byc włączone")
    @Comment("UWAGA - dobrze przemyśl decyzję o wyłączeniu regionów!")
    @Comment("Gildie nie będą miały w sobie żadnych informacji o regionach, a jeśli regiony są włączone - te informacje muszą byc obecne")
    @Comment("Jeśli regiony miałyby być znowu włączone - będzie trzeba wykasować WSZYSTKIE dane pluginu")
    @Comment("Wyłączenie tej opcji nie powinno spowodować żadnych błędów, jeśli już są utworzone regiony gildii")
    public boolean regionsEnabled = true;

    @Min(1)
    @Comment("")
    @Comment("Maksymalna długość nazwy gildii")
    @CustomKey("name-length")
    public int createNameLength = 22;

    @Min(1)
    @Comment("")
    @Comment("Minimalna długość nazwy gildii")
    @CustomKey("name-min-length")
    public int createNameMinLength = 4;

    @Min(1)
    @Comment("")
    @Comment("Maksymalna długość tagu gildii")
    @CustomKey("tag-length")
    public int createTagLength = 4;

    @Min(1)
    @Comment("")
    @Comment("Minimalna długość tagu gildii")
    @CustomKey("tag-min-length")
    public int createTagMinLength = 2;

    @Comment("")
    @Comment("Zasada sprawdzania nazwy gildii przy jej tworzeniu")
    @Comment("Dostepne zasady:")
    @Comment("LOWERCASE - umożliwia użycie tylko małych liter")
    @Comment("UPPERCASE - umożliwia użycie tylko wielkich liter")
    @Comment("DIGITS - umożliwia użycie tylko cyfr")
    @Comment("LOWERCASE_DIGITS - umożliwia użycie małych liter i cyfr")
    @Comment("UPPERCASE_DIGITS - umożliwia użycie wielkich liter i cyfr")
    @Comment("LETTERS - umożliwia użycie małych i wielkich liter")
    @Comment("LETTERS_DIGITS - umożliwia użycie małych i wielkich liter oraz cyfr")
    @Comment("LETTERS_DIGITS_UNDERSCORE - umożliwia użycie małych i wielkich liter, cyfr oraz podkreślnika")
    @Comment(" ")
    @Comment("Dodatkowo można stworzyć własną zasadę regexa - pomocna może okazać sięprzy tym strona https://regex101.com/")
    public FunnyPattern nameRegex = new FunnyPattern(DefaultRegex.LETTERS);

    @Comment("")
    @Comment("Zasada sprawdzania tagu gildii przy jej tworzeniu")
    @Comment("Możliwe zasady są takie same jak w przypadku name-regex")
    public FunnyPattern tagRegex = new FunnyPattern(DefaultRegex.LETTERS);

    @Comment("")
    @Comment("Zasada sprawdzania nicków graczy")
    @Comment("Możliwe zasady są takie same jak w przypadku name-regex")
    public FunnyPattern playerNameRegex = new FunnyPattern(DefaultRegex.LETTERS_DIGITS_UNDERSCORE);

    @Comment("")
    @Comment("Minimalna długość nicku gracza")
    public int playerNameMinLength = 3;

    @Comment("")
    @Comment("Maksymalna długość nicku gracza")
    public int playerNameMaxLength = 16;

    @Min(0)
    @Comment("")
    @Comment("Minimalna liczba graczy w gildii, aby zaliczała się ona do rankingu")
    @CustomKey("guild-min-members")
    public int minMembersToInclude = 1;

    @Comment("")
    @Comment("Czy wiadomości o braku potrzebnych przedmiotów maja zawierać elementy, na które można najechać")
    @Comment("Takie elementy pokazują informacje o przedmiocie, np. jego typ, nazwę czy opis")
    public boolean enableItemComponent = false;

    @Comment("")
    @Comment("Przedmioty wymagane do założenia gildii")
    @Comment("Tylko wartości ujęte w <> są wymagane - reszta, ujeta w [], jest opcjonalna")
    @Comment("Wzór: <ilosc> <przedmiot>:[metadata] [name:lore:enchants:eggtype:skullowner:armorcolor:flags]")
    @CustomKey("items")
    public List<ItemStack> createItems = ItemUtils.parseItems("5 stone", "5 dirt", "5 tnt");

    @Min(0)
    @Comment("")
    @Comment("Ilość doświadczenia wymagana do założenia gildii")
    public int requiredExperience = 0;

    @Min(0)
    @Comment("")
    @Comment("Ilość pieniędzy wymagana do założenia gildii")
    @Comment("UWAGA: Aby ta opcja mogła działać - na serwerze musi być plugin Vault oraz plugin dodający ekonomię")
    public double requiredMoney = 0;

    @Comment("")
    @Comment("Przedmioty wymagane do założenia gildii, dla osoby z uprawnieniem funnyguilds.vip.items")
    @CustomKey("items-vip")
    public List<ItemStack> createItemsVip = ItemUtils.parseItems("1 gold_ingot");

    @Min(0)
    @Comment("")
    @Comment("Ilość doświadczenia wymagana do założenia gildii, dla osoby z uprawnieniem funnyguilds.vip.items")
    public int requiredExperienceVip = 0;

    @Min(0)
    @Comment("")
    @Comment("Ilość pieniędzy wymagana do założenia gildii, dla osoby z uprawnieniem funnyguilds.vip.items")
    public double requiredMoneyVip = 0;

    @Comment("")
    @Comment("Czy opcja wymaganego rankingu do założenia gildii ma byc włączona")
    public boolean rankCreateEnable = true;

    @Comment("")
    @Comment("Minimalny ranking wymagany do założenia gildii")
    public int rankCreate = 1000;

    @Comment("")
    @Comment("Minimalny ranking wymagany do założenia gildii, dla osoby z uprawnieniem funnyguilds.vip.rank")
    public int rankCreateVip = 800;

    @Comment("")
    @Comment("Minimalna odległość od spawnu")
    public int createDistance = 100;

    @Comment("")
    @Comment("Minimalna odległość regionu gildii od granicy mapy")
    @CustomKey("create-guild-min-distance")
    public double createMinDistanceFromBorder = 50;

    @Comment("")
    @Comment("Lista nazw światów, na których możliwość utworzenia gildii ma być zablokowana")
    public List<String> blockedWorlds = Collections.singletonList("some_world");

    // ===== GUI Settings for Guild Creation =====

    @Comment("")
    @Comment("Czy GUI z przedmiotami na gildię ma być wspólne dla wszystkich")
    @Comment("Jeśli włączone - wszyscy gracze będą widzieli GUI stworzone w sekcji gui-items, a GUI z sekcji gui-items-vip będzie ignorowane")
    public boolean useCommonGUI = false;

    @Comment("")
    @Comment("GUI z przedmiotami na gildię, dla osób bez uprawnienia funnyguilds.vip.items")
    @CustomKey("gui-items")
    public List<String> guiItems_ = Arrays.asList("1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
            "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{ITEM-1}", "{ITEM-2}", "{ITEM-3}", "{GUI-1}",
            "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @Exclude
    public List<ItemStack> guiItems;

    @Comment("")
    @Comment("Nazwa GUI z przedmiotami na gildię, dla osób bez uprawnienia funnyguilds.vip.items")
    public RawString guiItemsTitle = new RawString("&5&lPrzedmioty na gildie");

    @Comment("")
    @Comment("GUI z przedmiotami na gildię, dla osób z uprawnieniem funnyguilds.vip.items")
    @CustomKey("gui-items-vip")
    public List<String> guiItemsVip_ = Arrays.asList("1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
            "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{GUI-1}", "{VIPITEM-1}", "{GUI-3}", "{GUI-1}",
            "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @Exclude
    public List<ItemStack> guiItemsVip;

    @Comment("")
    @Comment("Nazwa GUI z przedmiotami na gildię, dla osób z uprawnieniem funnyguilds.vip.items")
    public RawString guiItemsVipTitle = new RawString("&5&lPrzedmioty na gildie (VIP)");

    @Comment("")
    @Comment("Zmiana nazwy i koloru przedmiotów na gildię")
    public RawString guiItemsName = new RawString("&7>> &a{ITEM-NO-AMOUNT} &7<<");

    @Comment("")
    @Comment("Czy do przedmiotów na gildię, które są w GUI, mają być dodawane dodatkowe linie opisu")
    public boolean addLoreLines = true;

    @Comment("")
    @Comment("Dodatkowe linie opisu, dodawane do każdego przedmiotu")
    public List<RawString> guiItemsLore = RawString.listOf("", "&aPosiadasz juz:", "&a{PINV-AMOUNT} przy sobie &7({PINV-PERCENT}%)",
            "&a{EC-AMOUNT} w enderchescie &7({EC-PERCENT}%)", "&a{ALL-AMOUNT} calkowicie &7({ALL-PERCENT}%)");

    // ===== Guild Heart Settings =====

    @Comment("")
    @Comment("Konfiguracja serca gildii")
    @CustomKey("heart-configuration")
    public HeartConfiguration heart = new HeartConfiguration();

    // ===== Region Settings =====

    @Comment("")
    @Comment("Bloki, które można stawiać na terenie gildii, niezależnie od tego, czy jest się jej członkiem")
    @CustomKey("placing-blocks-bypass-on-region")
    public Set<String> placingBlocksBypassOnRegion_ = Collections.emptySet();

    @Exclude
    public Set<Material> placingBlocksBypassOnRegion;

    @Comment("")
    @Comment("Zablokuj rozlewanie się wody i lawy poza terenem gildii")
    @CustomKey("water-and-lava-flow-only-for-regions")
    public boolean blockFlow = false;

    @Comment("")
    @Comment("Czy gracz po śmierci ma się pojawiać w bazie swojej gildii")
    public boolean respawnInBase = true;

    @Min(1)
    @Comment("")
    @Comment("Wielkość regionu gildii")
    public int regionSize = 50;

    @Min(0)
    @Comment("")
    @Comment("Minimalna odległość między terenami gildii")
    public int regionMinDistance = 10;

    @Min(1)
    @Comment("")
    @Comment("Co ile może byc wywoływany pasek powiadomień przez jednego gracza, w sekundach")
    public int regionNotificationCooldown = 60;

    @Comment("")
    @Comment("Komendy zablokowane na terenie gildii, dla graczy niebędących członkami")
    @CustomKey("region-commands")
    public List<String> regionCommands = Collections.singletonList("sethome");

    @Comment("")
    @Comment("Typy bloków, z którymi osoba spoza gildii NIE może prowadzić interakcji na terenie innej gildii")
    public List<Material> blockedInteract = Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST);

    @Comment("")
    public BlockTeleportOnRegion blockTeleportOnRegion = new BlockTeleportOnRegion();

    public static class BlockTeleportOnRegion extends OkaeriConfig {

        @Comment("Czy ma być blokowana teleportacja na teren neutralnej gildii")
        public boolean neutral = true;

        @Comment("")
        @Comment("Czy ma być blokowana teleportacja na teren wrogiej gildii")
        public boolean enemy = true;

        @Comment("")
        @Comment("Czy ma być blokowana teleportacja na teren sojuszniczej gildii")
        public boolean ally = false;

    }

    @Exclude
    public boolean eventTeleport = false;

    // ===== Guild Limits =====

    @Min(1)
    @Comment("")
    @Comment("Maksymalna liczba członków w gildii")
    @CustomKey("max-members")
    public int maxMembersInGuild = 15;

    @Min(0)
    @Comment("")
    @Comment("Maksymalna liczba sojuszy między gildiami")
    @CustomKey("max-allies")
    public int maxAlliesBetweenGuilds = 15;

    @Min(0)
    @Comment("")
    @Comment("Maksymalna liczba wojen między gildiami")
    @CustomKey("max-enemies")
    public int maxEnemiesBetweenGuilds = 15;

    // ===== Teleportation Settings =====

    @Comment("")
    @Comment("Możliwość ucieczki z terenu innej gildii")
    public boolean escapeEnable = true;

    @PositiveOrZero
    @Comment("")
    @Comment("Czas jaki musi upłynąć od włączenia ucieczki do teleportacji")
    public Duration escapeDelay = Duration.ofMinutes(2);

    @Comment("")
    @Comment("Możliwość ucieczki na spawn dla graczy bez gildii")
    public boolean escapeSpawn = true;

    @Comment("")
    @Comment("Możliwość teleportacji do gildii")
    public boolean baseEnable = true;

    @PositiveOrZero
    @Comment("")
    @Comment("Czas oczekiwania na teleportację, w sekundach")
    public Duration baseDelay = Duration.ofSeconds(5);

    @PositiveOrZero
    @Comment("")
    @Comment("Czas oczekiwania na teleportację, w sekundach, dla graczy posiadających uprawnienie funnyguilds.vip.baseTeleportTime")
    public Duration baseDelayVip = Duration.ofSeconds(3);

    @Comment("")
    @Comment("Koszt teleportacji do gildii")
    public List<ItemStack> baseItems = ItemUtils.parseItems("1 diamond", "1 emerald");

    @Comment("")
    @Comment("Koszt dołączenia do gildii")
    public List<ItemStack> joinItems = ItemUtils.parseItems("1 diamond");

    // ===== Enlargement Settings =====

    @Comment("")
    @Comment("O ile powiększany jest teren gildii przy zwiększeniu poziomu")
    public int enlargeSize = 5;

    @Comment("")
    @Comment("Koszt powiększania gildii")
    public List<ItemStack> enlargeItems = ItemUtils.parseItems("8 diamond", "16 diamond", "24 diamond", "32 diamond", "40 diamond", "48 diamond", "56 diamond", "64 diamond", "72 diamond", "80 diamond");

    // ===== Validity Settings =====

    @Positive
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("")
    @Comment("Jaką ważność ma gildia po jej założeniu")
    @CustomKey("validity-start")
    public Duration validityStart = Duration.ofDays(14);

    @Positive
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("")
    @Comment("Ile czasu dodaje przedłużenie ważności gildii")
    @CustomKey("validity-time")
    public Duration validityTime = Duration.ofDays(14);

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("")
    @Comment("Ile dni przed końcem wygasania można przedłużyć gildię")
    @CustomKey("validity-when")
    public Duration validityWhen = Duration.ofDays(14);

    @Comment("")
    @Comment("Koszt przedłużenia gildii")
    public List<ItemStack> validityItems = ItemUtils.parseItems("10 diamond");

    // ===== War Settings =====

    @Comment("")
    @Comment("Możliwość podbijania gildii")
    public boolean warEnabled = true;

    @Min(1)
    @Comment("")
    @Comment("Ile żyć ma gildia")
    public int warLives = 3;

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("")
    @Comment("Po jakim czasie od założenia można zaatakować gildię")
    @CustomKey("war-protection")
    public Duration warProtection = Duration.ofHours(24);

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("")
    @Comment("Ile czasu trzeba czekać do następnego ataku na gildię")
    @CustomKey("war-wait")
    public Duration warWait = Duration.ofHours(24);

    @Comment("")
    @Comment("Czy gildia podczas okresu ochronnego ma posiadać ochronę przeciw TNT")
    public boolean warTntProtection = true;

    @Comment("")
    @Comment("Czy zwierzęta na terenie gildii mają być chronione przed osobami spoza gildii")
    public boolean animalsProtection = false;

    @Comment("")
    @Comment("Czy proces usunięcia gildii powinien zostać przerwany, jezeli ktoś spoza gildii jest na jej terenie")
    public boolean guildDeleteCancelIfSomeoneIsOnRegion = false;

    // ===== TNT Protection =====

    @Comment("")
    public TntProtectionConfiguration tntProtection = new TntProtectionConfiguration();

    // ===== Explosion Settings =====

    @PositiveOrZero
    @Comment("")
    @Comment("Czas przez jaki nie można budować na terenie gildii po wybuchu")
    public Duration regionExplode = Duration.ofMinutes(2);

    @Comment("")
    @Comment("Czy blokada budowania przy wybuchu powinna działać jeśli gildia jest chroniona")
    public boolean regionExplodeBlockProtected = false;

    @Comment("Czy blokada budowania przy wybuchu powinna działać jeśli TNT jest wyłączone")
    public boolean regionExplodeBlockTntDisabled = false;

    @Comment("")
    @Comment("Lista entity, których wybuch nie powoduje blokady budowania na terenie gildii")
    public Set<EntityType> regionExplodeExcludeEntities = EntityUtils.parseEntityTypes(true, "CREEPER", "WITHER", "WITHER_SKULL", "FIREBALL");

    @Comment("")
    @Comment("Czy blokada po wybuchu ma obejmować rownież niszczenie bloków")
    public boolean regionExplodeBlockBreaking = false;

    @Comment("")
    @Comment("Czy blokada po wybuchu ma obejmować rownież interakcje z blocked-interact")
    public boolean regionExplodeBlockInteractions = false;

    @Min(0)
    @Comment("")
    @Comment("Zasięg pobieranych przedmiotów po wybuchu, jeżeli chcesz wyłączyć - wpisz 0")
    public int explodeRadius = 3;

    @Comment("")
    @Comment("Jakie materiały, i z jaka szansą, maja byc niszczone po wybuchu")
    @CustomKey("explode-materials")
    public Map<String, Double> explodeMaterials_ = ImmutableMap.of(
            "ender_chest", 20.0,
            "enchantment_table", 20.0,
            "obsidian", 20.0,
            "water", 33.0,
            "lava", 33.0
    );

    @Exclude
    public Map<Material, Double> explodeMaterials;
    @Exclude
    public boolean allMaterialsAreExplosive;
    @Exclude
    public double defaultExplodeChance = -1.0;

    @Comment("")
    @Comment("Czy powstałe wybuchy powinny niszczyć bloki wyłącznie na terenach gildii")
    public boolean explodeShouldAffectOnlyGuild = false;

    // ===== Bugged Blocks =====

    @Comment("")
    @Comment("Czy funkcja efektu 'zbugowanych' klocków ma byc włączona")
    public boolean buggedBlocks = false;

    @Min(0)
    @Comment("")
    @Comment("Czas po którym 'zbugowane' klocki maja zostać usunięte")
    public long buggedBlocksTimer = 20L;

    @Comment("")
    @Comment("Bloki, których nie można 'bugować'")
    public Set<Material> buggedBlocksExclude = defaultBuggedBlocksExclude();

    @Comment("")
    @Comment("Czy klocki po 'zbugowaniu' mają zostać oddane")
    public boolean buggedBlocksReturn = false;

    // ===== PvP Settings =====

    @Comment("")
    @Comment("Czy członkowie gildii mogą sobie zadawać obrażenia (domyślnie)")
    @CustomKey("damage-guild")
    public boolean damageGuild = false;

    @Comment("")
    @Comment("Czy sojuszniczy mogą sobie zadawać obrażenia")
    @CustomKey("damage-ally")
    public boolean damageAlly = false;

    // ===== Misc Settings =====

    @Comment("")
    @Comment("Czy sprawdzanie zakazanych nazw i tagów gildii powinno być włączone")
    @CustomKey("check-for-restricted-guild-names")
    public boolean checkForRestrictedGuildNames = false;

    @Comment("")
    @Comment("Jeśli ustawione na true - jedynie nazwy i tagi z list będą dozwolone")
    @CustomKey("whitelist")
    public boolean whitelist = false;

    @Comment("")
    @Comment("Wyrażenia zakazane/dozwolone do użycia jako nazwa gildii")
    @CustomKey("restricted-guild-names")
    public List<String> restrictedGuildNames = Collections.singletonList("Administracja");

    @Comment("")
    @Comment("Wyrażenia zakazane/dozwolone do użycia jako tag gildii")
    @CustomKey("restricted-guild-tags")
    public List<String> restrictedGuildTags = Collections.singletonList("TEST");

    @Comment("")
    @Comment("Czy tag gildii podany przy tworzeniu gildii powinien zachować formę taką, w jakiej został wpisany")
    @CustomKey("guild-tag-keep-case")
    public boolean guildTagKeepCase = true;

    @Comment("")
    @Comment("Czy tagi gildii powinny byc pokazywane wielkimi literami")
    @CustomKey("guild-tag-uppercase")
    public boolean guildTagUppercase = false;

    @Comment("")
    @Comment("Czy osoba, która założyła pierwszą gildię na serwerze powinna dostać nagrodę")
    @CustomKey("should-give-rewards-for-first-guild")
    public boolean giveRewardsForFirstGuild = false;

    @Comment("")
    @Comment("Przedmioty, które zostaną przyznane graczowi, który pierwszy założył gildię na serwerze")
    @CustomKey("rewards-for-first-guild")
    public List<ItemStack> firstGuildRewards = ItemUtils.parseItems("1 diamond name:&bNagroda_za_pierwsza_gildie_na_serwerze");

    @Comment("")
    @Comment("Jaki argument powinien zostać podany przez gracza, gdy chce zaprosić wszystkich graczy w danym promieniu")
    public String inviteCommandAllArgument = "*";

    @Comment("")
    @Comment("Czy wielkość liter powinna być ignorowana dla argumentu od zapraszania wszystkich graczy w danym promieniu")
    public boolean inviteCommandAllArgumentIgnoreCase = true;

    @Comment("")
    @Comment("Maksymalna odległość, w jakiej zapraszani są gracze w momencie użycia komendy /invite *")
    public double inviteCommandAllMaxRange = 50.0;

    @Comment("")
    @Comment("Domyślna odległość, w jakiej zapraszani są gracze w momencie użycia komendy /invite *")
    public double inviteCommandAllDefaultRange = 10.0;

    @Comment("")
    @Comment("Czy event PlayMoveEvent ma byc aktywny (odpowiada za wyświetlanie powiadomień o wejściu na teren gildii)")
    @CustomKey("event-move")
    public boolean eventMove = true;

    @Exclude
    public boolean eventPhysics;

    // ===== Helper Methods =====

    private List<ItemStack> loadGUI(List<String> contents) {
        List<ItemStack> items = new ArrayList<>();

        for (String guiEntry : contents) {
            ItemStack item = null;

            if (guiEntry.contains("GUI-")) {
                int index = LegacyUtils.getIndex(guiEntry);
                if (index > 0 && index <= items.size()) {
                    item = items.get(index - 1);
                }
            }
            else if (guiEntry.contains("VIPITEM-")) {
                try {
                    int index = LegacyUtils.getIndex(guiEntry);
                    if (index > 0 && index <= this.createItemsVip.size()) {
                        item = this.createItemsVip.get(index - 1);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getPluginLogger().parser("Index given in " + guiEntry + " is > " + this.createItemsVip.size() + " or <= 0");
                }
            }
            else if (guiEntry.contains("ITEM-")) {
                try {
                    int index = LegacyUtils.getIndex(guiEntry);
                    if (index > 0 && index <= this.createItems.size()) {
                        item = this.createItems.get(index - 1);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getPluginLogger().parser("Index given in " + guiEntry + " is > " + this.createItems.size() + " or <= 0");
                }
            }
            else {
                item = ItemUtils.parseItem(guiEntry);
            }

            if (item == null) {
                item = new ItemBuilder(Material.RED_STAINED_GLASS_PANE, 1, 14)
                        .setName("&c&lERROR IN GUI CREATION: " + guiEntry, true).getItem();
            }

            items.add(item);
        }

        return items;
    }

    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();
        this.loadProcessedProperties();
        return this;
    }

    public void loadProcessedProperties() {
        if (placingBlocksBypassOnRegion_.contains("*")) {
            placingBlocksBypassOnRegion = new HashSet<>(Arrays.asList(Material.values()));
        } else {
            placingBlocksBypassOnRegion = placingBlocksBypassOnRegion_.stream()
                    .map(Material::matchMaterial)
                    .collect(Collectors.toSet());
        }

        this.guiItems = this.loadGUI(this.guiItems_);

        if (!this.useCommonGUI) {
            this.guiItemsVip = this.loadGUI(this.guiItemsVip_);
        }

        if (this.heart.createMaterial != null && this.heart.createMaterial.hasGravity()) {
            this.eventPhysics = true;
        }

        if (this.blockTeleportOnRegion.neutral || this.blockTeleportOnRegion.enemy || this.blockTeleportOnRegion.ally) {
            this.eventTeleport = true;
        }

        this.explodeMaterials = new EnumMap<>(Material.class);
        for (Map.Entry<String, Double> entry : this.explodeMaterials_.entrySet()) {
            double chance = entry.getValue();
            if (chance < 0) {
                continue;
            }

            if (entry.getKey().equalsIgnoreCase("*")) {
                this.allMaterialsAreExplosive = true;
                this.defaultExplodeChance = chance;
                continue;
            }

            Material material = Material.matchMaterial(entry.getKey());
            if (material == null || material == Material.AIR) {
                continue;
            }

            this.explodeMaterials.put(material, chance);
        }

        this.tntProtection.time.passingMidnight = this.tntProtection.time.startTime.getTime().isAfter(this.tntProtection.time.endTime.getTime());
        this.heart.loadProcessedProperties();
    }

    public Set<Material> defaultBuggedBlocksExclude() {
        Set<Material> excludedBlocks = new HashSet<>(Set.of(
            Material.TNT, Material.LAVA, Material.WATER,
            Material.RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL, Material.POWERED_RAIL,
            Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_BLOCK, Material.REDSTONE_TORCH,
            Material.REDSTONE_WALL_TORCH, Material.REPEATER, Material.COMPARATOR, Material.DAYLIGHT_DETECTOR,
            Material.DISPENSER, Material.HOPPER, Material.DROPPER, Material.OBSERVER, Material.LEVER,
            Material.TRIPWIRE_HOOK, Material.REDSTONE_LAMP, Material.TRAPPED_CHEST, Material.CHEST
        ));

        Set<Material> filteredMaterials = Arrays.stream(Material.values()).filter(material -> {
            if (material.name().startsWith("LEGACY_")) {
                return false;
            }
            if (material.hasGravity()) {
                return true;
            }
            if (material.name().endsWith("_PRESSURE_PLATE")) {
                return true;
            }
            if (material.name().endsWith("_TRAPDOOR")) {
                return true;
            }
            if (material.name().endsWith("_BUTTON")) {
                return true;
            }
            if (material.name().endsWith("_DOOR")) {
                return true;
            }
            if (material.name().endsWith("_FENCE_GATE")) {
                return true;
            }
            return false;
        }).collect(Collectors.toSet());

        excludedBlocks.addAll(filteredMaterials);
        return excludedBlocks;
    }

}
