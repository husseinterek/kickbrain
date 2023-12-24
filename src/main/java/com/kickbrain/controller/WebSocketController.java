package com.kickbrain.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.kickbrain.beans.ChallengeBean;
import com.kickbrain.beans.ChatRequest;
import com.kickbrain.beans.EndBidEvent;
import com.kickbrain.beans.EndBidRequest;
import com.kickbrain.beans.GameReport;
import com.kickbrain.beans.GameReportResult;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.Player;
import com.kickbrain.beans.QuestionResult;
import com.kickbrain.beans.RingBellRequest;
import com.kickbrain.beans.SkipQuestionEvent;
import com.kickbrain.beans.SkipQuestionRequest;
import com.kickbrain.beans.SubmitBidEvent;
import com.kickbrain.beans.SubmitBidRequest;
import com.kickbrain.beans.WaitingRoom;
import com.kickbrain.beans.configuration.ChallengeConfig;
import com.kickbrain.beans.configuration.GameConfig;
import com.kickbrain.configuration.XMLConfigurationManager;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.GameTimerManager;
import com.kickbrain.manager.WebSocketManager;

@Controller
public class WebSocketController {

	private GameRoomManager gameRoomManager;
	private SimpMessagingTemplate messagingTemplate;
    private WebSocketManager websocketManager;
    private GameTimerManager gameTimerManager;
    private XMLConfigurationManager xmlConfigurationManager;
    
    private Map<String, Long> ringBellRequests = new ConcurrentHashMap<String, Long>();
	
