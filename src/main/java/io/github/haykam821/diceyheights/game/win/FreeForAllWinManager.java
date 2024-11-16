package io.github.haykam821.diceyheights.game.win;

import java.util.List;

import io.github.haykam821.diceyheights.game.phase.DiceyHeightsActivePhase;
import io.github.haykam821.diceyheights.game.player.PlayerEntry;

public class FreeForAllWinManager extends WinManager {
	public FreeForAllWinManager(DiceyHeightsActivePhase phase) {
		super(phase);
	}

	@Override
	public WinResult checkRemainingWin(List<PlayerEntry> alivePlayers) {
		if (alivePlayers.size() == 1) {
			PlayerEntry winner = alivePlayers.get(0);
			return new WinResult(winner.getWinMessage());
		}

		return null;
	}
}
