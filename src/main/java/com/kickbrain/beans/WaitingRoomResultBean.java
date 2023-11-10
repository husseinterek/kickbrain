package com.kickbrain.beans;

public class WaitingRoomResultBean {

	private String roomId;
	private Player hostingPlayer;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Player getHostingPlayer() {
		return hostingPlayer;
	}

	public void setHostingPlayer(Player hostingPlayer) {
		this.hostingPlayer = hostingPlayer;
	}
}
