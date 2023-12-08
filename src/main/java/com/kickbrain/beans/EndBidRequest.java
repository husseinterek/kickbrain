package com.kickbrain.beans;

public class EndBidRequest {

	private int questionId;
	private int roomId;
	private String playerId;
	private int winningBid;

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getWinningBid() {
		return winningBid;
	}

	public void setWinningBid(int winningBid) {
		this.winningBid = winningBid;
	}

}
