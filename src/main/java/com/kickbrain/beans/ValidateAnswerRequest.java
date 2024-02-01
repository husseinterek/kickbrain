package com.kickbrain.beans;

public class ValidateAnswerRequest {

	private int currentQuestionIdx;
	private String roomId;
	private String questionId;
	private String submittedPlayerId;
	private String opponentPlayerId;
	private String capturedAnswer;
	private String answerElementId;
	private Integer challengeCategory;
	private String language;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getCapturedAnswer() {
		return capturedAnswer;
	}

	public void setCapturedAnswer(String capturedAnswer) {
		this.capturedAnswer = capturedAnswer;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getSubmittedPlayerId() {
		return submittedPlayerId;
	}

	public void setSubmittedPlayerId(String submittedPlayerId) {
		this.submittedPlayerId = submittedPlayerId;
	}

	public String getOpponentPlayerId() {
		return opponentPlayerId;
	}

	public void setOpponentPlayerId(String opponentPlayerId) {
		this.opponentPlayerId = opponentPlayerId;
	}

	public int getCurrentQuestionIdx() {
		return currentQuestionIdx;
	}

	public void setCurrentQuestionIdx(int currentQuestionIdx) {
		this.currentQuestionIdx = currentQuestionIdx;
	}

	public String getAnswerElementId() {
		return answerElementId;
	}

	public void setAnswerElementId(String answerElementId) {
		this.answerElementId = answerElementId;
	}

	public Integer getChallengeCategory() {
		return challengeCategory;
	}

	public void setChallengeCategory(Integer challengeCategory) {
		this.challengeCategory = challengeCategory;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
