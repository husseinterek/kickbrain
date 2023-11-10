package com.kickbrain.beans.configuration;

public class AppConfiguration {

	private int answerFullMatchRatio;
	private int answerPartMatchRatio;
	private int delayNextChallenge;
	private GameConfig onlineGameConfig;

	public int getAnswerFullMatchRatio() {
		return answerFullMatchRatio;
	}

	public void setAnswerFullMatchRatio(int answerFullMatchRatio) {
		this.answerFullMatchRatio = answerFullMatchRatio;
	}

	public int getAnswerPartMatchRatio() {
		return answerPartMatchRatio;
	}

	public void setAnswerPartMatchRatio(int answerPartMatchRatio) {
		this.answerPartMatchRatio = answerPartMatchRatio;
	}

	public GameConfig getOnlineGameConfig() {
		return onlineGameConfig;
	}

	public void setOnlineGameConfig(GameConfig onlineGameConfig) {
		this.onlineGameConfig = onlineGameConfig;
	}

	public int getDelayNextChallenge() {
		return delayNextChallenge;
	}

	public void setDelayNextChallenge(int delayNextChallenge) {
		this.delayNextChallenge = delayNextChallenge;
	}

}
