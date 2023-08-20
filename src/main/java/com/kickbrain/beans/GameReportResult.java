package com.kickbrain.beans;

import java.util.Map;

public class GameReportResult {

	private int player1Score;
	private int player2Score;
	private Map<String, String> questionsResult;

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

	public Map<String, String> getQuestionsResult() {
		return questionsResult;
	}

	public void setQuestionsResult(Map<String, String> questionsResult) {
		this.questionsResult = questionsResult;
	}

}
