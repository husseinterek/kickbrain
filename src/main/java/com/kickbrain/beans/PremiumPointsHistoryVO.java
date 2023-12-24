package com.kickbrain.beans;

import java.util.Date;

public class PremiumPointsHistoryVO {

	private long id;
	private long playerId;
	private long gameId;
	private int scorePoints;
	private float premiumPoints;
	private float conversionRatio;
	private Date creationDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public long getGameId() {
		return gameId;
	}

	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

	public int getScorePoints() {
		return scorePoints;
	}

	public void setScorePoints(int scorePoints) {
		this.scorePoints = scorePoints;
	}

	public float getPremiumPoints() {
		return premiumPoints;
	}

	public void setPremiumPoints(float premiumPoints) {
		this.premiumPoints = premiumPoints;
	}

	public float getConversionRatio() {
		return conversionRatio;
	}

	public void setConversionRatio(float conversionRatio) {
		this.conversionRatio = conversionRatio;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
