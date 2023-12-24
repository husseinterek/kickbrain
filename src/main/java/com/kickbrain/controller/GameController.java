package com.kickbrain.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.icu.text.Transliterator;
import com.kickbrain.beans.AnswerVO;
import com.kickbrain.beans.AuctionTimerCompleteRequest;
import com.kickbrain.beans.BaseResult;
import com.kickbrain.beans.GameReport;
import com.kickbrain.beans.GameResult;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.GameStartEvent;
import com.kickbrain.beans.GameVO;
import com.kickbrain.beans.JoinGameResult;
import com.kickbrain.beans.Player;
import com.kickbrain.beans.QuestionAnswersResult;
import com.kickbrain.beans.QuestionResult;
import com.kickbrain.beans.SingleGameReport;
import com.kickbrain.beans.SkipBellRequest;
import com.kickbrain.beans.StrikeRequest;
import com.kickbrain.beans.ValidateAnswerRequest;
import com.kickbrain.beans.ValidateAnswerResult;
import com.kickbrain.beans.ValidateSinglePlayerAnswerRequest;
import com.kickbrain.beans.ValidateSinglePlayerAnswerResult;
import com.kickbrain.beans.WaitingGameVO;
import com.kickbrain.beans.WaitingRoomResult;
import com.kickbrain.beans.WaitingRoomResultBean;
import com.kickbrain.challenges.AuctionChallengeManager;
import com.kickbrain.challenges.BellChallengeManager;
import com.kickbrain.challenges.ChallengeManager;
import com.kickbrain.challenges.WhatDoYouKnowChallengeManager;
import com.kickbrain.challenges.WhoAmIChallengeManager;
import com.kickbrain.configuration.XMLConfigurationManager;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.GameTimerManager;
import com.kickbrain.manager.Utility;
import com.kickbrain.manager.WebSocketManager;

import me.xdrop.fuzzywuzzy.FuzzySearch;

@RestController
@RequestMapping("/game")
public class GameController {
	
	@Autowired
	private GameRoomManager gameRoomManager;
	
	@Autowired
    private WebSocketManager webSocketManager;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private GameTimerManager gameTimerManager;
	
	@Autowired
	private XMLConfigurationManager xmlConfigurationManager;
	
	@Autowired
	private Environment env;
	
	@Autowired
	@Qualifier("gameProceedExecutor")
	private ThreadPoolTaskExecutor executor;
	
	private Map<String, Long> validateAnswerRequests = new ConcurrentHashMap<String, Long>();
	private Map<String, Long> joinRoomRequests = new ConcurrentHashMap<String, Long>();
	
	@RequestMapping(value = "/validateAnswer", method = RequestMethod.POST, consumes="application/json")
	public ValidateAnswerResult validateAnswer(@RequestBody ValidateAnswerRequest request) {
		
		ValidateAnswerResult result = new ValidateAnswerResult();
		
		// handle receiving duplicate validateAnswer requests within 2 seconds - we should consider only one
		String capturedAnswer = request.getCapturedAnswer();
		String questionId = request.getQuestionId();
		String playerId = request.getSubmittedPlayerId();
		String validateAnswerRequest = playerId + "_" + questionId + "_" + capturedAnswer;
		if(validateAnswerRequests.get(validateAnswerRequest) != null)
		{
			// duplicate requests detected
			result.setStatus(1);
		}
		else
		{
			validateAnswerRequests.put(validateAnswerRequest, System.currentTimeMillis());
			
			ChallengeManager challengeManager = null;
			if(request.getChallengeCategory() != null)
			{
				switch (request.getChallengeCategory()) {
					case 1:
						challengeManager = new WhatDoYouKnowChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
					case 2:
						challengeManager = new AuctionChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
					case 3:
						challengeManager = new BellChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
					case 4:
						challengeManager = new WhoAmIChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
					default:
						challengeManager = new WhatDoYouKnowChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
				}
			}
			else
			{
				challengeManager = new WhatDoYouKnowChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
			}
			
			result = challengeManager.validateAnswer(request);
		}
		
		return result;
	}
	
