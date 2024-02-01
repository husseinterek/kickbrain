package com.kickbrain.manager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kickbrain.beans.AnswerVO;
import com.kickbrain.beans.ChallengeBean;
import com.kickbrain.beans.GameDetailsVO;
import com.kickbrain.beans.GameHistoryRecord;
import com.kickbrain.beans.GameHistoryReportResult;
import com.kickbrain.beans.GameReport;
import com.kickbrain.beans.GameReportQuestion;
import com.kickbrain.beans.GameReportResult;
import com.kickbrain.beans.GameRequest;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.GameVO;
import com.kickbrain.beans.GamesHistoryResult;
import com.kickbrain.beans.Player;
import com.kickbrain.beans.PremiumPointsHistoryVO;
import com.kickbrain.beans.QuestionResult;
import com.kickbrain.beans.SingleGameReport;
import com.kickbrain.beans.UserVO;
import com.kickbrain.beans.WaitingGameVO;
import com.kickbrain.beans.configuration.ChallengeConfig;
import com.kickbrain.beans.configuration.GameConfig;
import com.kickbrain.beans.configuration.QuestionVO;
import com.kickbrain.configuration.XMLConfigurationManager;
import com.kickbrain.db.service.GameService;
import com.kickbrain.db.service.QuestionService;
import com.kickbrain.db.service.UserService;

@Component
public class GameRoomManager {

	private Map<String, GameRoom> activeGameRooms = new ConcurrentHashMap<String, GameRoom>();
	//private Map<String, GameRoom> waitingGameRooms = new ConcurrentHashMap<String, GameRoom>();
	private Map<String, GameReport> gameReports = new ConcurrentHashMap<String, GameReport>();
	private DecimalFormat df = new DecimalFormat("0.00");
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private GameService gameService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private FirebaseMessaging firebaseMessaging;
	
	@Autowired
    private WebSocketManager webSocketManager;
	
	@Autowired
    private XMLConfigurationManager xmlConfigurationManager;
	
	public GameRoom createWaitingGameRoom(GameRequest request, String sessionId) {
	    
		List<WaitingGameVO> availableWaitingRooms = new ArrayList<WaitingGameVO>();
		Player player = request.getPlayer();
		if(player.getPlayerId() != null)
		{
			availableWaitingRooms = gameService.getWaitingGamesByPlayerId(Long.valueOf(player.getPlayerId()));
		}
		
		WaitingGameVO waitingGameVO = new WaitingGameVO();
		if(availableWaitingRooms.size() > 0)
		{
			waitingGameVO = availableWaitingRooms.get(0);
		}
		else
		{
			waitingGameVO.setPlayer(player);
			waitingGameVO.setCreationDate(new Date());
			waitingGameVO.setAnonymousPlayer(player.getPlayerId() == null ? player.getUsername() : null);
			waitingGameVO.setSessionId(sessionId);
			waitingGameVO.setStatus(1);
			waitingGameVO.setDeviceToken(player.getDeviceToken());
			waitingGameVO.setIsPrivate(request.getIsPrivate());
			waitingGameVO.setPasscode(request.getPasscode());
			waitingGameVO = gameService.createWaitingGame(waitingGameVO);
		}
		
		long roomId = waitingGameVO.getId();
		// Create a new game room
		GameRoom gameRoom = new GameRoom();
		gameRoom.setRoomId(String.valueOf(roomId));
		
		if(player.getPlayerId() == null)
		{
			// Anonymous user
			player.setPlayerId(generateUniqueId());
			player.setAnonymous(true);
		}
		else
		{
			player.setAnonymous(false);
		}
		gameRoom.setPlayer1(player);
		
		List<String> playersSessions = new ArrayList<String>();
		playersSessions.add(sessionId);
		gameRoom.setPlayersSessions(playersSessions);
		
	    return gameRoom;
	}
	
