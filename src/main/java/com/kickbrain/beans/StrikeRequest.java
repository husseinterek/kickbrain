package com.kickbrain.beans;

public class StrikeRequest {

	private String roomId;
	private String questionId;
	private String submittedPlayer;
	private int currentQuestionIdx;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getSubmittedPlayer() {
		return submittedPlayer;
	}

	public void setSubmittedPlayer(String submittedPlayer) {
		this.submittedPlayer = submittedPlayer;
	}

	public int getCurrentQuestionIdx() {
		return currentQuestionIdx;
	}

	public void setCurrentQuestionIdx(int currentQuestionIdx) {
		this.currentQuestionIdx = currentQuestionIdx;
	}

}
