package com.kickbrain.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class GameTimerManager {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	private Map<String, GameTimerTask> timersPerGame = new ConcurrentHashMap<String, GameTimerTask>();
	
	@Autowired
	@Qualifier("timerExecutor")
	private ThreadPoolTaskExecutor executor;
	
	public void refreshGameTimer(String roomId, String currentTurn, String questionId)
	{
		if(timersPerGame.get(roomId) != null)
		{
			// kill existing thread first
			GameTimerTask t = timersPerGame.get(roomId);
			t.shutdown();
		}
		
		GameTimerTask task = new GameTimerTask(messagingTemplate, roomId, currentTurn, questionId);
		executor.submit(task);
		
		timersPerGame.put(roomId, task);
	}
	
	public void removeGameTimer(String roomId)
	{
		GameTimerTask t = timersPerGame.get(roomId);
		t.shutdown();
		timersPerGame.remove(roomId);
	}
}
