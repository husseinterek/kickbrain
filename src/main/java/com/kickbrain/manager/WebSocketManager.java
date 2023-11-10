package com.kickbrain.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketManager {

	@Autowired
	private GameRoomManager gameRoomManager;
	
	private final Queue<String> activeSessions = new ConcurrentLinkedQueue<String>();
	private final Queue<String> waitingSessions = new ConcurrentLinkedQueue<String>();
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
    
    public List<String> getActiveSessionsByPlayer(String playerId)
    {
    	List<String> result = new ArrayList<String>();
    	
    	for(Entry<String,String> entry : sessionToPlayerMap.entrySet())
    	{
    		String session = entry.getKey();
    		String sessionPlayer = entry.getValue();
    		if(sessionPlayer.equalsIgnoreCase(playerId))
    		{
    			result.add(session);
    		}
    	}
    	
    	return result;
    }
    
    public void addWaitingSession(String session, String playerId) {
    	
    	waitingSessions.add(session);
    	waitingSessionToPlayerMap.put(session, playerId);
    	lastWaitPingTimes.put(session, System.currentTimeMillis());
    }

    public void removeSession(String sessionId) {
    	
    	Iterator<String> iterator = activeSessions.iterator();
    	while (iterator.hasNext()) 
    	{
    		String session = iterator.next();
    		if(session.equalsIgnoreCase(sessionId))
    		{
    			iterator.remove();
    			break;
    		}
    	}
    	
        sessionToPlayerMap.remove(sessionId);
    }

    public String getPlayerIdBySessionId(String sessionId) {
        return sessionToPlayerMap.get(sessionId);
    }
    
    public String getPlayerIdByWaitingSessionId(String sessionId) {
        return waitingSessionToPlayerMap.get(sessionId);
    }
    
    public Queue<String> getActiveSessions() {
		return activeSessions;
	}
    
    public Queue<String> getWaitingSessions() {
		return waitingSessions;
	}
    
    public ConcurrentHashMap<String, String> getWaitingSessionToPlayerMap() {
		return waitingSessionToPlayerMap;
	}
    
    public ConcurrentHashMap<String, String> getSessionToPlayerMap() {
		return sessionToPlayerMap;
	}
}
