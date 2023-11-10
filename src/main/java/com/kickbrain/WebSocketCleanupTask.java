
package com.kickbrain;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kickbrain.beans.GameReport;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.WaitingGameVO;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.WebSocketManager;

@Service
public class WebSocketCleanupTask {

    private static final long INACTIVE_THRESHOLD_ACTIVE = 30000; // 20 seconds (adjust as needed)
    private static final long INACTIVE_THRESHOLD_WAIT = 600000; // 15 mins (adjust as needed)

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
        	Iterator<String> iterator = webSocketManager.getActiveSessions().iterator();
        	while (iterator.hasNext()) 
        	{
        		String session = iterator.next();
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
                        		System.out.println("Killing active room with Id: " + gameRoom.getRoomId() + " due to inactivity");
                        		
                        		// Save the game details in the database
                				GameReport gameReport = gameRoomManager.getGameReport(gameRoom.getRoomId());
                				gameRoomManager.persistGameRoom(gameRoom, gameReport);
                				
                        		gameRoomManager.flushGame(gameRoom);
                                notifyOtherPlayersAboutDisconnection(gameRoom);
                        	}
                        }
                    }
        		}
        	}
        }
        
        if(webSocketManager.getWaitingSessions() != null)
        {
        	Iterator<String> iterator = webSocketManager.getWaitingSessions().iterator();
        	while (iterator.hasNext())
        	{
        		String session = iterator.next();
        		if(session != null)
        		{
        			Long lastPingTime = webSocketManager.getLastWaitPingTimes().get(session);
        			if (lastPingTime != null && currentTime - lastPingTime > INACTIVE_THRESHOLD_WAIT) {
        				String playerId = getPlayerIdFromWaitingSession(session);
        				if (playerId != null) {
        					WaitingGameVO gameRoom = gameRoomManager.getWaitingGameByPlayerId(playerId);
        					if(gameRoom != null)
                        	{
        						System.out.println("Killing waiting room with Id: " + gameRoom.getId() + " due to inactivity!");
        						gameRoomManager.cancelWaitingGame(String.valueOf(gameRoom.getId()));
                        	}
        				}
        			}
        		}
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