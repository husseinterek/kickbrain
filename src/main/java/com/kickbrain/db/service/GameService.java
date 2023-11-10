package com.kickbrain.db.service;

import java.util.List;

import com.kickbrain.beans.GameVO;
import com.kickbrain.beans.WaitingGameVO;

public interface GameService {

	public void createGame(GameVO gameVO);
	public List<GameVO> retrieveGamesByPlayer(long playerId);
	public GameVO retrieveGame(long id);
	public WaitingGameVO createWaitingGame(WaitingGameVO waitingGameVO);
	public WaitingGameVO updateWaitingGame(long roomId, int status);
	public WaitingGameVO getWaitingGameById(long roomId);
	public List<WaitingGameVO> getAvailableWaitingRooms();
	public List<WaitingGameVO> getWaitingGamesByPlayerId(long playerId);
}
