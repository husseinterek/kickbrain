package com.kickbrain.beans;

public class SkipQuestionEvent {

	private int nextQuestionIndex;
	private Integer challengeCategory;
	private boolean isLastQuestion;
	private String currentTurn;

	public int getNextQuestionIndex() {
		return nextQuestionIndex;
	}

	public void setNextQuestionIndex(int nextQuestionIndex) {
		this.nextQuestionIndex = nextQuestionIndex;
	}

	public Integer getChallengeCategory() {
		return challengeCategory;
	}

	public void setChallengeCategory(Integer challengeCategory) {
		this.challengeCategory = challengeCategory;
	}

	public boolean isLastQuestion() {
		return isLastQuestion;
	}

	public void setLastQuestion(boolean isLastQuestion) {
		this.isLastQuestion = isLastQuestion;
	}

	public String getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(String currentTurn) {
		this.currentTurn = currentTurn;
	}

}
