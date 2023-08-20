package com.kickbrain.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kickbrain.beans.GameReport;
import com.kickbrain.beans.GameReportResult;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.Player;
import com.kickbrain.beans.QuestionResult;
import com.kickbrain.beans.configuration.Question;
import com.kickbrain.configuration.XMLConfigurationManager;

@Component
public class GameRoomManager {

	private Map<String, GameRoom> activeGameRooms = new ConcurrentHashMap<String, GameRoom>();
	private Map<String, GameRoom> waitingGameRooms = new ConcurrentHashMap<String, GameRoom>();
	private Map<String, GameReport> gameReports = new ConcurrentHashMap<String, GameReport>();
	
	@Autowired
	private XMLConfigurationManager xmlConfigurationManager;
	
	public GameRoom createWaitingGameRoom(Player player, String sessionId) {
	    // Create a new game room
		GameRoom gameRoom = new GameRoom();
		gameRoom.setRoomId(generateRoomId());
		
		player.setPlayerId(generateUniqueId());
		gameRoom.setPlayer1(player);
		
		List<String> playersSessions = new ArrayList<String>();
		playersSessions.add(sessionId);
		gameRoom.setPlayersSessions(playersSessions);
		
		List<Question> storeQuestions = new ArrayList<Question>(xmlConfigurationManager.getAppConfigurationBean().getQuestions());
		Collections.shuffle(storeQuestions);
		storeQuestions = storeQuestions.subList(0, 10);
		
		List<QuestionResult> questionsResult = new ArrayList<QuestionResult>();
		for(Question question : storeQuestions)
		{
			QuestionResult questionResult = new QuestionResult();
			questionResult.setId(question.getId());
			questionResult.setQuestionAr(question.getPromptAr());
			questionResult.setQuestionEn(question.getPromptEn());
			
			questionsResult.add(questionResult);
		}
		
		gameRoom.setQuestions(questionsResult);

	    // Add the new game room to the list of game rooms
		waitingGameRooms.put(gameRoom.getRoomId(), gameRoom);

	    return gameRoom;
	}
	
	public GameRoom createSingleGameRoom(String username) {
		
	    // Create a new game room
		GameRoom gameRoom = new GameRoom();
		gameRoom.setRoomId(generateRoomId());
		
		Player player1 = new Player(generateUniqueId(), username);
		gameRoom.setPlayer1(player1);
		
		List<Question> storeQuestions = new ArrayList<Question>(xmlConfigurationManager.getAppConfigurationBean().getQuestions());
		List<Question> filteredQuestions = new ArrayList<Question>();
		for(Question storeQuestion : storeQuestions)
		{
			// for single games, include only questions that have max 10 answers
			if(storeQuestion.getAnswers().size() <= 10)
			{
				filteredQuestions.add(storeQuestion);
			}
		}
		Collections.shuffle(filteredQuestions);
		filteredQuestions = filteredQuestions.subList(0, 10);
		
		List<QuestionResult> questionsResult = new ArrayList<QuestionResult>();
		for(Question question : filteredQuestions)
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
	
	public GameRoom getWaitingGameById(String roomId) {
		return waitingGameRooms.get(roomId);
	}
	
	public GameRoom findAvailableGameRoom() {
	    // Iterate through the list of game rooms and find the first available room
	    for (GameRoom room : waitingGameRooms.values()) {
	        if (!room.isFull()) {
	            return room;
	        }
	    }
	    return null; // If no available room is found
	}
	
	public GameRoom createActiveGameRoom(GameRoom room, Player player)
	{
		player.setPlayerId(generateUniqueId());
		room.setPlayer2(player);
		room.setPlayersSessions(null);
		activeGameRooms.put(room.getRoomId(), room);
		
		waitingGameRooms.remove(room.getRoomId());
		
		// create game report and initiate scores
		GameReport gameReport = new GameReport();
		gameReport.setRoomId(room.getRoomId());
		
		Map<String, Integer> playersScoreByGame = gameReport.getPlayersScore();
		playersScoreByGame.put(room.getPlayer1().getPlayerId(), 0);
		playersScoreByGame.put(room.getPlayer2().getPlayerId(), 0);
		gameReport.setPlayersScore(playersScoreByGame);
		
		gameReports.put(room.getRoomId(), gameReport);
		
		return room;
	}
	
	public void addSuccessfulAnswerToGameReport(String roomId, String questionId, String answer, String playerId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport == null)
		{
			gameReport = new GameReport();
			gameReport.setRoomId(roomId);
		}
		Map<String, Map<String, List<String>>> playersAnswersByQuestion = gameReport.getPlayersAnswersByQuestion();
		
		Map<String, List<String>> playersAnswers = playersAnswersByQuestion.get(questionId);
		if(playersAnswers == null)
		{
			playersAnswers = new HashMap<String, List<String>>();
		}
		
		List<String> answers = playersAnswers.get(playerId);
		if(answers == null)
		{
			answers = new ArrayList<String>();
		}
		answers.add(answer);
		
		playersAnswers.put(playerId, answers);
		playersAnswersByQuestion.put(questionId, playersAnswers);
		gameReport.setPlayersAnswersByQuestion(playersAnswersByQuestion);
		
		gameReports.put(roomId, gameReport);
	}
	
	public List<String> getPlayerAnswersByGameAndQuestion(String roomId, String questionId, String playerId)
	{
		GameReport gameReport = gameReports.get(roomId);
		if(gameReport != null)
		{
			Map<String, Map<String, List<String>>> playersAnswersByQuestion = gameReport.getPlayersAnswersByQuestion();
			
			Map<String, List<String>> playersAnswers = playersAnswersByQuestion.get(questionId);
			if(playersAnswers != null)
			{
				return playersAnswers.get(playerId);
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
		int player1Score = playersScores.get(gameRoom.getPlayer1().getPlayerId());
		int player2Score = playersScores.get(gameRoom.getPlayer2().getPlayerId());
		
		gameReportResult.setPlayer1Score(player1Score);
		gameReportResult.setPlayer2Score(player2Score);
		
		Map<String, String> questionsResult = getQuestionsResultPerGame(roomId);
		gameReportResult.setQuestionsResult(questionsResult);
		
		return gameReportResult;
	}
	
	public void flushGame(String roomId)
	{
		gameReports.remove(roomId);
		activeGameRooms.remove(roomId);
	}
	
	public void flushWaitingGame(String roomId)
	{
		waitingGameRooms.remove(roomId);
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
	
	public GameRoom getWaitingGameByPlayerId(String playerId)
	{
		Collection<GameRoom> gameRooms = waitingGameRooms.values();
		GameRoom game = null;
		for(GameRoom gameRoom : gameRooms)
		{
			if((gameRoom.getPlayer1() != null && gameRoom.getPlayer1().getPlayerId().equalsIgnoreCase(playerId)))
			{
				game = gameRoom;
				break;
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
		
		GameRoom gameRoom = getGameByPlayerId(playerId);
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
