package io.github.haykam821.diceyheights.game.win;

import java.util.List;

import io.github.haykam821.diceyheights.game.phase.DiceyHeightsActivePhase;
import io.github.haykam821.diceyheights.game.player.PlayerEntry;
import io.github.haykam821.diceyheights.game.player.TeamEntry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class TeamWinManager extends WinManager {
	public TeamWinManager(DiceyHeightsActivePhase phase) {
		super(phase);
	}

	@Override
	public WinResult checkRemainingWin(List<PlayerEntry> alivePlayers) {
		Object2IntOpenHashMap<TeamEntry> alivePlayerCounts = new Object2IntOpenHashMap<>();

		for (PlayerEntry player : alivePlayers) {
			TeamEntry team = player.getTeam();

			if (team != null) {
				alivePlayerCounts.addTo(team, 1);
			}
		}

		TeamEntry winner = getSingleLivingTeam(alivePlayerCounts);

		if (winner != null) {
			return new WinResult(winner.getWinMessage());
		}

		return null;
	}

	private TeamEntry getSingleLivingTeam(Object2IntMap<TeamEntry> alivePlayerCounts) {
		TeamEntry winner = null;

		for (Object2IntMap.Entry<TeamEntry> entry : alivePlayerCounts.object2IntEntrySet()) {
			if (entry.getIntValue() > 0) {
				// Multiple teams with living players, so there is no winner
				if (winner != null) {
					return null;
				}

				winner = entry.getKey();
			}
		}

		return winner;
	}
}
