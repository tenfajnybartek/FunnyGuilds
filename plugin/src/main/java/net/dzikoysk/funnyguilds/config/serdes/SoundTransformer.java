package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class SoundTransformer extends BidirectionalTransformer<String, Sound> {

    @Override
    public GenericsPair<String, Sound> getPair() {
        return this.genericsPair(String.class, Sound.class);
    }

    @Override
    public Sound leftToRight(@NotNull String data, @NotNull SerdesContext serdesContext) {
        try {
            return Sound.valueOf(data.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Return a safe default if the sound name is invalid
            return Sound.ENTITY_PLAYER_LEVELUP;
        }
    }

    @Override
    public String rightToLeft(Sound data, @NotNull SerdesContext serdesContext) {
        return data.name();
    }

}
