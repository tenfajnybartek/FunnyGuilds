package net.dzikoysk.funnyguilds.feature.eventlog;

/**
 * Types of events logged in guild event log.
 */
public enum EventLogType {
    // Member events
    MEMBER_JOIN("Dołączenie"),
    MEMBER_LEAVE("Opuszczenie"),
    MEMBER_KICK("Wyrzucenie"),
    MEMBER_INVITE("Zaproszenie"),
    
    // Permission/role events
    DEPUTY_SET("Mianowanie zastępcy"),
    DEPUTY_REMOVE("Usunięcie zastępcy"),
    LEADER_CHANGE("Zmiana lidera"),
    PERMISSION_CHANGE("Zmiana uprawnień"),
    
    // Vault events
    VAULT_DEPOSIT_MONEY("Wpłata pieniędzy"),
    VAULT_WITHDRAW_MONEY("Wypłata pieniędzy"),
    VAULT_DEPOSIT_ITEM("Wpłata przedmiotów"),
    VAULT_WITHDRAW_ITEM("Wypłata przedmiotów"),
    
    // Diplomacy events
    ALLY_REQUEST_SENT("Wysłanie propozycji sojuszu"),
    ALLY_REQUEST_ACCEPTED("Przyjęcie sojuszu"),
    ALLY_REQUEST_REJECTED("Odrzucenie sojuszu"),
    ALLY_BROKEN("Rozwiązanie sojuszu"),
    WAR_DECLARED("Wypowiedzenie wojny"),
    WAR_ENDED("Zakończenie wojny"),
    WAR_WON("Wygrana wojna"),
    WAR_LOST("Przegrana wojna"),
    
    // Guild management events
    GUILD_CREATED("Założenie gildii"),
    GUILD_EXTENDED("Przedłużenie ważności"),
    GUILD_ENLARGED("Powiększenie terenu"),
    PVP_TOGGLED("Zmiana PvP gildii"),
    ALLY_PVP_TOGGLED("Zmiana PvP sojuszu"),
    BASE_MOVED("Przeniesienie bazy");

    private final String displayName;

    EventLogType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
