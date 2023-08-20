package com.kickbrain.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketManager {

	@Autowired
	private GameRoomManager gameRoomManager;
	
	private final List<String> activeSessions = new ArrayList<String>();
	private final List<String> waitingSessions = new ArrayList<String>();
    private final Map<String, Long> lastPingTimes = new ConcurrentHashMap<String, Long>();
    private final Map<String, Long> lastWaitPingTimes = new ConcurrentHashMap<String, Long>();
    private final ConcurrentHashMap<String, String> sessionToPlayerMap = new ConcurrentHashMap<String, String>();
    private final ConcurrentHashMap<String, String> waitingSessionToPlayerMap = new ConcurrentHashMap<String, String>();
    
	public void addActiveSession(String session)
    {
    	activeSessions.add(session);
    }
    
    public void handlePingMessage(String session){
    	lastPingTimes.put(session, System.currentTimeMillis());
    }
    
    public Map<String, Long> getLastpingtimes() {
		return lastPingTimes;
	}
    
    public void handleWaitPingMessage(String session){
    	lastWaitPingTimes.put(session, System.currentTimeMillis());
    }
    
    public Map<String, Long> getLastWaitPingTimes() {
		return lastWaitPingTimes;
	}
    
    public void addActiveSession(String session, String playerId) {
    	
    	gameRoomManager.addPlayerSessionToGame(playerId, session);
    	
    	activeSessions.add(session);
        sessionToPlayerMap.put(session, playerId);
        lastPingTimes.put(session, System.currentTimeMillis());
    }
    
    public void addWaitingSession(String session, String playerId) {
    	
    	waitingSessions.add(session);
    	waitingSessionToPlayerMap.put(session, playerId);
    	lastWaitPingTimes.put(session, System.currentTimeMillis());
    }

    public void removeSession(String sessionId) {
    	
    	Integer indexToBeRemoved = null;
    	for(int i=0; i<activeSessions.size(); i++)
    	{
    		String session = activeSessions.get(i);
    		if(session.equalsIgnoreCase(sessionId))
    		{
    			indexToBeRemoved = i;
    			break;
    		}
    	}
    	
    	if(indexToBeRemoved != null)
    	{
    		activeSessions.remove((int)indexToBeRemoved);
    	}
        sessionToPlayerMap.remove(sessionId);
    }

    public String getPlayerIdBySessionId(String sessionId) {
        return sessionToPlayerMap.get(sessionId);
    }
    
    public String getPlayerIdByWaitingSessionId(String sessionId) {
        return waitingSessionToPlayerMap.get(sessionId);
    }
    
    public List<String> getActiveSessions() {
		return activeSessions;
	}
    
    public List<String> getWaitingSessions() {
		return waitingSessions;
	}
}
