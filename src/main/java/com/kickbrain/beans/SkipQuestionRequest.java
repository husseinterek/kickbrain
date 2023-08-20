package com.kickbrain.beans;

public class SkipQuestionRequest {

	private int currentQuestionIndex;
	private String roomId;
	private String submittedPlayerId;
	private String questionId;

	public int getCurrentQuestionIndex() {
		return currentQuestionIndex;
	}

	public void setCurrentQuestionIndex(int currentQuestionIndex) {
		this.currentQuestionIndex = currentQuestionIndex;
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

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

}
