package io.github.haykam821.diceyheights.game.win;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record WinResult(Text message) {
	private static final Text NONE_MESSAGE = Text.translatable("text.diceyheights.no_winners").formatted(Formatting.GOLD);
	protected static final WinResult NONE = new WinResult(NONE_MESSAGE);
}
