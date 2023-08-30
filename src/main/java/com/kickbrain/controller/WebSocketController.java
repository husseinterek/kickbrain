package com.kickbrain.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.kickbrain.beans.GameReportResult;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.Player;
import com.kickbrain.beans.SkipQuestionRequest;
import com.kickbrain.beans.WaitingRoom;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.WebSocketManager;

@Controller
public class WebSocketController {

	private GameRoomManager gameRoomManager;
	private SimpMessagingTemplate messagingTemplate;
    private WebSocketManager websocketManager;
	
    @Autowired
    public WebSocketController(GameRoomManager gameRoomManager, SimpMessagingTemplate messagingTemplate, WebSocketManager websocketManager) {
        this.gameRoomManager = gameRoomManager;
        this.messagingTemplate = messagingTemplate;
        this.websocketManager = websocketManager;
    }
    
    @MessageMapping("/startGame")
    public void startGame(Player player, SimpMessageHeaderAccessor headerAccessor) {
    	// Check if there is an available game room to join
        GameRoom availableGameRoom = gameRoomManager.findAvailableGameRoom();
        String playerId = null;
        try
        {
        	if (availableGameRoom != null) {
        		
        		String player1Username = availableGameRoom.getPlayer1().getUsername();
        		if(!player1Username.equalsIgnoreCase(player.getUsername()))
        		{
        			// An available game room is found, add the player to the room as player2
                	GameRoom activeRoom = gameRoomManager.createActiveGameRoom(availableGameRoom, player);
                	playerId = activeRoom.getPlayer2().getPlayerId();
                	
                    // Notify both players in the room about the game start
                    notifyGameStart(availableGameRoom);
        		}
        		else
        		{
        			String sessionId = headerAccessor.getSessionId();
        			// Don't allow users with same username to play together
        			// No available game room found, create a new game room and add the player as player1
                    GameRoom newGameRoom = gameRoomManager.createWaitingGameRoom(player, sessionId);
                    playerId = newGameRoom.getPlayer1().getPlayerId();
                    
                    // Add the sessionId and playerId to the WebSocketSessionManager
                    websocketManager.addWaitingSession(sessionId, playerId);
                    
                    // Wait for the second player to join the room
                    waitForSecondPlayer(newGameRoom);
        		}
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
			messagingTemplate.convertAndSend("/topic/game/" + request.getRoomId() + "/complete", gameReportResult);
		}
		else
		{
			Map<String, Integer> playersScores = gameRoomManager.getPlayersScoresPerGame(request.getRoomId());
			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/updateScore", playersScores);
			messagingTemplate.convertAndSend("/topic/game/"+request.getRoomId()+"/newQuestion", request.getCurrentQuestionIndex() + 1);
		}
    }
    
    private void notifyGameStart(GameRoom gameRoom) {
        // Send a WebSocket message to both players in the game room to notify them about the game start
        messagingTemplate.convertAndSend("/topic/game/start/" + gameRoom.getPlayer1().getUsername(), gameRoom.getRoomId());
        messagingTemplate.convertAndSend("/topic/game/start/" + gameRoom.getPlayer2().getUsername(), gameRoom.getRoomId());
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