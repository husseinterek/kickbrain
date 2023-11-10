package com.kickbrain.beans;

import java.util.List;

public class GamesHistoryResult extends BaseResult {

	private List<GameHistoryRecord> games;

	public List<GameHistoryRecord> getGames() {
		return games;
	}

	public void setGames(List<GameHistoryRecord> games) {
		this.games = games;
	}

}
