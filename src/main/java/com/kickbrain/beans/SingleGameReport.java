package com.kickbrain.beans;

import java.util.List;

public class SingleGameReport {

	private Player player;
	private int totalScore;
	private List<GameReportQuestion> questionsResult;

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public List<GameReportQuestion> getQuestionsResult() {
		return questionsResult;
	}

	public void setQuestionsResult(List<GameReportQuestion> questionsResult) {
		this.questionsResult = questionsResult;
	}

}
