package com.kickbrain.beans.configuration;

public class ChallengeConfig {

	private String titleEn;
	private String titleAr;
	private int category;
	private int nbQuestions;
	private int appearInSingleGame;
	private int bellTimer;
	private int answerTimer;
	private int bidTimer;

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getNbQuestions() {
		return nbQuestions;
	}

	public void setNbQuestions(int nbQuestions) {
		this.nbQuestions = nbQuestions;
	}

	public String getTitleEn() {
		return titleEn;
	}

	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}

	public String getTitleAr() {
		return titleAr;
	}

	public void setTitleAr(String titleAr) {
		this.titleAr = titleAr;
	}

	public int getAppearInSingleGame() {
		return appearInSingleGame;
	}

	public void setAppearInSingleGame(int appearInSingleGame) {
		this.appearInSingleGame = appearInSingleGame;
	}

	public int getBellTimer() {
		return bellTimer;
	}

	public void setBellTimer(int bellTimer) {
		this.bellTimer = bellTimer;
	}

	public int getAnswerTimer() {
		return answerTimer;
	}

	public void setAnswerTimer(int answerTimer) {
		this.answerTimer = answerTimer;
	}

	public int getBidTimer() {
		return bidTimer;
	}

	public void setBidTimer(int bidTimer) {
		this.bidTimer = bidTimer;
	}

}
