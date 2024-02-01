package com.kickbrain.db.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "WAITING_GAMES")
@NamedQuery(name = "WaitingGame.findAll", query = "SELECT g FROM WaitingGame g")
public class WaitingGame implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@TableGenerator( 
	        name = "WaitingGamesSeqStore", 
	        table = "SEQUENCE_STORE", 
	        pkColumnName = "SEQ_NAME", 
	        pkColumnValue = "WAITING_GAMES_SEQ", 
	        valueColumnName = "SEQ_VALUE", 
	        initialValue = 4, 
	        allocationSize = 1 )
	@GeneratedValue( strategy = GenerationType.TABLE, generator = "WaitingGamesSeqStore")
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "PLAYER_ID")
	private User player;
	
	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	@Column(name = "ANONYMOUS_PLAYER")
	private String anonymousPlayer;
	
	@Column(name = "STATUS")
	private int status;
	
	@Column(name = "SESSION_ID")
	private String sessionId;
	
	@Column(name = "DEVICE_TOKEN")
	private String deviceToken;
	
	@Column(name = "IS_PRIVATE")
	private int isPrivate;
	
	@Column(name = "PASSCODE")
	private String passcode;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getPlayer() {
		return player;
	}

	public void setPlayer(User player) {
		this.player = player;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getAnonymousPlayer() {
		return anonymousPlayer;
	}

	public void setAnonymousPlayer(String anonymousPlayer) {
		this.anonymousPlayer = anonymousPlayer;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	
	public String getDeviceToken() {
		return deviceToken;
	}
	
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
	
	public String getPasscode() {
		return passcode;
	}

	public int getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(int isPrivate) {
		this.isPrivate = isPrivate;
	}
	
}
