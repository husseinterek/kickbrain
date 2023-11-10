package com.kickbrain.beans;

import com.kickbrain.beans.configuration.QuestionVO;

public class GameDetailsVO {

	private int gameId;
	private QuestionVO question;
	private int player1Score;
	private int player2Score;
	private String winnerId;

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public QuestionVO getQuestion() {
		return question;
	}

	public void setQuestion(QuestionVO question) {
		this.question = question;
	}

	public int getPlayer1Score() {
		return player1Score;
	}

	public void setPlayer1Score(int player1Score) {
		this.player1Score = player1Score;
	}

	public int getPlayer2Score() {
		return player2Score;
	}

	public void setPlayer2Score(int player2Score) {
		this.player2Score = player2Score;
	}

	public String getWinnerId() {
		return winnerId;
	}

	public void setWinnerId(String winnerId) {
		this.winnerId = winnerId;
	}

}