	@RequestMapping(value = "/validateSinglePlayerAnswer", method = RequestMethod.POST, consumes="application/json")
	public ValidateSinglePlayerAnswerResult validateSinglePlayerAnswer(@RequestBody ValidateSinglePlayerAnswerRequest request) {
		
		ValidateSinglePlayerAnswerResult result = new ValidateSinglePlayerAnswerResult();
		try
		{
			int minimumMatchingRatio = Integer.valueOf(env.getProperty("answer.matchingRatio"));

			List<AnswerVO> possibleAnswers = gameRoomManager.retrievePossibleAnswersByQuestionId(Integer.valueOf(request.getQuestionId()));
			
			int fullMatchRatio = xmlConfigurationManager.getAppConfigurationBean().getAnswerFullMatchRatio();
			int partMatchRatio = xmlConfigurationManager.getAppConfigurationBean().getAnswerPartMatchRatio();

			AnswerVO matchingAnswer = Utility.getMatchingAnswerV2(request.getCapturedAnswer(), possibleAnswers, null, null, minimumMatchingRatio, fullMatchRatio, partMatchRatio);
			
			List<String> submittedPlayerAnswers = request.getSubmittedPlayerAnswers();
			if(matchingAnswer != null && request.getSubmittedPlayerAnswers() != null)
			{
				for(String submittedAnswer : request.getSubmittedPlayerAnswers())
				{
					if(submittedAnswer.equalsIgnoreCase(matchingAnswer.getAnswerEn()))
					{
						matchingAnswer = null;
						break;
					}
				}
			}
			
			result.setCorrect(matchingAnswer != null);
			result.setAnswerElementId(request.getAnswerElementId());
			if(matchingAnswer != null)
			{
				result.setMatchingAnswer(matchingAnswer.getAnswerEn());

				// End the question if all possible answers are provided
				List<AnswerVO> allSubmittedAnswers = new ArrayList<AnswerVO>();
				allSubmittedAnswers.add(matchingAnswer);
				
				int numOfPossibleAnswers = possibleAnswers.size();
				if(submittedPlayerAnswers == null)
				{
					submittedPlayerAnswers = new ArrayList<String>();
				}
				submittedPlayerAnswers.add(matchingAnswer.getAnswerEn());
				
				result.setAllAnswersProvided(submittedPlayerAnswers.size() == numOfPossibleAnswers);
			}
			
			result.setStatus(1);
		}
		catch(Exception ex)
		{
			result.setErrorMessage("Error occured while validating the answer");
			result.setStatus(0);
			ex.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value = "/strike", method = RequestMethod.POST, consumes="application/json")
	public void strike(@RequestBody StrikeRequest request) {

		GameRoom gameRoom = gameRoomManager.getGameRoomById(request.getRoomId());
		if(gameRoom != null)
		{
			ChallengeManager challengeManager = null;
			if(request.getChallengeCategory() != null)
			{
				switch (request.getChallengeCategory()) {
					case 1:
						challengeManager = new WhatDoYouKnowChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
					case 2:
						challengeManager = new AuctionChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
					case 3:
						challengeManager = new BellChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
					case 4:
						challengeManager = new WhoAmIChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
					default:
						challengeManager = new WhatDoYouKnowChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
						break;
				}
			}
			else
			{
				challengeManager = new WhatDoYouKnowChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
			}
			
			challengeManager.strike(request.getRoomId(), request.getSubmittedPlayer(), request.getQuestionId(), request.getCurrentQuestionIdx(), gameRoom);
		}
	}
	
	@RequestMapping(value = "/skipBell", method = RequestMethod.POST, consumes="application/json")
	public void skipBell(@RequestBody SkipBellRequest request) {
		
		// notify players that no one answered the Bell question
		messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+ "/bellNoAnswer", "");
		
		BellChallengeManager challengeManager = new BellChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
		challengeManager.skipBell(request.getRoomId(), request.getQuestionId(), request.getCurrentQuestionIndex());
	}
	
	@RequestMapping(value = "/whoAmITimerComplete", method = RequestMethod.POST, consumes="application/json")
	public void whoAmITimerComplete(@RequestBody SkipBellRequest request) {
		
		// notify players that no one answered the Bell question
		messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+ "/whoAmINoWinner", "");
		