	public GameRoom createSingleGameRoom(String username, String playerId) {
		
	    // Create a new game room
		GameRoom gameRoom = new GameRoom();
		gameRoom.setRoomId(generateRoomId());
		
		Player player1 = new Player(generateUniqueId(), username);
		gameRoom.setPlayer1(player1);
		
		List<QuestionVO> storeQuestions = generateGameQuestions(playerId, 1, 10);
		
		List<QuestionResult> questionsResult = new ArrayList<QuestionResult>();
		for(QuestionVO question : storeQuestions)
		{
			QuestionResult questionResult = new QuestionResult();
			questionResult.setId(question.getId());
			questionResult.setQuestionAr(question.getPromptAr());
			questionResult.setQuestionEn(question.getPromptEn());
			questionResult.setPossibleAnswers(question.getAnswers().size());
			
			questionsResult.add(questionResult);
		}
		
		gameRoom.setQuestions(questionsResult);

	    return gameRoom;
	}
	
	public GameRoom getGameRoomById(String roomId) {
		return activeGameRooms.get(roomId);
	}
	
	public WaitingGameVO getWaitingGameById(String roomId) {
		
		return gameService.getWaitingGameById(Long.valueOf(roomId));
	}
	
	/*public GameRoom findAvailableGameRoom(String username) {
	    // Iterate through the list of game rooms and find the first available room
	    for (GameRoom room : waitingGameRooms.values()) {
	        if (!room.isFull()) {
	        	if(!room.getPlayer1().getUsername().equalsIgnoreCase(username))
	        	{
	        		// if the requested player has the same username as existing player, don't return the room
	        		return room;
	        	}
	        }
	    }
	    return null; // If no available room is found
	}*/
	
	public GameRoom createActiveGameRoom(WaitingGameVO waitingGame, Player player)
	{
		GameRoom gameRoom = new GameRoom();
		gameRoom.setRoomId(String.valueOf(waitingGame.getId()));
		
		String player1Username = waitingGame.getAnonymousPlayer() != null ? waitingGame.getAnonymousPlayer() : waitingGame.getPlayer().getUsername();
		Player player1 = new Player(waitingGame.getPlayer().getPlayerId(), player1Username);
		
		String player1DisplayName = waitingGame.getAnonymousPlayer() != null ? waitingGame.getAnonymousPlayer() : (waitingGame.getPlayer().getFirstName() + " " + waitingGame.getPlayer().getLastName());
		player1.setDisplayName(player1DisplayName);
		player1.setDeviceToken(waitingGame.getDeviceToken());
		
		if(waitingGame.getAnonymousPlayer() == null)
		{
			player1.setFirstName(waitingGame.getPlayer().getFirstName());
			player1.setLastName(waitingGame.getPlayer().getLastName());
		}
		gameRoom.setPlayer1(player1);
		
		GameConfig onlineGameConfig = xmlConfigurationManager.getAppConfigurationBean().getOnlineGameConfig();
		List<ChallengeConfig> onlineChallengesConfig = onlineGameConfig.getChallenges();
		
		Map<Integer, List<AnswerVO>> answersByQuestion = new HashMap<Integer, List<AnswerVO>>();
		List<QuestionResult> questionsResult = new ArrayList<QuestionResult>();
		List<ChallengeBean> onlineChallenges = new ArrayList<ChallengeBean>();
		Map<Integer, ChallengeBean> challengesMap = new HashMap<Integer, ChallengeBean>();
		for(ChallengeConfig onlineChallengeConfig : onlineChallengesConfig)
		{
			int category = onlineChallengeConfig.getCategory();
			int nbQuestions = onlineChallengeConfig.getNbQuestions();
			
			ChallengeBean onlineChallengeBean = new ChallengeBean();
			onlineChallengeBean.setTitleAr(onlineChallengeConfig.getTitleAr());
			onlineChallengeBean.setTitleEn(onlineChallengeConfig.getTitleEn());
			onlineChallengeBean.setCategory(category);
			
			//List<QuestionVO> questions = questionService.retrieveRandomQuestions(category, nbQuestions);
			
			List<QuestionVO> questions = new ArrayList<QuestionVO>();
			if(waitingGame.getAnonymousPlayer() == null)
			{
				// Retrieve questions based on player1
				questions = generateGameQuestions(waitingGame.getPlayer().getPlayerId(), category, nbQuestions);
			}
			else
			{
				if(player.getPlayerId() != null)
				{
					// Retrieve questions based on player2
					questions = generateGameQuestions(player.getPlayerId(), category, nbQuestions);
				}
				else
				{
					questions = questionService.retrieveRandomQuestions(category, nbQuestions);
				}
			}
			List<QuestionResult> challengeQuestions = new ArrayList<QuestionResult>();
			
			for(QuestionVO question : questions)
			{
				QuestionResult questionResult = new QuestionResult();
				questionResult.setId(question.getId());
				questionResult.setQuestionAr(question.getPromptAr());
				questionResult.setQuestionEn(question.getPromptEn());
				questionResult.setPossibleAnswers(question.getAnswers().size());
				questionResult.setChallengeCategory(category);
				
				answersByQuestion.put(question.getId(), question.getAnswers());
				
				questionsResult.add(questionResult);
				challengeQuestions.add(questionResult);
			}

			onlineChallengeBean.setQuestions(challengeQuestions);
			onlineChallenges.add(onlineChallengeBean);
			challengesMap.put(onlineChallengeBean.getCategory(), onlineChallengeBean);
		}
		gameRoom.setQuestions(questionsResult);
		gameRoom.setAnswersByQuestion(answersByQuestion);
		gameRoom.setChallenges(onlineChallenges);
		gameRoom.setChallengesMap(challengesMap);
		
		if(player.getPlayerId() == null)
		{
			player.setPlayerId(generateUniqueId());
			player.setDisplayName(player.getUsername());
			player.setAnonymous(true);
		}
		else
		{
			UserVO user = userService.findById(Long.valueOf(player.getPlayerId()));
			player.setFirstName(user.getFirstName());
			player.setLastName(user.getLastName());
			player.setDisplayName(user.getFirstName() + " " + user.getLastName());
			player.setAnonymous(false);
		}
		gameRoom.setPlayer2(player);
		
		activeGameRooms.put(gameRoom.getRoomId(), gameRoom);
		
		// Close the waiting game
		System.out.println("Waiting room with Id: " + waitingGame.getId() + " is converted to an active game!");
		gameService.updateWaitingGame(waitingGame.getId(), 2);
		webSocketManager.getWaitingSessions().remove(waitingGame.getSessionId());
		webSocketManager.getLastWaitPingTimes().remove(waitingGame.getSessionId());
		webSocketManager.getWaitingSessionToPlayerMap().remove(waitingGame.getSessionId());
		
		// create game report and initiate scores
		GameReport gameReport = new GameReport();
		gameReport.setRoomId(gameRoom.getRoomId());
		
		Map<String, Integer> playersScores = gameReport.getPlayersScore();
		playersScores.put(gameRoom.getPlayer1().getPlayerId(), 0);
		playersScores.put(gameRoom.getPlayer2().getPlayerId(), 0);
		gameReport.setPlayersScore(playersScores);
		
		gameReports.put(gameRoom.getRoomId(), gameReport);
		
		return gameRoom;
	}
	
