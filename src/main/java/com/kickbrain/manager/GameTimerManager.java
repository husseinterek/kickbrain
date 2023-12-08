package com.kickbrain.manager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.kickbrain.beans.configuration.ChallengeConfig;
import com.kickbrain.beans.configuration.GameConfig;
import com.kickbrain.configuration.XMLConfigurationManager;

@Service
public class GameTimerManager {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private XMLConfigurationManager xmlConfigurationManager;
	
	private Map<String, GameTimerTask> timersPerGame = new ConcurrentHashMap<String, GameTimerTask>();
	
	@Autowired
	@Qualifier("timerExecutor")
	private ThreadPoolTaskExecutor executor;
	
	public void refreshGameTimer(String roomId, String currentTurn, String questionId, Integer challengeCategory, Integer nonAnswerTimer)
	{
		if(timersPerGame.get(roomId) != null)
		{
			// kill existing thread first
			GameTimerTask t = timersPerGame.get(roomId);
			t.shutdown();
		}
		
		int timer = 30;
		if(nonAnswerTimer == null)
		{
			if(challengeCategory != null)
			{
				GameConfig gameConfig = xmlConfigurationManager.getAppConfigurationBean().getOnlineGameConfig();
				List<ChallengeConfig> challenges = gameConfig.getChallenges();
				for(ChallengeConfig challenge : challenges)
				{
					if(challenge.getCategory() == challengeCategory)
					{
						timer = challenge.getAnswerTimer();
					}
				}
			}
		}
		else
		{
			timer = nonAnswerTimer;
		}
		
		GameTimerTask task = new GameTimerTask(messagingTemplate, roomId, currentTurn, questionId, timer);
		executor.submit(task);
		
		timersPerGame.put(roomId, task);
	}
	
	public void removeGameTimer(String roomId)
	{
		if(timersPerGame.get(roomId) != null)
		{
			GameTimerTask t = timersPerGame.get(roomId);
			t.shutdown();
			timersPerGame.remove(roomId);
		}
	}
}
