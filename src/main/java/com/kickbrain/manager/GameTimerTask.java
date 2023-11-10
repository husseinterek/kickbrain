package com.kickbrain.manager;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.kickbrain.beans.GameTimerEvent;

public class GameTimerTask implements Runnable {

	private int timer = 30;
	private SimpMessagingTemplate messagingTemplate;
	private String roomId;
	private String currentTurn;
	private String questionId;
	private boolean shutdown;
	
    public GameTimerTask(SimpMessagingTemplate messagingTemplate, String roomId, String currentTurn, String questionId, int timer) {
        this.messagingTemplate = messagingTemplate;
        this.roomId = roomId;
        this.currentTurn = currentTurn;
        this.questionId = questionId;
        this.timer = timer;
    }
	
	@Override
	public void run() {
		
		for(int i=timer; i>=0; i--)
		{
			if(!shutdown)
			{
				GameTimerEvent gameTimerEvent = new GameTimerEvent();
		        gameTimerEvent.setCurrentPlayer(currentTurn);
		        gameTimerEvent.setTimer(i);
		        gameTimerEvent.setQuestionId(questionId);
		        messagingTemplate.convertAndSend("/topic/game/"+roomId+"/timer", gameTimerEvent);
		        
		        try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else
			{
				break;
			}
		}
	}
	
	public void shutdown() {
		shutdown = true;
	}
}
