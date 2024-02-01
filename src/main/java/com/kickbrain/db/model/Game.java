package com.kickbrain.db.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "GAMES")
@NamedQuery(name = "Game.findAll", query = "SELECT g FROM Game g")
public class Game implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name = "GamesSeqStore", table = "SEQUENCE_STORE", pkColumnName = "SEQ_NAME", pkColumnValue = "GAMES_SEQ", valueColumnName = "SEQ_VALUE", initialValue = 4, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GamesSeqStore")
	private long id;

	@ManyToOne
	@JoinColumn(name = "PLAYER1_ID")
	private User player1;

	@ManyToOne
	@JoinColumn(name = "PLAYER2_ID")
	private User player2;

	@Column(name = "TYPE")
	private int type;

	@Column(name = "CREATION_DATE")
	private Date creationDate;

	@Column(name = "PLAYER1_SCORE")
	private int player1Score;

	@Column(name = "PLAYER2_SCORE")
	private int player2Score;

	@Column(name = "ANONYMOUS_PLAYER1")
	private String anonymousPlayer1;

	@Column(name = "ANONYMOUS_PLAYER2")
	private String anonymousPlayer2;

	// bi-directional many-to-one association to ReportMetricGroupField
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "game")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<GameDetails> gameDetails;

	@Column(name = "IS_CANCELLED")
	private int isCancelled;

	@Column(name = "IS_CLEANED")
	private int isCleaned;

	@Column(name = "CANCELLED_BY")
	private String cancelledBy;

	@Column(name = "CLEANED_BY")
	private String cleanedBy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getPlayer1() {
		return player1;
	}

	public void setPlayer1(User player1) {
		this.player1 = player1;
	}

	public User getPlayer2() {
		return player2;
	}

	public void setPlayer2(User player2) {
		this.player2 = player2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public List<GameDetails> getGameDetails() {
		return gameDetails;
	}

	public void setGameDetails(List<GameDetails> gameDetails) {
		this.gameDetails = gameDetails;
	}

	public void setAnonymousPlayer1(String anonymousPlayer1) {
		this.anonymousPlayer1 = anonymousPlayer1;
	}

	public void setAnonymousPlayer2(String anonymousPlayer2) {
		this.anonymousPlayer2 = anonymousPlayer2;
	}

	public String getAnonymousPlayer1() {
		return anonymousPlayer1;
	}

	public String getAnonymousPlayer2() {
		return anonymousPlayer2;
	}

	public int getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(int isCancelled) {
		this.isCancelled = isCancelled;
	}

	public int getIsCleaned() {
		return isCleaned;
	}

	public void setIsCleaned(int isCleaned) {
		this.isCleaned = isCleaned;
	}

	public String getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public String getCleanedBy() {
		return cleanedBy;
	}

	public void setCleanedBy(String cleanedBy) {
		this.cleanedBy = cleanedBy;
	}
	
}
