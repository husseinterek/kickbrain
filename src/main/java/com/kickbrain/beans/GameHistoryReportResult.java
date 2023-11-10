package com.kickbrain.beans;

import java.util.List;

public class GameHistoryReportResult extends BaseResult {

	private int gameId;
	private List<GameDetailsVO> gameDetails;
	private int type;
	private int singleGameTotalScore;
	private String onlineGameWinner;
	private boolean onlineGameIsTie;

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public List<GameDetailsVO> getGameDetails() {
		return gameDetails;
	}

	public void setGameDetails(List<GameDetailsVO> gameDetails) {
		this.gameDetails = gameDetails;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSingleGameTotalScore() {
		return singleGameTotalScore;
	}

	public void setSingleGameTotalScore(int singleGameTotalScore) {
		this.singleGameTotalScore = singleGameTotalScore;
	}

	public String getOnlineGameWinner() {
		return onlineGameWinner;
	}

	public void setOnlineGameWinner(String onlineGameWinner) {
		this.onlineGameWinner = onlineGameWinner;
	}

	public boolean isOnlineGameIsTie() {
		return onlineGameIsTie;
	}

	public void setOnlineGameIsTie(boolean onlineGameIsTie) {
		this.onlineGameIsTie = onlineGameIsTie;
	}

}
