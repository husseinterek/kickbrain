package com.kickbrain.beans;

public class SubmitBidEvent {

	private String currentTurn;
	private int opponentLatestBid;
	private int currentTurnLatestBid;

	public String getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(String currentTurn) {
		this.currentTurn = currentTurn;
	}

	public int getOpponentLatestBid() {
		return opponentLatestBid;
	}

	public void setOpponentLatestBid(int opponentLatestBid) {
		this.opponentLatestBid = opponentLatestBid;
	}

	public int getCurrentTurnLatestBid() {
		return currentTurnLatestBid;
	}

	public void setCurrentTurnLatestBid(int currentTurnLatestBid) {
		this.currentTurnLatestBid = currentTurnLatestBid;
	}

}
