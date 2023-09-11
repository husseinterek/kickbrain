package com.kickbrain.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.kickbrain.beans.GameReportResult;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.GameStartEvent;
import com.kickbrain.beans.Player;
import com.kickbrain.beans.SkipQuestionRequest;
import com.kickbrain.beans.WaitingRoom;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.GameTimerManager;
import com.kickbrain.manager.WebSocketManager;

@Controller
public class WebSocketController {

	private GameRoomManager gameRoomManager;
	private SimpMessagingTemplate messagingTemplate;
    private WebSocketManager websocketManager;
    private GameTimerManager gameTimerManager;
	
    @Autowired
    public WebSocketController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate, WebSocketManager websocketManager, GameTimerManager gameTimerManager) {
        this.gameRoomManager = gameRoomManager;
        this.messagingTemplate = messagingTemplate;
        this.websocketManager = websocketManager;
        this.gameTimerManager = gameTimerManager;
    }
    
    @MessageMapping("/startGame")
    public void startGame(Player player, SimpMessageHeaderAccessor headerAccessor) {
    	// Check if there is an available game room to join
        GameRoom availableGameRoom = gameRoomManager.findAvailableGameRoom(player.getUsername());
        String playerId = null;
        try
        {
        	if (availableGameRoom != null) {
        		// An available game room is found, add the player to the room as player2
            	GameRoom activeRoom = gameRoomManager.createActiveGameRoom(availableGameRoom, player);
            	playerId = activeRoom.getPlayer2().getPlayerId();
            	
            	// Send push notification to player1 who initiated the game
            	if(activeRoom.getPlayer1().getDeviceToken() != null)
            	{
            		gameRoomManager.sendPushNotificationToWaitingPlayer(activeRoom.getPlayer1(), activeRoom.getRoomId());
            	}
            	
                // Notify both players in the room about the game start
                notifyGameStart(availableGameRoom);
                
                // Start answer timer
                gameTimerManager.refreshGameTimer(availableGameRoom.getRoomId(), availableGameRoom.getPlayer1().getPlayerId(), String.valueOf(activeRoom.getQuestions().get(0).getId()));
            } else {
            	
            	String sessionId = headerAccessor.getSessionId();
                // No available game room found, create a new game room and add the player as player1
                GameRoom newGameRoom = gameRoomManager.createWaitingGameRoom(player, sessionId);
                playerId = newGameRoom.getPlayer1().getPlayerId();
                
                // Add the sessionId and playerId to the WebSocketSessionManager
                websocketManager.addWaitingSession(sessionId, playerId);
                
                // Wait for the second player to join the room
                waitForSecondPlayer(newGameRoom);
            }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	GameRoom gameRoom = gameRoomManager.getGameByPlayerId(playerId);
        	gameRoomManager.flushGame(gameRoom.getRoomId());
        }
    }
    
    @MessageMapping("/game/skipQuestion")
    public void skipQuestion(SkipQuestionRequest request) {
    	
    	GameRoom gameRoom = gameRoomManager.getGameRoomById(request.getRoomId());
    	
    	// add point to the opponent
		if(gameRoom.getPlayer1().getPlayerId().equals(request.getSubmittedPlayerId()))
		{
			gameRoomManager.addPlayerScoreToGameReport(request.getRoomId(), gameRoom.getPlayer2().getPlayerId(), request.getQuestionId());
		}
		else
		{
			gameRoomManager.addPlayerScoreToGameReport(request.getRoomId(), gameRoom.getPlayer1().getPlayerId(), request.getQuestionId());
		}
    	
		if(request.getCurrentQuestionIndex() == 10)
		{
			// complete the game
			GameReportResult gameReportResult = gameRoomManager.generateGameReportResult(request.getRoomId());
			gameRoomManager.flushGame(request.getRoomId());
			gameTimerManager.removeGameTimer(request.getRoomId());
			
			messagingTemplate.convertAndSend("/topic/game/" + request.getRoomId() + "/complete", gameReportResult);
		}
		else
		{
			Map<String, Integer> playersScores = gameRoomManager.getPlayersScoresPerGame(request.getRoomId());
			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/updateScore", playersScores);
			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/newQuestion", request.getCurrentQuestionIndex() + 1);
			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/switchTurn", request.getSubmittedPlayerId());
			
			String currentTurn = request.getSubmittedPlayerId();
			int nextQuestionId = gameRoom.getQuestions().get(request.getCurrentQuestionIndex()).getId();
			gameTimerManager.refreshGameTimer(request.getRoomId(), currentTurn, String.valueOf(nextQuestionId));
		}
    }
    
    private void notifyGameStart(GameRoom gameRoom) {
        // Send a WebSocket message to both players in the game room to notify them about the game start
    	GameStartEvent gameStartEventP1 = new GameStartEvent();
    	gameStartEventP1.setRoomId(gameRoom.getRoomId());
    	gameStartEventP1.setPlayerId(gameRoom.getPlayer1().getPlayerId());
    	
    	GameStartEvent gameStartEventP2 = new GameStartEvent();
    	gameStartEventP2.setRoomId(gameRoom.getRoomId());
    	gameStartEventP2.setPlayerId(gameRoom.getPlayer2().getPlayerId());
    	
        messagingTemplate.convertAndSend("/topic/game/start/" + gameRoom.getPlayer1().getUsername(), gameStartEventP1);
        messagingTemplate.convertAndSend("/topic/game/start/" + gameRoom.getPlayer2().getUsername(), gameStartEventP2);
    }
    
    private void waitForSecondPlayer(GameRoom gameRoom) {
    	
    	WaitingRoom waitingRoom = new WaitingRoom();
    	waitingRoom.setRoomId(gameRoom.getRoomId());
    	waitingRoom.setPlayerId(gameRoom.getPlayer1().getPlayerId());
        // Send a message to the player informing them that they need to wait for another player to join
        messagingTemplate.convertAndSend("/topic/game/wait/" + gameRoom.getPlayer1().getUsername(), waitingRoom);
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
    	
    	// Add the sessionId and playerId to the WebSocketSessionManager
        websocketManager.addActiveSession(sessionId, playerId);
    }
    
}