package io.github.haykam821.diceyheights;

import io.github.haykam821.diceyheights.game.DiceyHeightsConfig;
import io.github.haykam821.diceyheights.game.phase.DiceyHeightsWaitingPhase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.api.game.GameType;

public class DiceyHeights implements ModInitializer {
	private static final String MOD_ID = "diceyheights";

	private static final Identifier DICEY_HEIGHTS_ID = DiceyHeights.identifier("dicey_heights");
	public static final GameType<DiceyHeightsConfig> DICEY_HEIGHTS_TYPE = GameType.register(DICEY_HEIGHTS_ID, DiceyHeightsConfig.CODEC, DiceyHeightsWaitingPhase::open);

	@Override
	public void onInitialize() {
		return;
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
