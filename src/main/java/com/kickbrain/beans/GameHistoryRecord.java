package com.kickbrain.beans;

import java.util.Date;

public class GameHistoryRecord {

	private long gameId;
	private String opponentUsername;
	private int currentUserScore;
	private int opponentUserScore;
	private boolean isWinner;
	private boolean isTie;
	private Date gameDate;
	private int type;

	public long getGameId() {
		return gameId;
	}

	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

	public String getOpponentUsername() {
		return opponentUsername;
	}

	public void setOpponentUsername(String opponentUsername) {
		this.opponentUsername = opponentUsername;
	}

	public int getCurrentUserScore() {
		return currentUserScore;
	}

	public void setCurrentUserScore(int currentUserScore) {
		this.currentUserScore = currentUserScore;
	}

	public int getOpponentUserScore() {
		return opponentUserScore;
	}

	public void setOpponentUserScore(int opponentUserScore) {
		this.opponentUserScore = opponentUserScore;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}

	public Date getGameDate() {
		return gameDate;
	}

	public void setGameDate(Date gameDate) {
		this.gameDate = gameDate;
	}

	public boolean isTie() {
		return isTie;
	}

	public void setTie(boolean isTie) {
		this.isTie = isTie;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
