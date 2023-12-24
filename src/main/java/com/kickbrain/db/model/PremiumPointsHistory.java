package com.kickbrain.db.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * The persistent class for the USERS database table.
 * 
 */
@Entity
@Table(name = "PREMIUM_POINTS_HISTORY")
@NamedQuery(name = "PremiumPointsHistory.findAll", query = "SELECT p FROM PremiumPointsHistory p")
public class PremiumPointsHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name = "PremiumPointsHistorySeqStore", table = "SEQUENCE_STORE", pkColumnName = "SEQ_NAME", pkColumnValue = "PREMIUM_POINTS_HISTORY_SEQ", valueColumnName = "SEQ_VALUE", initialValue = 4, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PremiumPointsHistorySeqStore")
	private long id;

	@Column(name = "PLAYER_ID")
	private long playerId;

	@Column(name = "GAME_ID")
	private long gameId;

	@Column(name = "SCORE_POINTS")
	private int scorePoints;

	@Column(name = "PREMIUM_POINTS")
	private float premiumPoints;

	@Column(name = "CONVERSION_RATIO")
	private float conversionRatio;

	@Column(name = "CREATION_DATE")
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