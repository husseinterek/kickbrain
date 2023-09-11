package com.kickbrain.beans;

public class Player {

	private String playerId;
	private String username;
	private String deviceToken;

	public Player(String playerId, String username) {
		this.playerId = playerId;
		this.username = username;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

}