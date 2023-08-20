package com.kickbrain.beans;

public class StrikeResult {

	private int nbStrikes;
	private String submittedPlayer;
	private String currentTurn;
	private String answerElementId;

	public int getNbStrikes() {
		return nbStrikes;
	}

	public void setNbStrikes(int nbStrikes) {
		this.nbStrikes = nbStrikes;
	}

	public String getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(String currentTurn) {
		this.currentTurn = currentTurn;
	}

	public String getSubmittedPlayer() {
		return submittedPlayer;
	}

	public void setSubmittedPlayer(String submittedPlayer) {
		this.submittedPlayer = submittedPlayer;
	}

	public String getAnswerElementId() {
		return answerElementId;
	}

	public void setAnswerElementId(String answerElementId) {
		this.answerElementId = answerElementId;
	}

}
