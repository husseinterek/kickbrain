package com.kickbrain.beans;

import java.util.Date;
import java.util.List;

public class GameVO {

	private long id;
	private Player player1;
	private Player player2;
	private Date creationDate;
	private int player1Score;
	private int player2Score;
	private int type;
	private List<GameDetailsVO> gameDetails;
	private String anonymousPlayer1;
	private String anonymousPlayer2;

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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getPlayer1Score() {
		return player1Score;
	}

	public void setPlayer1Score(int player1Score) {
		this.player1Score = player1Score;
	}

	public int getPlayer2Score() {
		return player2Score;
	}

	public void setPlayer2Score(int player2Score) {
		this.player2Score = player2Score;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<GameDetailsVO> getGameDetails() {
		return gameDetails;
	}

	public void setGameDetails(List<GameDetailsVO> gameDetails) {
		this.gameDetails = gameDetails;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAnonymousPlayer1() {
		return anonymousPlayer1;
	}

	public void setAnonymousPlayer1(String anonymousPlayer1) {
		this.anonymousPlayer1 = anonymousPlayer1;
	}

	public String getAnonymousPlayer2() {
		return anonymousPlayer2;
	}

	public void setAnonymousPlayer2(String anonymousPlayer2) {
		this.anonymousPlayer2 = anonymousPlayer2;
	}

}
