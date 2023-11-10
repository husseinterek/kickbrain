package com.kickbrain.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.kickbrain.beans.ChallengeBean;
import com.kickbrain.beans.GameReport;
import com.kickbrain.beans.GameReportResult;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.Player;
import com.kickbrain.beans.QuestionResult;
import com.kickbrain.beans.RingBellRequest;
import com.kickbrain.beans.SkipQuestionEvent;
import com.kickbrain.beans.SkipQuestionRequest;
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
    	    			skipQuestionEvent.setLastQuestion(returnedChallenge.getQuestions().size() == 1);
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
    						nonAnswerTimer = challenge.getBellTimer();
    					}
    				}
    			}
    			
    			gameTimerManager.refreshGameTimer(request.getRoomId(), currentTurn, String.valueOf(nextQuestionId), challengeCategory, nonAnswerTimer);
    		}
    	}
    }
    
    @MessageMapping("/game/bell/ring")
    public void ringBell(RingBellRequest request) {
    	
    	messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/"+request.getQuestionId()+"/ringBell", request.getPlayerId());
    	gameTimerManager.refreshGameTimer(String.valueOf(request.getRoomId()), request.getPlayerId(), String.valueOf(request.getQuestionId()), 2, null);
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
    
}