	public void addSuccessfulAnswerToGameReport(String roomId, String questionId, AnswerVO answer, String playerId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport == null)
		{
			gameReport = new GameReport();
			gameReport.setRoomId(roomId);
		}
		Map<String, Map<String, List<AnswerVO>>> playersAnswersByQuestion = gameReport.getPlayersAnswersByQuestion();
		
		Map<String, List<AnswerVO>> playersAnswers = playersAnswersByQuestion.get(questionId);
		if(playersAnswers == null)
		{
			playersAnswers = new HashMap<String, List<AnswerVO>>();
		}
		
		List<AnswerVO> answers = playersAnswers.get(playerId);
		if(answers == null)
		{
			answers = new ArrayList<AnswerVO>();
		}
		answers.add(answer);
		
		playersAnswers.put(playerId, answers);
		playersAnswersByQuestion.put(questionId, playersAnswers);
		gameReport.setPlayersAnswersByQuestion(playersAnswersByQuestion);
		
		gameReports.put(roomId, gameReport);
	}
	
	public List<AnswerVO> getPlayerAnswersByGameAndQuestion(String roomId, String questionId, String playerId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport != null)
		{
			Map<String, Map<String, List<AnswerVO>>> playersAnswersByQuestion = gameReport.getPlayersAnswersByQuestion();
			
			Map<String, List<AnswerVO>> playersAnswers = playersAnswersByQuestion.get(questionId);
			if(playersAnswers != null)
			{
				return playersAnswers.get(playerId);
			}
			else
			{
				return new ArrayList<AnswerVO>();
			}
		}
		else
		{
			return new ArrayList<AnswerVO>();
		}
	}
	
	public Integer getPlayerStrikesByGameAndQuestion(String roomId, String questionId, String playerId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport != null)
		{
			Map<String, Map<String, Integer>> playersStrikesByQuestion = gameReport.getPlayersStrikesByQuestion();
			
			Map<String, Integer> playersStrikes = playersStrikesByQuestion.get(questionId);
			if(playersStrikes != null)
			{
				return playersStrikes.get(playerId);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	public void addPlayerStrikeToGameReport(String roomId, String questionId, String playerId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport == null)
		{
			gameReport = new GameReport();
			gameReport.setRoomId(roomId);
		}
		Map<String, Map<String, Integer>> playersStrikesByQuestion = gameReport.getPlayersStrikesByQuestion();
		
		Map<String, Integer> playersStrikes = playersStrikesByQuestion.get(questionId);
		if(playersStrikes == null)
		{
			playersStrikes = new HashMap<String, Integer>();
		}
		
		Integer strikes = playersStrikes.get(playerId);
		if(strikes == null)
		{
			strikes = 1;
		}
		else
		{
			strikes++;
		}
		
		playersStrikes.put(playerId, strikes);
		playersStrikesByQuestion.put(questionId, playersStrikes);
		gameReport.setPlayersStrikesByQuestion(playersStrikesByQuestion);
		
		gameReports.put(roomId, gameReport);
	}
	
	public void addPlayerScoreToGameReport(String roomId, String playerId, String questionId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport == null)
		{
			gameReport = new GameReport();
			gameReport.setRoomId(roomId);
		}
		Map<String, Integer> playersScoreByGame = gameReport.getPlayersScore();
		
		Integer score = playersScoreByGame.get(playerId);
		if(score == null)
		{
			score = 1;
		}
		else
		{
			score++;
		}
		
		playersScoreByGame.put(playerId, score);
		
		Map<String, String> questionsResult = gameReport.getQuestionsResult();
		questionsResult.put(questionId, playerId);
		
		gameReport.setQuestionsResult(questionsResult);
		gameReport.setPlayersScore(playersScoreByGame);
		
		gameReports.put(roomId, gameReport);
	}
	
	public void addNoAnswerToGameReport(String roomId, String questionId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport == null)
		{
			gameReport = new GameReport();
			gameReport.setRoomId(roomId);
		}
		
		Map<String, String> questionsResult = gameReport.getQuestionsResult();
		questionsResult.put(questionId, "-1");
		
		gameReport.setQuestionsResult(questionsResult);
		
		gameReports.put(roomId, gameReport);
	}
	
	public void setQuestionResultasTie(String roomId, String questionId, String player1Id, String player2Id)
	{
		GameReport gameReport = gameReports.get(roomId);
		
		Map<String, String> questionsResult = gameReport.getQuestionsResult();
		questionsResult.put(questionId, player1Id + "," + player2Id);
		gameReport.setQuestionsResult(questionsResult);
		
		gameReports.put(roomId, gameReport);
	}
	
	public Map<String, Integer> getPlayersScoresPerGame(String roomId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport == null)
		{
			gameReport = new GameReport();
			gameReport.setRoomId(roomId);
		}
		
		return gameReport.getPlayersScore();
	}
	
	public Map<String, String> getQuestionsResultPerGame(String roomId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport == null)
		{
			gameReport = new GameReport();
			gameReport.setRoomId(roomId);
		}
		
		return gameReport.getQuestionsResult();
	}
	
	public GameReportResult generateGameReportResult(String roomId)
	{
		GameReportResult gameReportResult = new GameReportResult();
		
		GameRoom gameRoom = getGameRoomById(roomId);
		
		Map<String, Integer> playersScores = getPlayersScoresPerGame(roomId);
		Integer player1Score = playersScores.get(gameRoom.getPlayer1().getPlayerId());
		Integer player2Score = playersScores.get(gameRoom.getPlayer2().getPlayerId());
		
		gameReportResult.setPlayer1Score(player1Score == null ? 0 : player1Score);
		gameReportResult.setPlayer2Score(player2Score == null ? 0 : player2Score);
		
		Map<String, String> questionsResult = getQuestionsResultPerGame(roomId);
		gameReportResult.setQuestionsResult(questionsResult);
		
		return gameReportResult;
	}
	
	public void flushGame(GameRoom game)
	{
		gameReports.remove(game.getRoomId());
		activeGameRooms.remove(game.getRoomId());
		
		List<String> playersSessions = game.getPlayersSessions();
		for(String session : playersSessions)
		{
			webSocketManager.getActiveSessions().remove(session);
			webSocketManager.getLastpingtimes().remove(session);
			webSocketManager.getSessionToPlayerMap().remove(session);
		}
	}
	
	public void cancelWaitingGame(String roomId)
	{
		WaitingGameVO waitingGame = gameService.updateWaitingGame(Long.valueOf(roomId), 3);
		
		webSocketManager.getWaitingSessions().remove(waitingGame.getSessionId());
		webSocketManager.getLastWaitPingTimes().remove(waitingGame.getSessionId());
		webSocketManager.getWaitingSessionToPlayerMap().remove(waitingGame.getSessionId());
	}
	
	public GameRoom getGameByPlayerId(String playerId)
	{
		Collection<GameRoom> gameRooms = activeGameRooms.values();
		GameRoom game = null;
		for(GameRoom gameRoom : gameRooms)
		{
			if((gameRoom.getPlayer1() != null && gameRoom.getPlayer1().getPlayerId().equalsIgnoreCase(playerId)) || (gameRoom.getPlayer2() != null && gameRoom.getPlayer2().getPlayerId().equalsIgnoreCase(playerId)))
			{
				game = gameRoom;
				break;
			}
		}
		
		return game;
	}
	
	public WaitingGameVO getWaitingGameByPlayerId(String playerId)
	{
		List<WaitingGameVO> waitingGames = gameService.getAvailableWaitingRooms();
		WaitingGameVO game = null;
		if(waitingGames != null)
		{
			for(WaitingGameVO waitingGame : waitingGames)
			{
				if((waitingGame.getPlayer() != null && waitingGame.getPlayer().getPlayerId().equalsIgnoreCase(playerId)))
				{
					game = waitingGame;
					break;
				}
			}
		}
		
		return game;
	}

	private String generateUniqueId() {
		return UUID.randomUUID().toString();
	}
	
	private String generateRoomId() {
		return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
	}

	public void addPlayerSessionToGame(String playerId, String session) {
		
		try
		{
			GameRoom gameRoom = getGameByPlayerId(playerId);
			if(gameRoom != null)
			{
				List<String> playersSessions = gameRoom.getPlayersSessions();
				if(playersSessions == null)
				{
					playersSessions = new ArrayList<String>();
				}
				playersSessions.add(session);
				gameRoom.setPlayersSessions(playersSessions);
				
				activeGameRooms.put(gameRoom.getRoomId(), gameRoom);
			}
		}
		catch(Exception ex)
		{
			System.out.println("An error ocurred while adding the session of player: " + playerId);
			ex.printStackTrace();
		}
	}
	
	public void sendPushNotificationToWaitingPlayer(Player player, String roomId)
	{
		try
		{
			if(StringUtils.isNotEmpty(player.getDeviceToken()))
			{
				String notificationContent = messageSource.getMessage("waitingRoom.pushNotificationContent", null, Locale.forLanguageTag("ar"));
				String notificationSubject = messageSource.getMessage("waitingRoom.pushNotificationSubject", null, Locale.forLanguageTag("ar"));
				
				Notification notification = Notification
		                .builder()
		                .setTitle(notificationSubject)
		                .setBody(notificationContent)
		                .build();
				
				Map<String, String> allData = new HashMap<String, String>();
				allData.put("playerId", player.getPlayerId());
				allData.put("roomId", roomId);
				allData.put("type", "GAME_STARTED");
				
				Message message = Message
		                .builder()
		                .setToken(player.getDeviceToken())
		                .setNotification(notification)
		                .putAllData(allData)
		                .build();
				
				firebaseMessaging.send(message);
			}
		}
		catch(Exception ex)
		{
			System.out.println("Failed to send push notification to player: " + player.getPlayerId() + " , token: "+player.getDeviceToken()+" and room: " + roomId);
			ex.printStackTrace();
		}
	}
	
	public List<WaitingGameVO> getWaitingGameRooms() {
		
		return gameService.getAvailableWaitingRooms();
	}
	
	public List<AnswerVO> retrievePossibleAnswersByQuestionId(int questionId)
	{
		return questionService.retrieveQuestionAnswers(questionId);
	}
	
	public GameVO persistGameRoom(GameRoom gameRoom, GameReport gameReport)
	{
		GameVO savedGame = null;
		try
		{
			GameVO gameVO = new GameVO();
			gameVO.setType(2);
			
			gameVO.setPlayer1(gameRoom.getPlayer1());
			gameVO.setPlayer1Score(gameReport.getPlayersScore().get(gameRoom.getPlayer1().getPlayerId()));
			
			gameVO.setPlayer2(gameRoom.getPlayer2());
			gameVO.setPlayer2Score(gameReport.getPlayersScore().get(gameRoom.getPlayer2().getPlayerId()));
			
			if(gameRoom.getPlayer1().getFirstName() == null && gameRoom.getPlayer1().getLastName() == null)
			{
				gameVO.setAnonymousPlayer1(gameRoom.getPlayer1().getUsername());
			}
			
			if(gameRoom.getPlayer2().getFirstName() == null && gameRoom.getPlayer2().getLastName() == null)
			{
				gameVO.setAnonymousPlayer2(gameRoom.getPlayer2().getUsername());
			}
			
			List<GameDetailsVO> gameDetailsVOLst = new ArrayList<GameDetailsVO>();
			Map<String, Map<String, List<AnswerVO>>> playersAnswersByQuestion = gameReport.getPlayersAnswersByQuestion();
			
			Map<String, String> questionsResult = gameReport.getQuestionsResult();
			for(Entry<String, String> entry : questionsResult.entrySet())
			{
				String questionId = entry.getKey();
				String playerId = entry.getValue();
				
				GameDetailsVO gameDetailsVO = new GameDetailsVO();
				
				QuestionVO questionVO = new QuestionVO();
				questionVO.setId( Integer.valueOf(questionId));
				gameDetailsVO.setQuestion(questionVO);
				
				gameDetailsVO.setWinnerId(playerId);
				
				Map<String, List<AnswerVO>> playersAnswers = playersAnswersByQuestion.get(questionId);
				if(playersAnswers != null)
				{
					List<AnswerVO> player1Answers = playersAnswers.get(gameRoom.getPlayer1().getPlayerId());
					List<AnswerVO> player2Answers = playersAnswers.get(gameRoom.getPlayer2().getPlayerId());
					
					gameDetailsVO.setPlayer1Score(player1Answers != null ? player1Answers.size() : 0);
					gameDetailsVO.setPlayer2Score(player2Answers != null ? player2Answers.size() : 0);
				}
				
				gameDetailsVOLst.add(gameDetailsVO);
			}
			
			gameVO.setGameDetails(gameDetailsVOLst);
			
			savedGame = gameService.createGame(gameVO);
			
			if(gameVO.getAnonymousPlayer1() == null)
			{
				int gameScore = gameReport.getPlayersScore().get(gameRoom.getPlayer1().getPlayerId());
				long playerId = Long.valueOf(gameRoom.getPlayer1().getPlayerId());
				userService.addUserScore(playerId, gameScore);
				
				// Convert game score to premium points
				if(gameScore > 0)
				{
					savePremiumPoints(gameScore, playerId, savedGame.getId());
				}
			}
			
			if(gameVO.getAnonymousPlayer2() == null)
			{
				int gameScore = gameReport.getPlayersScore().get(gameRoom.getPlayer2().getPlayerId());
				long playerId = Long.valueOf(gameRoom.getPlayer2().getPlayerId());
				userService.addUserScore(playerId, gameScore);
				
				// Convert game score to premium points
				if(gameScore > 0)
				{
					savePremiumPoints(gameScore, playerId, savedGame.getId());
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("An error occurred while persisting game: " + gameRoom.getRoomId());
			ex.printStackTrace();
		}
		
		return savedGame;
	}
	
	public void persistSingleGame(SingleGameReport gameReport)
	{
		GameVO gameVO = new GameVO();
		
		gameVO.setPlayer1(gameReport.getPlayer());
		gameVO.setPlayer1Score(gameReport.getTotalScore());
		gameVO.setType(1);
		
		if(gameReport.getPlayer().getPlayerId() == null)
		{
			gameVO.getPlayer1().setAnonymous(true);
			gameVO.setAnonymousPlayer1(gameReport.getPlayer().getUsername());
		}
		else
		{
			gameVO.getPlayer1().setAnonymous(false);
		}
		
		List<GameDetailsVO> gameDetailsVOs = new ArrayList<GameDetailsVO>();
		List<GameReportQuestion> questionsResult = gameReport.getQuestionsResult();
		for(GameReportQuestion questionResult : questionsResult)
		{
			GameDetailsVO gameDetailsVO = new GameDetailsVO();
			
			long questionId = questionResult.getQuestionId();
			boolean isSuccess = questionResult.isPassed();
			
			QuestionVO questionVO = new QuestionVO();
			questionVO.setId((int)questionId);
			gameDetailsVO.setQuestion(questionVO);
			
			gameDetailsVO.setWinnerId(isSuccess ? gameReport.getPlayer().getPlayerId() : null);
			
			gameDetailsVOs.add(gameDetailsVO);
		}
		gameVO.setGameDetails(gameDetailsVOs);
		
		gameService.createGame(gameVO);
		
		/*if(!gameReport.getPlayer().isAnonymous())
		{
			userService.addUserScore(Long.valueOf(gameReport.getPlayer().getPlayerId()), gameReport.getTotalScore());
		}*/
	}
	
	public GameReport getGameReport(String gameRoomId)
	{
		return gameReports.get(gameRoomId);
	}
	
	public GamesHistoryResult retrieveGamesHistory(long playerId)
	{
		GamesHistoryResult result = new GamesHistoryResult();
		
		List<GameHistoryRecord> records = new ArrayList<GameHistoryRecord>();
		List<GameVO> games = gameService.retrieveGamesByPlayer(playerId);
		for(GameVO gameVO : games)
		{
			GameHistoryRecord record = new GameHistoryRecord();
			record.setGameId(gameVO.getId());
			record.setType(gameVO.getType());
			record.setGameDate(gameVO.getCreationDate());
			
			if(gameVO.getPlayer1().getPlayerId().equalsIgnoreCase(String.valueOf(playerId)))
			{
				record.setCurrentUserScore(gameVO.getPlayer1Score());
				
				if(gameVO.getType() == 2)
				{
					record.setOpponentUserScore(gameVO.getPlayer2Score());
					record.setOpponentUsername(gameVO.getPlayer2().getUsername());
					record.setWinner(gameVO.getPlayer1Score() > gameVO.getPlayer2Score());
					record.setTie(gameVO.getPlayer1Score() == gameVO.getPlayer2Score());
				}
			}
			else
			{
				record.setCurrentUserScore(gameVO.getPlayer2Score());
				
				if(gameVO.getType() == 2)
				{
					record.setOpponentUserScore(gameVO.getPlayer1Score());
					record.setOpponentUsername(gameVO.getPlayer1().getUsername());
					record.setWinner(gameVO.getPlayer2Score() > gameVO.getPlayer1Score());
					record.setTie(gameVO.getPlayer1Score() == gameVO.getPlayer2Score());
				}
			}
			
			records.add(record);
		}
		result.setGames(records);
		
		return result;
	}
	
	public GameHistoryReportResult retrieveGameHistoryReport(long gameId, long userId)
	{
		GameHistoryReportResult result = new GameHistoryReportResult();
		
		GameVO gameVO = gameService.retrieveGame(gameId);
		
		result.setType(gameVO.getType());
		result.setGameId((int)gameId);
		
		if(gameVO.getType() == 1)
		{
			result.setSingleGameTotalScore(gameVO.getPlayer1Score());
		}
		else
		{
			String player1Username = gameVO.getPlayer1().isAnonymous() ? gameVO.getAnonymousPlayer1() : gameVO.getPlayer1().getUsername();
			String player2Username = gameVO.getPlayer2().isAnonymous() ? gameVO.getAnonymousPlayer2() : gameVO.getPlayer2().getUsername();
			
			if(gameVO.getPlayer1Score() != gameVO.getPlayer2Score())
			{
				if(gameVO.getPlayer1Score() > gameVO.getPlayer2Score())
				{
					result.setOnlineGameWinner(player1Username);
				}
				else
				{
					result.setOnlineGameWinner(player2Username);
				}
			}
			else
			{
				result.setOnlineGameIsTie(true);
			}
		}
		
		result.setGameDetails(gameVO.getGameDetails());
		
		return result;
	}
	
	public void addBidToGameReport(String roomId, String questionId, Integer bid, String playerId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport == null)
		{
			gameReport = new GameReport();
			gameReport.setRoomId(roomId);
		}
		Map<String, Map<String, Integer>> playersBidByQuestion = gameReport.getPlayersBidByQuestion();
		
		Map<String, Integer> playersBids = playersBidByQuestion.get(questionId);
		if(playersBids == null)
		{
			playersBids = new HashMap<String, Integer>();
		}
		
		playersBids.put(playerId, bid);
		playersBidByQuestion.put(questionId, playersBids);
		gameReport.setPlayersBidByQuestion(playersBidByQuestion);
		
		gameReports.put(roomId, gameReport);
	}
	
	public Map<String, Integer> getPlayerBidsFromGameReport(String roomId, String questionId)
	{
		GameReport gameReport = gameReports.get(roomId);
		Map<String, Map<String, Integer>> playersBidByQuestion = gameReport.getPlayersBidByQuestion();
		Map<String, Integer> playersBids = playersBidByQuestion.get(questionId);
		if(playersBids == null)
		{
			playersBids = new HashMap<String, Integer>();
		}
		
		return playersBids;
	}
	
	public void cancelActiveGame(String roomId, String playerId)
	{
		gameService.cancelActiveGame(Long.valueOf(roomId), playerId);
	}
	
	public void cleanActiveGame(String roomId, String playerId)
	{
		gameService.cleanActiveGame(Long.valueOf(roomId), playerId);
	}
	
	private List<QuestionVO> generateGameQuestions(String playerId, int category, int limit)
	{
		List<QuestionVO> storeQuestions = new ArrayList<QuestionVO>();
		if(playerId != null)
		{
			storeQuestions = questionService.retrieveUnPlayedQuestionsForPlayer(Integer.valueOf(playerId), category);
			
			if(storeQuestions.size() < limit)
			{
				// Return the played questions for this player and complete the list
				//List<QuestionVO> leastPlayedQuestions = questionService.retrieveLeastPlayedQuestionsForPlayer(Integer.valueOf(playerId), category, limit);
				List<QuestionVO> playedQuestions = questionService.retrievePlayedQuestionsForPlayer(Integer.valueOf(playerId), category, limit);
				
				int remainingQuestions = limit - storeQuestions.size();
				storeQuestions.addAll(playedQuestions.stream().limit(remainingQuestions).collect(Collectors.toList()));
			}
			else
			{
				storeQuestions = storeQuestions.stream().limit(limit).collect(Collectors.toList());
			}
		}
		else
		{
			storeQuestions = questionService.retrieveRandomQuestions(category, limit);
		}
		
		return storeQuestions;
	}
	
	private void savePremiumPoints(int gameScore, long playerId, long roomId)
	{
		// Convert the earned game score to premium points
		float premiumPointsRatio = xmlConfigurationManager.getAppConfigurationBean().getPremiumPointsRatio();
		float premiumPoints = gameScore * premiumPointsRatio;
		premiumPoints = Float.valueOf(df.format(premiumPoints));
		
		// Add premium points to the user's record
		userService.addUserPremiumPoints(playerId, premiumPoints);
		
		// Save history record
		PremiumPointsHistoryVO premiumPointsHistoryVO = new PremiumPointsHistoryVO();
		premiumPointsHistoryVO.setPlayerId(playerId);
		premiumPointsHistoryVO.setGameId(roomId);
		premiumPointsHistoryVO.setScorePoints(gameScore);
		premiumPointsHistoryVO.setPremiumPoints(premiumPoints);
		premiumPointsHistoryVO.setConversionRatio(premiumPointsRatio);
		premiumPointsHistoryVO.setCreationDate(new Date());
		
		gameService.addPremiumPointsHistoryRecord(premiumPointsHistoryVO);
	}
}
