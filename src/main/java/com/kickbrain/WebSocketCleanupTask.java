
package com.kickbrain;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kickbrain.beans.GameReport;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.GameVO;
import com.kickbrain.beans.QuestionResult;
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
                				
                				// Set the remaining questions as pass for the opponent player in case at least 2 questions are already answered
        						Map<String, String> questionsResult = gameReport.getQuestionsResult();
        						List<QuestionResult> gameQuestions = gameRoom.getQuestions();
        						
        						if(questionsResult.keySet().size() >= 2 && questionsResult.keySet().size() < gameQuestions.size())
        						{
        							String opponentPlayerId = playerId.equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
        							for(QuestionResult question : gameQuestions)
        							{
        								if(questionsResult.get(String.valueOf(question.getId())) == null)
        								{
        									// Question is not answered, so mark it as successfull for the opponent player
        									gameRoomManager.addPlayerScoreToGameReport(gameReport.getRoomId(), opponentPlayerId, String.valueOf(question.getId()));
        								}
        							}
        							gameReport = gameRoomManager.getGameReport(gameRoom.getRoomId());
        						}
                				
                				GameVO savedGame = gameRoomManager.persistGameRoom(gameRoom, gameReport);
                				gameRoomManager.cleanActiveGame(String.valueOf(savedGame.getId()), playerId);
                				
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