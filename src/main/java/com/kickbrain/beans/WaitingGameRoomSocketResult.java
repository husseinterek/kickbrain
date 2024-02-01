package com.kickbrain.beans;

public class WaitingGameRoomSocketResult extends BaseResult {

	private WaitingRoom waitingRoom;
	private boolean hasSufficientPoints;

	public boolean isHasSufficientPoints() {
		return hasSufficientPoints;
	}

	public void setHasSufficientPoints(boolean hasSufficientPoints) {
		this.hasSufficientPoints = hasSufficientPoints;
	}

	public WaitingRoom getWaitingRoom() {
		return waitingRoom;
	}

	public void setWaitingRoom(WaitingRoom waitingRoom) {
		this.waitingRoom = waitingRoom;
	}

}
