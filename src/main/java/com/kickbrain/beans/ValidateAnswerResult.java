package com.kickbrain.beans;

public class ValidateAnswerResult extends BaseResult {

	private String matchingAnswer;
	private boolean isCorrect;
	private String submittedPlayer;
	private String currentTurn;
	private String answerElementId;
	private boolean allAnswersProvided;
	private String winner;
	private boolean isTie;

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public String getMatchingAnswer() {
		return matchingAnswer;
	}

	public void setMatchingAnswer(String matchingAnswer) {
		this.matchingAnswer = matchingAnswer;
	}

	public String getSubmittedPlayer() {
		return submittedPlayer;
	}

	public void setSubmittedPlayer(String submittedPlayer) {
		this.submittedPlayer = submittedPlayer;
	}

	public String getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(String currentTurn) {
		this.currentTurn = currentTurn;
	}

	public String getAnswerElementId() {
		return answerElementId;
	}

	public void setAnswerElementId(String answerElementId) {
		this.answerElementId = answerElementId;
	}

	public boolean isAllAnswersProvided() {
		return allAnswersProvided;
	}

	public void setAllAnswersProvided(boolean allAnswersProvided) {
		this.allAnswersProvided = allAnswersProvided;
	}

	public boolean isTie() {
		return isTie;
	}

	public void setTie(boolean isTie) {
		this.isTie = isTie;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

}