    @Autowired
    public WebSocketController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate, WebSocketManager websocketManager, GameTimerManager gameTimerManager, XMLConfigurationManager xmlConfigurationManager) {
        this.gameRoomManager = gameRoomManager;
        this.messagingTemplate = messagingTemplate;
        this.websocketManager = websocketManager;
        this.gameTimerManager = gameTimerManager;
        this.xmlConfigurationManager = xmlConfigurationManager;
    }
    
    @MessageMapping("/startGame")
    public void startGame(Player player, SimpMessageHeaderAccessor headerAccessor) {

        String playerId = null;
        try
        {
        	String sessionId = headerAccessor.getSessionId();
        	
            GameRoom newGameRoom = gameRoomManager.createWaitingGameRoom(player, sessionId);
            playerId = newGameRoom.getPlayer1().getPlayerId();
            
            // Add the sessionId and playerId to the WebSocketSessionManager
            websocketManager.addWaitingSession(sessionId, playerId);
            
            System.out.println("A new waiting game has been created with the following id: " + newGameRoom.getRoomId() + " for player: " + newGameRoom.getPlayer1().getUsername());
            // Wait for the second player to join the room
            waitForSecondPlayer(newGameRoom);
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
    }
    
    @MessageMapping("/game/skipQuestion")
    public void skipQuestion(SkipQuestionRequest request) {
    	
    	GameRoom gameRoom = gameRoomManager.getGameRoomById(request.getRoomId());
    	
    	if(gameRoom != null)
    	{
    		// add point to the opponent
    		if(gameRoom.getPlayer1().getPlayerId().equals(request.getSubmittedPlayerId()))
    		{
    			gameRoomManager.addPlayerScoreToGameReport(request.getRoomId(), gameRoom.getPlayer2().getPlayerId(), request.getQuestionId());
    		}
    		else
    		{
    			gameRoomManager.addPlayerScoreToGameReport(request.getRoomId(), gameRoom.getPlayer1().getPlayerId(), request.getQuestionId());
    		}
    		
    		boolean isGameComplete = false;
    		ChallengeBean returnedChallenge = new ChallengeBean();
    		if(request.getChallengeCategory() != null)
    		{
    			Map<Integer, ChallengeBean> challengesMap = gameRoom.getChallengesMap();
    			ChallengeBean currentChallenge = challengesMap.get(request.getChallengeCategory());
    			
				List<QuestionResult> challengeQuestions = currentChallenge.getQuestions();
				if(request.getCurrentQuestionIndex() == challengeQuestions.size())
				{
					// Challenge is completed, move on to the next challenge in the game
					returnedChallenge = challengesMap.get((request.getChallengeCategory() + 1));
					isGameComplete = returnedChallenge == null;
				}
				else
				{
					returnedChallenge = currentChallenge;
				}
    		}
    		else
    		{
    			returnedChallenge = gameRoom.getChallenges().get(0);
    			isGameComplete = request.getCurrentQuestionIndex() == returnedChallenge.getQuestions().size();
    		}
    		
    		if(isGameComplete)
    		{
    			// No more challenges in the list, so complete the game
    			GameReportResult gameReportResult = gameRoomManager.generateGameReportResult(request.getRoomId());
    			
    			gameTimerManager.removeGameTimer(request.getRoomId());
    			messagingTemplate.convertAndSend("/topic/game/" + request.getRoomId() + "/complete", gameReportResult);
    			
    			// Save the game details in the database
    			GameReport gameReport = gameRoomManager.getGameReport(gameRoom.getRoomId());
    			gameRoomManager.persistGameRoom(gameRoom, gameReport);
    			
    			gameRoomManager.flushGame(gameRoom);
    		}
    		else
    		{
    			Map<String, Integer> playersScores = gameRoomManager.getPlayersScoresPerGame(request.getRoomId());
    			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/updateScore", playersScores);
    			
    			SkipQuestionEvent skipQuestionEvent = new SkipQuestionEvent();
    			QuestionResult nextQuestion = null;
    			boolean delayNewQuestion = false;
    			if(request.getChallengeCategory() != null)
    			{
    				if(returnedChallenge.getCategory() != request.getChallengeCategory())
    				{
    					// Challenge has been changed
    					nextQuestion = returnedChallenge.getQuestions().get(0);
    	    			skipQuestionEvent.setNextQuestionIndex(1);
    	    			
    	    			boolean isLastQuestion = false;
    					if(returnedChallenge.getQuestions().size() == 1 && (gameRoom.getChallengesMap().get((request.getChallengeCategory() + 1)) == null))
    					{
    						// If it is the last question in the current challenge and there are no remaining challenges
    						isLastQuestion = true;
    					}
    					skipQuestionEvent.setLastQuestion(isLastQuestion);
    	    			
    	    			delayNewQuestion = true;
    				}
    				else
    				{
    					// Get next index for the same challenge
    					skipQuestionEvent.setNextQuestionIndex(request.getCurrentQuestionIndex() + 1);
    					nextQuestion = returnedChallenge.getQuestions().get(request.getCurrentQuestionIndex());
    					
    					boolean isLastQuestion = false;
    					if((returnedChallenge.getQuestions().size() == (request.getCurrentQuestionIndex() + 1)) && (gameRoom.getChallengesMap().get((request.getChallengeCategory() + 1)) == null))
    					{
    						// If it is the last question in the current challenge and there are no remaining challenges
    						isLastQuestion = true;
    					}
    					skipQuestionEvent.setLastQuestion(isLastQuestion);
    				}
    			}
    			else
    			{
    				skipQuestionEvent.setNextQuestionIndex(request.getCurrentQuestionIndex() + 1);
    				nextQuestion = returnedChallenge.getQuestions().get(request.getCurrentQuestionIndex());
    				skipQuestionEvent.setLastQuestion(returnedChallenge.getQuestions().size() == (request.getCurrentQuestionIndex() + 1));
    			}
    			
    			skipQuestionEvent.setChallengeCategory(returnedChallenge.getCategory());
    			
    			// Notify the other player that the opponent has skipped the question
    			String opponentPlayerId = request.getSubmittedPlayerId().equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
    			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+ "/" + opponentPlayerId + "/skipQuestion", "");

    			if(delayNewQuestion)
    			{
    				messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/nextChallengePopup", returnedChallenge.getCategory());
    				// delay the trigger of the new question by 5 seconds
    				int delayInSecs = xmlConfigurationManager.getAppConfigurationBean().getDelayNextChallenge();
    				try
    				{
    					Thread.sleep(delayInSecs * 1000);
    				}
    				catch (Exception e) {
    					e.printStackTrace();
					}
    			}
    			
    			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/newQuestion", skipQuestionEvent);
    			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/switchTurn", request.getSubmittedPlayerId());
    			
    			String currentTurn = request.getSubmittedPlayerId();
    			
    			//QuestionResult question = gameRoom.getQuestions().get(request.getCurrentQuestionIndex());
    			int nextQuestionId = nextQuestion.getId();
    			int challengeCategory = nextQuestion.getChallengeCategory();
    			
    			Integer nonAnswerTimer = null;
    			if(challengeCategory == 2)
    			{
        			GameConfig gameConfig = xmlConfigurationManager.getAppConfigurationBean().getOnlineGameConfig();
    				List<ChallengeConfig> challenges = gameConfig.getChallenges();
    				for(ChallengeConfig challenge : challenges)
    				{
    					if(challenge.getCategory() == challengeCategory)
    					{
    						nonAnswerTimer = challenge.getBidTimer();
    					}
    				}
    			}
    			if(challengeCategory == 3)
    			{
        			GameConfig gameConfig = xmlConfigurationManager.getAppConfigurationBean().getOnlineGameConfig();
    				List<ChallengeConfig> challenges = gameConfig.getChallenges();
    				for(ChallengeConfig challenge : challenges)
    				{
    					if(challenge.getCategory() == challengeCategory)
    					{
    						nonAnswerTimer = challenge.getBellTimer();
    					}
    				}
    			}
    			
    			gameTimerManager.refreshGameTimer(request.getRoomId(), currentTurn, String.valueOf(nextQuestionId), challengeCategory, nonAnswerTimer);
    		}
    	}
    }
    
    @MessageMapping("/game/chat")
    public void chat(ChatRequest request) {
    	
    	GameRoom gameRoom = gameRoomManager.getGameRoomById(String.valueOf(request.getRoomId()));
    	String opponentPlayerId = request.getPlayerId().equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
		
    	messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/"+opponentPlayerId+"/chat", request.getMessage());
    }
    
    @MessageMapping("/game/bell/ring")
    public void ringBell(RingBellRequest request) {
    	
    	String ringBellRequest = request.getRoomId() + "_" + request.getQuestionId();
		if(ringBellRequests.get(ringBellRequest) == null)
		{
			ringBellRequests.put(ringBellRequest, System.currentTimeMillis());
			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/"+request.getQuestionId()+"/ringBell", request.getPlayerId());
	    	gameTimerManager.refreshGameTimer(String.valueOf(request.getRoomId()), request.getPlayerId(), String.valueOf(request.getQuestionId()), 3, null);
		}
    }
    
    @MessageMapping("/game/auction/end")
    public void endBid(EndBidRequest request) {
    	try
    	{
    		GameRoom gameRoom = gameRoomManager.getGameRoomById(String.valueOf(request.getRoomId()));
    		if(gameRoom != null)
    		{
    			// if the winning bid parameter is equal to the max possible answers, then we consider the winning bidder as the submitting player
            	int maxPossibleAnswers = gameRoom.getAnswersByQuestion().get(request.getQuestionId()).size();
            	
            	String winningBidder = null;
            	if(request.getWinningBid() == maxPossibleAnswers)
            	{
            		winningBidder = request.getPlayerId();
            	}
            	else
            	{
            		winningBidder = request.getPlayerId().equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
            	}
            	
            	EndBidEvent endBidEvent = new EndBidEvent();
            	endBidEvent.setWinningBidder(winningBidder);
            	
            	int winningBid = request.getWinningBid() == 0 ? 1 : request.getWinningBid();
            	endBidEvent.setWinningBid(winningBid);
            	
            	gameRoomManager.addBidToGameReport(String.valueOf(request.getRoomId()), String.valueOf(request.getQuestionId()), winningBid, winningBidder);
            	
            	messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/"+request.getQuestionId()+"/endBid", endBidEvent);
        		gameTimerManager.refreshGameTimer(String.valueOf(request.getRoomId()), request.getPlayerId(), String.valueOf(request.getQuestionId()), 2, null);
    		}
    	}
    	catch(Exception ex)
    	{
    		System.out.println("An exception has been occurred while processing endBid request for player: " + request.getPlayerId() + " ,question: " + request.getQuestionId() + " and room: " + request.getRoomId());
    		ex.printStackTrace();
    	}
    }
    
    @MessageMapping("/game/auction/submit")
    public void submitBid(SubmitBidRequest request) {
    	
    	try
    	{
    		gameRoomManager.addBidToGameReport(String.valueOf(request.getRoomId()), String.valueOf(request.getQuestionId()), request.getBid(), request.getPlayerId());
        	
        	GameRoom gameRoom = gameRoomManager.getGameRoomById(String.valueOf(request.getRoomId()));
        	String currentTurn = request.getPlayerId().equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
        	
        	SubmitBidEvent submitBidEvent = new SubmitBidEvent();
        	submitBidEvent.setCurrentTurn(currentTurn);
        	submitBidEvent.setOpponentLatestBid(request.getBid());
        	
        	Map<String, Integer> playersBids = gameRoomManager.getPlayerBidsFromGameReport(String.valueOf(request.getRoomId()), String.valueOf(request.getQuestionId()));
        	Integer currentTurnLatestBid = playersBids.get(currentTurn) == null ? 0 : playersBids.get(currentTurn);
        	submitBidEvent.setCurrentTurnLatestBid(currentTurnLatestBid);
        	
        	messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/"+request.getQuestionId()+"/submitBid", submitBidEvent);
        	
        	int bidTimer = 0;
        	GameConfig gameConfig = xmlConfigurationManager.getAppConfigurationBean().getOnlineGameConfig();
    		List<ChallengeConfig> challenges = gameConfig.getChallenges();
    		for(ChallengeConfig challenge : challenges)
    		{
    			if(challenge.getCategory() == 2)
    			{
    				bidTimer = challenge.getBidTimer();
    			}
    		}
    		gameTimerManager.refreshGameTimer(String.valueOf(request.getRoomId()), request.getPlayerId(), String.valueOf(request.getQuestionId()), 2, bidTimer);
    	}
    	catch(Exception ex)
    	{
    		System.out.println("An exception has occurred while processing submitBid request for player: " + request.getPlayerId() + " ,question: " + request.getQuestionId() + " and room: " + request.getRoomId());
    		ex.printStackTrace();
    	}
    }
    
    private void waitForSecondPlayer(GameRoom gameRoom) {
    	
    	WaitingRoom waitingRoom = new WaitingRoom();
    	waitingRoom.setRoomId(gameRoom.getRoomId());
    	waitingRoom.setPlayerId(gameRoom.getPlayer1().getPlayerId());
    	
        // Send a message to the player informing them that they need to wait for another player to join
        messagingTemplate.convertAndSend("/topic/game/wait/" + gameRoom.getPlayer1().getUsername(), waitingRoom);
        System.out.println("/topic/game/wait/" + gameRoom.getPlayer1().getUsername());
        
        String playerName = gameRoom.getPlayer1().getUsername();
    	if(playerName.contains(" "))
    	{
    		playerName = playerName.replaceAll("\\s+", "_");
    		
    		// workaround for Android
    		System.out.println("Sending event to android: " + "/topic/game/wait/" + playerName);
            messagingTemplate.convertAndSend("/topic/game/wait/" + playerName, waitingRoom);
    	}
    }
    
    @MessageMapping("/ping")
    public void handlePingMessage(SimpMessageHeaderAccessor headerAccessor) {
    	String sessionId = headerAccessor.getSessionId();
    	
    	websocketManager.handlePingMessage(sessionId);
    }
    
    @MessageMapping("/waitPing")
    public void handleWaitPingMessage(SimpMessageHeaderAccessor headerAccessor) {
    	String sessionId = headerAccessor.getSessionId();
    	websocketManager.handleWaitPingMessage(sessionId);
    }
    
    @MessageMapping("/connection")
    public void handleGameConnection(SimpMessageHeaderAccessor headerAccessor, String playerId) {
    	String sessionId = headerAccessor.getSessionId();
    	
    	// Remove previous active sessions for the same player
    	List<String> activeSessions = websocketManager.getActiveSessionsByPlayer(playerId);
    	if(activeSessions != null)
    	{
    		for(String activeSession : activeSessions)
    		{
    			websocketManager.removeSession(activeSession);
    		}
    	}
    	
    	// Add the sessionId and playerId to the WebSocketSessionManager
        websocketManager.addActiveSession(sessionId, playerId);
    }
    
    @Scheduled(fixedRate = 3000) // Run every 3 sec (adjust as needed)
    public void cleanRingBellRequests() {
		
		long RING_BELL_REQUEST_THRESHOLD = 10000;
		long currentTime = System.currentTimeMillis();
		
		List<String> toBeRemovedLst = new ArrayList<String>();
		for(Entry<String, Long> entry : ringBellRequests.entrySet())
		{
			String ringBellRequest = entry.getKey();
			Long requestTime = entry.getValue();
			
			if (requestTime != null && currentTime - requestTime > RING_BELL_REQUEST_THRESHOLD) {
				toBeRemovedLst.add(ringBellRequest);
			}
		}
		
		for(String toBeRemovedRequest : toBeRemovedLst)
		{
			ringBellRequests.remove(toBeRemovedRequest);
		}
	}
    
}