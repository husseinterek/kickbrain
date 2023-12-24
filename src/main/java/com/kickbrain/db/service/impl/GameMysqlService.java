package com.kickbrain.db.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kickbrain.beans.GameVO;
import com.kickbrain.beans.PremiumPointsHistoryVO;
import com.kickbrain.beans.WaitingGameVO;
import com.kickbrain.db.model.Game;
import com.kickbrain.db.model.PremiumPointsHistory;
import com.kickbrain.db.model.WaitingGame;
import com.kickbrain.db.repository.GameRepository;
import com.kickbrain.db.repository.PremiumPointsHistoryRepository;
import com.kickbrain.db.repository.WaitingGameRepository;
import com.kickbrain.db.service.GameService;
import com.kickbrain.manager.Utility;

@Service
@Transactional
public class GameMysqlService implements GameService {

	@Autowired
	private GameRepository gameDao;
	
	@Autowired
	private WaitingGameRepository waitingGameDao;
	
	@Autowired
	private PremiumPointsHistoryRepository premiumPointsHistoryDao;
	
	@Override
	public GameVO createGame(GameVO gameVO) {
		Game game = Utility.convertGameVOToGameModel(gameVO);
		
		// Remove non-BMP characters
		if(game.getAnonymousPlayer1() != null)
		{
			game.setAnonymousPlayer1(game.getAnonymousPlayer1().replaceAll("[^\u0000-\uFFFF]", ""));
		}
		
		if(game.getAnonymousPlayer2() != null)
		{
			game.setAnonymousPlayer2(game.getAnonymousPlayer2().replaceAll("[^\u0000-\uFFFF]", ""));
		}
		
		Game savedGame = gameDao.save(game);
		return Utility.convertGameModelToGameVO(savedGame);
	}
	
	@Override
	public WaitingGameVO createWaitingGame(WaitingGameVO waitingGameVO) {
	
		WaitingGame waitingGame = Utility.convertWaitingGameVOToModel(waitingGameVO);
		
		// Remove non-BMP characters
		if(waitingGameVO.getAnonymousPlayer() != null)
		{
			waitingGame.setAnonymousPlayer(waitingGameVO.getAnonymousPlayer().replaceAll("[^\u0000-\uFFFF]", ""));
		}
		
		return Utility.convertWaitingGameModelToVO(waitingGameDao.save(waitingGame));
	}
	
	@Override
	public List<GameVO> retrieveGamesByPlayer(long playerId) {
		
		List<GameVO> result = new ArrayList<GameVO>();
		
		List<Game> games = gameDao.findSqlQuery("SELECT * FROM GAMES where player1_id = "+playerId+" or player2_id = "+playerId+" order by creation_date desc ", Game.class);
		for(Game game : games)
		{
			result.add(Utility.convertGameModelToGameVO(game));
		}
		
		return result;
	}
	
	@Override
	public GameVO retrieveGame(long id) {
		
		return Utility.convertGameModelToGameVO(gameDao.findById(id).get());
	}
	
	@Override
	public WaitingGameVO updateWaitingGame(long roomId, int status) {
		
		WaitingGame waitingGame = waitingGameDao.findById(roomId).get();
		waitingGame.setStatus(status);

		waitingGame = waitingGameDao.save(waitingGame);
		return Utility.convertWaitingGameModelToVO(waitingGame);
	}
	
	@Override
	public WaitingGameVO getWaitingGameById(long roomId) {
		
		WaitingGameVO waitingGameVO = null;
		List<WaitingGame> waitingGames = waitingGameDao.findSqlQuery("SELECT * FROM WAITING_GAMES where id = "+roomId+" and status = 1", WaitingGame.class);
		
		if(waitingGames != null && waitingGames.size() > 0)
		{
			WaitingGame waitingGame = waitingGames.get(0);
			waitingGameVO = Utility.convertWaitingGameModelToVO(waitingGame);
		}
		
		return waitingGameVO;
	}
	
	@Override
	public List<WaitingGameVO> getAvailableWaitingRooms() {
		
		List<WaitingGameVO> result = new ArrayList<WaitingGameVO>();
		List<WaitingGame> waitingGames = waitingGameDao.findSqlQuery("SELECT * FROM WAITING_GAMES where status = 1 order by id desc", WaitingGame.class);
		
		for(WaitingGame waitingGame : waitingGames)
		{
			result.add(Utility.convertWaitingGameModelToVO(waitingGame));
		}
		
		return result;
	}
	
	@Override
	public List<WaitingGameVO> getWaitingGamesByPlayerId(long playerId) {
		
		List<WaitingGameVO> result = new ArrayList<WaitingGameVO>();
		List<WaitingGame> waitingGames = waitingGameDao.findSqlQuery("SELECT * FROM WAITING_GAMES where player_id = "+playerId+" and status = 1 order by id desc", WaitingGame.class);
		
		for(WaitingGame waitingGame : waitingGames)
		{
			result.add(Utility.convertWaitingGameModelToVO(waitingGame));
		}
		
		return result;
	}
	
	@Override
	public void deleteWaitingGamesForPlayer(long playerId) {
		
		List<WaitingGame> waitingGames = waitingGameDao.findSqlQuery("SELECT * FROM WAITING_GAMES where player_id = "+playerId+" and status = 1", WaitingGame.class);
		waitingGameDao.deleteAll(waitingGames);
	}
	
	@Override
	public void cancelActiveGame(long roomId, String playerId) {
		Game game = gameDao.findById(roomId).get();
		game.setIsCancelled(1);
		game.setCancelledBy(playerId);
		
		gameDao.save(game);
	}
	
	@Override
	public void cleanActiveGame(long roomId, String playerId) {
		Game game = gameDao.findById(roomId).get();
		game.setIsCleaned(1);
		game.setCleanedBy(playerId);
		
		gameDao.save(game);
	}
	
	@Override
	public void addPremiumPointsHistoryRecord(PremiumPointsHistoryVO premiumPointsHistoryVO) {
		
		PremiumPointsHistory premiumPointsHistory = Utility.convertPremiumPointsHistoryVOToModel(premiumPointsHistoryVO);
		premiumPointsHistoryDao.save(premiumPointsHistory);
	}
}