		WhoAmIChallengeManager challengeManager = new WhoAmIChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
		challengeManager.whoAmITimerComplete(request.getRoomId(), request.getQuestionId(), request.getCurrentQuestionIndex());
	}
	
	@RequestMapping(value = "/auctionTimerComplete", method = RequestMethod.POST, consumes="application/json")
	public void auctionTimerComplete(@RequestBody AuctionTimerCompleteRequest request) {
		
		AuctionChallengeManager challengeManager = new AuctionChallengeManager(env, gameRoomManager, gameTimerManager, xmlConfigurationManager, messagingTemplate, executor);
		challengeManager.auctionTimerComplete(request.getRoomId(), String.valueOf(request.getQuestionId()), request.getPlayerId(), request.getCurrentQuestionIndex());
	}
	
	@RequestMapping(value = "/singleGameReport", method = RequestMethod.POST, consumes="application/json")
	public BaseResult saveSingleGameReport(@RequestBody SingleGameReport request) {

		BaseResult result = new BaseResult();
		gameRoomManager.persistSingleGame(request);
		result.setStatus(1);
		
		return result;
	}
	
	@RequestMapping(value = "/{roomId}/join", method = RequestMethod.POST, consumes="application/json")
	public JoinGameResult joinGame(@PathVariable String roomId, @RequestParam String username, @RequestParam(name="playerId",required=false) String playerId) {
		
		JoinGameResult result = new JoinGameResult();
		
		if(joinRoomRequests.get(roomId) != null)
		{
			// duplicate requests detected
			result.setStatus(0);
			result.setErrorMessage("Duplicate Join Room requests detected!");
		}
		else
		{
			joinRoomRequests.put(roomId, System.currentTimeMillis());
			
			WaitingGameVO waitingGame = gameRoomManager.getWaitingGameById(roomId);
			if(waitingGame != null)
			{
				if(playerId != null && waitingGame.getPlayer().getPlayerId() != null && playerId.equalsIgnoreCase(waitingGame.getPlayer().getPlayerId()))
				{
					result.setStatus(0);
					result.setErrorMessage("Can't play with yourself!");
				}
				else
				{
					String player1Session = waitingGame.getSessionId();

					// An available game room is found, add the player to the room as player2
					Player player = new Player(playerId, username);
		        	GameRoom activeRoom = gameRoomManager.createActiveGameRoom(waitingGame, player);
		        	
		        	// Temporarily add the waiting session as active session. This is to handle the scenario where player1 was in the background and he didn't join
		        	webSocketManager.addActiveSession(player1Session, activeRoom.getPlayer1().getPlayerId());
		        	
		        	GameStartEvent gameStartEventP1 = new GameStartEvent();
		        	gameStartEventP1.setRoomId(activeRoom.getRoomId());
		        	gameStartEventP1.setPlayerId(activeRoom.getPlayer1().getPlayerId());
		        	
		        	GameStartEvent gameStartEventP2 = new GameStartEvent();
		        	gameStartEventP2.setRoomId(activeRoom.getRoomId());
		        	gameStartEventP2.setPlayerId(activeRoom.getPlayer2().getPlayerId());
		        	
		            // Notify both players in the room about the game start
		        	messagingTemplate.convertAndSend("/topic/game/start/" + activeRoom.getPlayer1().getUsername(), gameStartEventP1);
		            messagingTemplate.convertAndSend("/topic/game/start/" + activeRoom.getPlayer2().getUsername(), gameStartEventP2);
		            
		            String player1Name = activeRoom.getPlayer1().getUsername();
		        	if(player1Name.contains(" "))
		        	{
		        		player1Name = player1Name.replaceAll("\\s+", "_");
		        		
		        		// workaround for Android
		                messagingTemplate.convertAndSend("/topic/game/start/" + player1Name, gameStartEventP1);
		                System.out.println("Notification for room "+ activeRoom.getRoomId() +" has been sent to: " + player1Name);
		        	}
		            
		            // Send push notification to player1 who initiated the game
		            if(activeRoom.getPlayer1().getDeviceToken() != null)
		            {
		            	gameRoomManager.sendPushNotificationToWaitingPlayer(activeRoom.getPlayer1(), activeRoom.getRoomId());
		            }
		            
		            // Start answer timer
		            gameTimerManager.refreshGameTimer(activeRoom.getRoomId(), activeRoom.getPlayer1().getPlayerId(), String.valueOf(activeRoom.getQuestions().get(0).getId()), 1, null);
		            
		            result.setPlayerId(activeRoom.getPlayer2().getPlayerId());
		            result.setStatus(1);
				}
			}
			else
			{
				result.setStatus(0);
				result.setErrorMessage("Game room is invalid!");
			}
		}
		
		return result;
	}
	
	@RequestMapping(value = "/cancelGame", method = RequestMethod.POST, consumes="application/json")
	public BaseResult cancelGame(@RequestParam(value = "roomId") String roomId, @RequestParam(name="submittedPlayerId",required=false) String submittedPlayerId) {
	
		System.out.println("Cancel game request has been received for room: " + roomId + " and player: " + submittedPlayerId);
		
		BaseResult result = new BaseResult();
		if(StringUtils.isNotEmpty(roomId))
		{
			WaitingGameVO waitingGame = gameRoomManager.getWaitingGameById(roomId);
			if(waitingGame != null)
			{
				gameRoomManager.cancelWaitingGame(roomId);
			}
			else
			{
				GameRoom gameRoom = gameRoomManager.getGameRoomById(roomId);
				if(gameRoom != null)
				{
					// Save the game details in the database
					GameReport gameReport = gameRoomManager.getGameReport(gameRoom.getRoomId());
					if(submittedPlayerId != null)
					{
						// Set the remaining questions as pass for the opponent player in case at least 2 questions are already answered
						Map<String, String> questionsResult = gameReport.getQuestionsResult();
						List<QuestionResult> gameQuestions = gameRoom.getQuestions();
						
						if(questionsResult.keySet().size() >= 2 && questionsResult.keySet().size() < gameQuestions.size())
						{
							String opponentPlayerId = submittedPlayerId.equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
							for(QuestionResult question : gameQuestions)
							{
								if(questionsResult.get(String.valueOf(question.getId())) == null)
								{
									// Question is not answered, so mark it as successfull for the opponent player
									gameRoomManager.addPlayerScoreToGameReport(roomId, opponentPlayerId, String.valueOf(question.getId()));
								}
							}
							gameReport = gameRoomManager.getGameReport(gameRoom.getRoomId());
						}
					}
					
					GameVO savedGame = gameRoomManager.persistGameRoom(gameRoom, gameReport);
					gameRoomManager.cancelActiveGame(String.valueOf(savedGame.getId()), submittedPlayerId);
					
					gameRoomManager.flushGame(gameRoom);
					messagingTemplate.convertAndSend("/topic/game/"+gameRoom.getRoomId()+"/end", "");
				}
			}
		}
		
		result.setStatus(1);
		return result;
	}
	
	@RequestMapping(value = "/onlineGame/{roomId}", method = RequestMethod.GET)
	public GameResult onlineGame(@PathVariable String roomId) {
		
		GameResult result = new GameResult();
		GameRoom gameRoom = null;
		try
		{
			if(StringUtils.isEmpty(roomId))
			{
				throw new Exception();
			}
			
			gameRoom = gameRoomManager.getGameRoomById(roomId);
			if(gameRoom != null)
			{
				result.setChallenges(gameRoom.getChallenges());
				result.setQuestions(gameRoom.getQuestions());
				result.setStatus(1);
				result.setRoomId(roomId);
				result.setPlayer1(gameRoom.getPlayer1());
				result.setPlayer2(gameRoom.getPlayer2());
				result.setCurrentTurn(gameRoom.getPlayer1().getPlayerId());
			}
			else
			{
				result.setErrorMessage("Could not find the specified game room");
				result.setStatus(0);
			}
		}
		catch(Exception ex)
		{
			result.setErrorMessage("Error while compiling the game questions");
			result.setStatus(0);
			ex.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping(value = "/singleGame", method = RequestMethod.GET)
	public GameResult singleGame(@RequestParam(value = "username") String username, @RequestParam(name="playerId",required=false) String playerId) {
		
		GameResult result = new GameResult();
		GameRoom gameRoom = null;
		try
		{
			if(StringUtils.isEmpty(username))
			{
				throw new Exception();
			}
			
			gameRoom = gameRoomManager.createSingleGameRoom(username, playerId);
			if(gameRoom != null)
			{
				result.setQuestions(gameRoom.getQuestions());
				result.setStatus(1);
				result.setRoomId(gameRoom.getRoomId());
				result.setPlayer1(gameRoom.getPlayer1());
				result.setPlayer2(gameRoom.getPlayer2());
			}
			else
			{
				result.setErrorMessage("Could not generate the game");
				result.setStatus(0);
			}
		}
		catch(Exception ex)
		{
			result.setErrorMessage("Error while compiling the game questions");
			result.setStatus(0);
			ex.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping(value = "/waitingRooms", method = RequestMethod.GET)
	public WaitingRoomResult waitingRooms() {
		
		WaitingRoomResult result = new WaitingRoomResult();
		
		List<WaitingGameVO> waitingGameRooms = gameRoomManager.getWaitingGameRooms();
		List<WaitingRoomResultBean> waitingRooms = new ArrayList<WaitingRoomResultBean>();
		for(WaitingGameVO room : waitingGameRooms)
		{
			WaitingRoomResultBean waitingRoomResult = new WaitingRoomResultBean();
			waitingRoomResult.setRoomId(String.valueOf(room.getId()));
			
			String playerUsername = room.getAnonymousPlayer() != null ? room.getAnonymousPlayer() : (room.getPlayer().getFirstName() + " " + room.getPlayer().getLastName());
			Player hostingPlayer = new Player(room.getPlayer().getPlayerId(), playerUsername);
			
			if(room.getAnonymousPlayer() == null)
			{
				hostingPlayer.setTotalScore(room.getPlayer().getTotalScore());
			}
			waitingRoomResult.setHostingPlayer(hostingPlayer);
			
			waitingRooms.add(waitingRoomResult);
		}
		
		result.setWaitingRooms(waitingRooms);
		
		return result;
	}
	
	/*private static boolean isAnswerMatching(List<String> answers, String capturedAnswer, List<String> opponentPlayerAnswers, List<String> submittedPlayerAnswers, int minimumMatchingRatio)
	{
		boolean answerMatch = false;
		String matchingAnswer = null;
		int highestMatchingRatio = 0;
		for(String answer : answers)
		{
			if((opponentPlayerAnswers != null && opponentPlayerAnswers.contains(answer)) || (submittedPlayerAnswers != null && submittedPlayerAnswers.contains(answer)))
			{
				continue;
			}
			else
			{
				int matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), answer.toLowerCase());
				if(matchingRatio >= minimumMatchingRatio)
				{
					if(highestMatchingRatio < matchingRatio)
					{
						highestMatchingRatio = matchingRatio;
						matchingAnswer = answer;
					}
					answerMatch = true;
					//gameRoomManager.addSuccessfulAnswerToGameReport(request.getRoomId(), request.getQuestionId(), matchingAnswer, request.getSubmittedPlayerId());
				}
			}
		}
		
		System.out.println("The matching answer is: " + matchingAnswer);
		return answerMatch;
	}*/
	
	/*public static void main(String[] args) {
		
		
		List<String> answers = new ArrayList<String>();
		answers.add("Lionel Messi");
		answers.add("Cristiano Ronaldo");
		answers.add("Pele");
		answers.add("Didier Drogba");
		answers.add("Al Ahly");
		answers.add("Manchester City");
		answers.add("Manchester United");
		answers.add("Sun Di Jong");
		answers.add("De Yong");
		
		String capturedAnswer = "Yong";
		
		boolean isAnswerMatch = isAnswerMatching(answers, capturedAnswer, null, null, 53);
		System.out.println(isAnswerMatch ? "Correct Answer" : "Incorrect Answer");
	}*/
	
	private String transliterateAnswer(String capturedAnswer)
	{
		String LANGUAGE_COMBINATION_NO_ACCENTS = "Any-Eng; nfd; [:nonspacing mark:] remove; nfc";
		Transliterator transliterator = Transliterator.getInstance(LANGUAGE_COMBINATION_NO_ACCENTS);
		
		return transliterator.transliterate(capturedAnswer);
	}
	
	private AnswerVO getMatchingAnswer(String capturedAnswer, List<AnswerVO> possibleAnswers, List<AnswerVO> submittedPlayerAnswers, List<AnswerVO> opponentPlayerAnswers, int minimumMatchingRatio)
	{
		AnswerVO matchingAnswer = null;
		
		int highestMatchingRatio = 0;
		boolean isNumericAnswer = StringUtils.isNumeric(capturedAnswer);
		boolean isArabicAnswer = Utility.isArabicText(capturedAnswer);
		
		for(AnswerVO answerVO : possibleAnswers)
		{
			String possibleAnswer = isArabicAnswer ? answerVO.getAnswerAr() : StringUtils.stripAccents(answerVO.getAnswerEn());
			
			if(isNumericAnswer)
			{
				if(capturedAnswer.equals(possibleAnswer))
				{
					matchingAnswer = answerVO;
					break;
				}
			}
			else
			{
				int matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), possibleAnswer.toLowerCase());
				if(matchingRatio >= minimumMatchingRatio)
				{
					if(highestMatchingRatio < matchingRatio)
					{
						highestMatchingRatio = matchingRatio;
						matchingAnswer = answerVO;
					}
				}
			}
		}
		
		// Check if the matching answer is already answered by the submitted player or the opponent player
		if(matchingAnswer != null)
		{
			if(opponentPlayerAnswers != null)
			{
				for(AnswerVO oppoAnswerVO : opponentPlayerAnswers)
				{
					if(matchingAnswer.getId() == oppoAnswerVO.getId())
					{
						matchingAnswer = null;
						break;
					}
				}
			}
			
			if(matchingAnswer != null && submittedPlayerAnswers != null)
			{
				for(AnswerVO subAnswerVO : submittedPlayerAnswers)
				{
					if(matchingAnswer.getId() == subAnswerVO.getId())
					{
						matchingAnswer = null;
						break;
					}
				}
			}
		}
		
		return matchingAnswer;
	}
	
	
	
	public static void main(String[] args) {
		
		String capturedAnswer = "ايتو";
		String possibleAnswer = "سامويل ايتو";
		
		/*String possibleAnswer = "Miroslav Klose";
		String capturedAnswer = "Mirslev klusa";*/
		
		boolean isNumericAnswer = StringUtils.isNumeric(capturedAnswer);
		boolean isArabicAnswer = Utility.isArabicText(capturedAnswer);
		
		if(isNumericAnswer)
		{
			if(capturedAnswer.equals(possibleAnswer))
			{
				System.out.println("Answer is matching");
			}
		}
		else
		{
			int fullMatchRatio = 80;
			int partMatchRatio = 80;
			int highestMatchingRatio = 0;
		
			capturedAnswer = capturedAnswer.replaceAll("أ", "ا");
			capturedAnswer = capturedAnswer.replaceAll("إ", "ا");
			capturedAnswer = capturedAnswer.replaceAll("آ", "ا");
			possibleAnswer = possibleAnswer.replaceAll("أ", "ا");
			possibleAnswer = possibleAnswer.replaceAll("إ", "ا");
			possibleAnswer = possibleAnswer.replaceAll("آ", "ا");
			
			if(possibleAnswer.startsWith("ال"))
			{
				possibleAnswer = possibleAnswer.replaceFirst("ال", "");
			}
			
			if(capturedAnswer.startsWith("ال"))
			{
				capturedAnswer = capturedAnswer.replaceFirst("ال", "");
			}
			
			int matchingRatio = 0;
			String capturedAnswerArr[] = capturedAnswer.split("\\s+");
			String storedAnswerArr[] = possibleAnswer.split("\\s+");
			
			if(capturedAnswerArr.length > 1)
			{
				// 1st step match (Full name)
				if((capturedAnswerArr.length >= storedAnswerArr.length))
				{
					matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), possibleAnswer.toLowerCase());
					if(matchingRatio >= fullMatchRatio)
					{
						if(highestMatchingRatio < matchingRatio)
						{
							System.out.println("Full name match is found with ratio: " + matchingRatio);
							highestMatchingRatio = matchingRatio;
						}
					}
					else
					{
						System.out.println("No match found - ratio = " + matchingRatio);
					}
				}
				else
				{
					int capturedAnsLength = capturedAnswerArr.length;
					// If the stored answer is larger than the captured answer, then we compare the tokens of the captured answer by same tokens of stored answe
					for(int i=0; i<storedAnswerArr.length; i++)
					{
						String token = "";
						for(int j=0; j < capturedAnsLength; j++)
						{
							if((i+j) < storedAnswerArr.length)
							{
								if(StringUtils.isNoneEmpty(token))
								{
									token += " ";
								}
								token += storedAnswerArr[i+j];
							}
						}
						
						matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), token.toLowerCase());
						if(matchingRatio >= fullMatchRatio)
						{
							if(highestMatchingRatio < matchingRatio)
							{
								System.out.println("Full name match is found with ratio: " + matchingRatio);
								highestMatchingRatio = matchingRatio;
							}
						}
						
					}
				}
			}
			else
			{
				int capturedAnswerLength = capturedAnswer.length();
				if(storedAnswerArr.length <= 2)
				{
					// if the captured answer is one token and the possible answer is 2 tokens, then the captured answer should be minimum 3 characters
					if(capturedAnswerLength >= 3)
					{
						for(String storedAnswer : storedAnswerArr)
						{
							matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), storedAnswer.toLowerCase());
							
							if(matchingRatio >= partMatchRatio)
							{
								if(highestMatchingRatio < matchingRatio)
								{
									System.out.println("Part name match is found with ratio: " + matchingRatio);
									highestMatchingRatio = matchingRatio;
								}
							}
						}
					}
				}
				else
				{
					// if the captured answer is one token and the possible answer is more than 2 tokens, then the captured answer length should be minimum 50% of the total length of the stored answer
					float lengthPercentage = (float)capturedAnswer.length() / (float)(possibleAnswer.length());
					if(lengthPercentage >= 0.5)
					{
						for(String storedAnswer : storedAnswerArr)
						{
							matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), storedAnswer.toLowerCase());
							
							if(matchingRatio >= partMatchRatio)
							{
								if(highestMatchingRatio < matchingRatio)
								{
									System.out.println("Part name match is found with ratio: " + matchingRatio);
									highestMatchingRatio = matchingRatio;
								}
							}
						}
					}
				}
			}
			
			
			
			
			/*int nbCharactersOfPossibleAnswer = possibleAnswer.length();
			
			String possibleAnswerArr[] = possibleAnswer.split("\\s+");
			
			// 2nd step match (First name)
			String firstPart = capturedAnswerArr[0];
			if(((float)firstPart.length() / (float)nbCharactersOfPossibleAnswer) >= 0.5)
			{
				// The length of the first name should cover minimum 50% of the complete answer, otherwise it's considered as wrong answer e.g De Jong
				possibleMatchingRatio = 95;
				matchingRatio = FuzzySearch.ratio(firstPart.toLowerCase(), possibleAnswerArr[0].toLowerCase());
				System.out.println("First name match = " + matchingRatio);
				
				if(matchingRatio >= possibleMatchingRatio)
				{
					System.out.println("Answer is matching");
					return;
				}
			}
			
			// 3rd step match (Last name)
			if((firstPart.length() / nbCharactersOfPossibleAnswer) >= 0.5)
			{
				possibleMatchingRatio = 95;
				String possibleAnswerMatch = "";
				if(possibleAnswerArr.length > 1)
				{
					for(int i=1; i<possibleAnswerArr.length; i++)
					{
						possibleAnswerMatch = possibleAnswerMatch + possibleAnswerArr[i];
					}
					
					matchingRatio = FuzzySearch.ratio(firstPart.toLowerCase(), possibleAnswerMatch.toLowerCase());
					System.out.println("Last name match = " + matchingRatio);
					
					if(matchingRatio >= possibleMatchingRatio)
					{
						System.out.println("Answer is matching");
						return;
					}
				}
				else
				{
					possibleAnswerMatch = possibleAnswerArr[0];
					matchingRatio = FuzzySearch.ratio(firstPart.toLowerCase(), possibleAnswerMatch.toLowerCase());
					System.out.println("Last name match = " + matchingRatio);
					
					if(matchingRatio >= possibleMatchingRatio)
					{
						System.out.println("Answer is matching");
						return;
					}
				}
			 }*/
		}
		
	}
	
	
	public static boolean matchName(String userAnswer, String storedAnswer) {
        // Full Name Fuzzy Matching
        int fullMatchResultScore = FuzzySearch.ratio(userAnswer, storedAnswer);
        if (fullMatchResultScore >= 85) {
            System.out.println("Result match with score: " + fullMatchResultScore);
        	return true;
        }

        // Partial Name Matching - Flexible Threshold
        String[] userParts = userAnswer.split("\\s+");
        String[] storedAnswerParts = storedAnswer.split("\\s+");
        Map<String, Integer> matchedParts = new HashMap<String, Integer>();

        for (int i=0; i<userParts.length; i++) {
        	String part = userParts[i];
            int threshold = (part.length() < 4) ? 80 : 90;

            if(i < storedAnswerParts.length)
            {
            	int partMatchResultScore = FuzzySearch.ratio(part, storedAnswerParts[i]);
                if (partMatchResultScore >= threshold) {
                    matchedParts.put(part, matchedParts.getOrDefault(part, 0) + 1);
                }
            }
        }

        if (!matchedParts.isEmpty()) {
            // Choose the part that appears most frequently.
            String mostFrequentPart = matchedParts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey();

            if (matchedParts.get(mostFrequentPart) == 1) {
                // If there's a tie, choose the longest matched part.
                for (String part : matchedParts.keySet()) {
                    if (part.length() > mostFrequentPart.length()) {
                        mostFrequentPart = part;
                    }
                }
            }
            
            System.out.println("Most frequent part: " + mostFrequentPart + " match with score: " + matchedParts.get(mostFrequentPart));
            return true;
        }

        // Handle additional scenarios (nicknames, character count rules, special characters, etc.)
        // Implement additional rules as needed.

        // If none of the above conditions are met, return null or handle the case where there's no match.
        System.out.println("No match found");
        return false;
    }
	
	@Scheduled(fixedRate = 3000) // Run every 3 sec (adjust as needed)
    public void cleanValidateAnswerRequests() {
		
		long VALIDATE_REQUEST_THRESHOLD = 2000;
		long currentTime = System.currentTimeMillis();
		
		List<String> toBeRemovedLst = new ArrayList<String>();
		for(Entry<String, Long> entry : validateAnswerRequests.entrySet())
		{
			String validateAnswerRequest = entry.getKey();
			Long requestTime = entry.getValue();
			
			if (requestTime != null && currentTime - requestTime > VALIDATE_REQUEST_THRESHOLD) {
				toBeRemovedLst.add(validateAnswerRequest);
			}
		}
		
		for(String toBeRemovedRequest : toBeRemovedLst)
		{
			validateAnswerRequests.remove(toBeRemovedRequest);
		}
	}
	
	@RequestMapping(value = "/questionAnswers", method = RequestMethod.GET)
	public QuestionAnswersResult retrieveQuestionAnswers(@RequestParam(value = "questionId") int questionId) {
		
		QuestionAnswersResult result = new QuestionAnswersResult();
		
		List<AnswerVO> answers = gameRoomManager.retrievePossibleAnswersByQuestionId(questionId);
		result.setAnswers(answers);
		result.setQuestionId(questionId);
		
		return result;
	}
	
	@Scheduled(fixedRate = 3000) // Run every 3 sec (adjust as needed)
    public void cleanJoinRoomRequests() {
		
		long JOIN_ROOM_REQUEST_THRESHOLD = 2000;
		long currentTime = System.currentTimeMillis();
		
		List<String> toBeRemovedLst = new ArrayList<String>();
		for(Entry<String, Long> entry : joinRoomRequests.entrySet())
		{
			String joinRoomRequest = entry.getKey();
			Long requestTime = entry.getValue();
			
			if (requestTime != null && currentTime - requestTime > JOIN_ROOM_REQUEST_THRESHOLD) {
				toBeRemovedLst.add(joinRoomRequest);
			}
		}
		
		for(String toBeRemovedRequest : toBeRemovedLst)
		{
			joinRoomRequests.remove(toBeRemovedRequest);
		}
	}

}
