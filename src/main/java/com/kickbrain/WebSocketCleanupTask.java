package com.kickbrain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kickbrain.beans.GameRoom;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.WebSocketManager;

@Service
public class WebSocketCleanupTask {

    private static final long INACTIVE_THRESHOLD_ACTIVE = 10000; // 10 seconds (adjust as needed)
    private static final long INACTIVE_THRESHOLD_WAIT = 900000; // 15 mins (adjust as needed)

    @Autowired
    private WebSocketManager webSocketManager;
    
    @Autowired
    private GameRoomManager gameRoomManager;
    
    @Autowired
	private SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 5000) // Run every minute (adjust as needed)
    public void cleanupInactiveWebSocketSessions() {
        long currentTime = System.currentTimeMillis();
        if(webSocketManager.getActiveSessions() != null)
        {
        	List<String> sessionsToBeRemoved = new ArrayList<>();
        	for (String session : webSocketManager.getActiveSessions()) {
        		if(session != null)
        		{
        			Long lastPingTime = webSocketManager.getLastpingtimes().get(session);
                    if (lastPingTime != null && currentTime - lastPingTime > INACTIVE_THRESHOLD_ACTIVE) {
                    	// Handle WebSocket disconnect event here
                        String playerId = getPlayerIdFromSession(session);
                        if (playerId != null) {
                        	GameRoom gameRoom = gameRoomManager.getGameByPlayerId(playerId);
                        	if(gameRoom != null)
                        	{
                        		gameRoomManager.flushGame(gameRoom.getRoomId());
                                notifyOtherPlayersAboutDisconnection(gameRoom);
                                
                                sessionsToBeRemoved.addAll(gameRoom.getPlayersSessions());
                        	}
                        }
                    }
        		}
            }
        	
        	for(String sessionToBeRemoved : sessionsToBeRemoved)
        	{
        		webSocketManager.getActiveSessions().remove(sessionToBeRemoved);
        		webSocketManager.getLastpingtimes().remove(sessionToBeRemoved);
        	}
        }
        
        if(webSocketManager.getWaitingSessions() != null)
        {
        	List<String> sessionsToBeRemoved = new ArrayList<>();
        	for (String session : webSocketManager.getWaitingSessions()) {
        		if(session != null)
        		{
        			Long lastPingTime = webSocketManager.getLastWaitPingTimes().get(session);
        			if (lastPingTime != null && currentTime - lastPingTime > INACTIVE_THRESHOLD_WAIT) {
        				String playerId = getPlayerIdFromWaitingSession(session);
        				if (playerId != null) {
        					GameRoom gameRoom = gameRoomManager.getWaitingGameByPlayerId(playerId);
        					if(gameRoom != null)
                        	{
        						gameRoomManager.flushWaitingGame(gameRoom.getRoomId());
        						sessionsToBeRemoved.addAll(gameRoom.getPlayersSessions());
                        	}
        				}
        			}
        		}
        	}
        	
        	for(String sessionToBeRemoved : sessionsToBeRemoved)
        	{
        		webSocketManager.getWaitingSessions().remove(sessionToBeRemoved);
        		webSocketManager.getLastWaitPingTimes().remove(sessionToBeRemoved);
        	}
        }
    }
    
    private String getPlayerIdFromSession(String sessionId) {
        return webSocketManager.getPlayerIdBySessionId(sessionId);
    }
    
    private String getPlayerIdFromWaitingSession(String sessionId) {
    	return webSocketManager.getPlayerIdByWaitingSessionId(sessionId);
    }
	
	private void notifyOtherPlayersAboutDisconnection(GameRoom gameRoom) {
		messagingTemplate.convertAndSend("/topic/game/"+gameRoom.getRoomId()+"/end", "");
    }
}