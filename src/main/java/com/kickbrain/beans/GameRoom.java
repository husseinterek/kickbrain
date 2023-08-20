package com.kickbrain.beans;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {

	private String roomId;
	private Player player1;
	private Player player2;
	private List<QuestionResult> questions;
	private List<String> playersSessions = new ArrayList<String>();

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public List<QuestionResult> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionResult> questions) {
		this.questions = questions;
	}
	
	public boolean isFull()
	{
		return player1 != null && player2 != null;
	}

	public List<String> getPlayersSessions() {
		return playersSessions;
	}

	public void setPlayersSessions(List<String> playersSessions) {
		this.playersSessions = playersSessions;
	}

}