package com.kickbrain.challenges;

import java.util.List;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.kickbrain.beans.AnswerVO;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.StrikeResult;
import com.kickbrain.beans.ValidateAnswerRequest;
import com.kickbrain.beans.ValidateAnswerResult;
import com.kickbrain.configuration.XMLConfigurationManager;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.GameTimerManager;

public class WhoAmIChallengeManager extends ChallengeManager{

	public WhoAmIChallengeManager(Environment env, GameRoomManager gameRoomManager, GameTimerManager gameTimerManager, XMLConfigurationManager xmlConfigurationManager, SimpMessagingTemplate messagingTemplate, ThreadPoolTaskExecutor executor)
	{
		super.env = env;
		super.gameRoomManager = gameRoomManager;
		super.gameTimerManager = gameTimerManager;
		super.xmlConfigurationManager = xmlConfigurationManager;
		super.messagingTemplate = messagingTemplate;
		super.executor = executor;
	}
	
	public ValidateAnswerResult processCorrectAnswer(ValidateAnswerRequest request, ValidateAnswerResult result, GameRoom gameRoom, AnswerVO matchingAnswer, int numOfPossibleAnswers, List<AnswerVO> submittedPlayerAnswers, List<AnswerVO> opponentPlayerAnswers) {

		// stop the timer
		gameTimerManager.removeGameTimer(request.getRoomId());
				
		result.setStatus(1);
		
		Map<String, String> questionsResult = gameRoomManager.getQuestionsResultPerGame(gameRoom.getRoomId());
		if(questionsResult.get(request.getQuestionId()) == null)
		{
			// Submitted player wins the point
			gameRoomManager.addPlayerScoreToGameReport(gameRoom.getRoomId(), request.getSubmittedPlayerId(), request.getQuestionId());
					
			result.setAllAnswersProvided(true);
			result.setWinner(request.getSubmittedPlayerId());
			
			messagingTemplate.convertAndSend("/topic/game/" + request.getRoomId() + "/answer", result);
			
			// Schedule the 'proceed' function to run in a separate thread after 8 seconds
			executor.execute(() -> {
	            try {
	                Thread.sleep(2000);
	                proceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getSubmittedPlayerId(), request.getChallengeCategory());
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	        });
		}
		else
		{
			System.out.println("Duplicate Who am I correct answer has been detected!");
		}
		
		return result;
	}
	
	public void strike(String roomId, String submittedPlayerId, String questionId, int currentQuestionIndex, GameRoom gameRoom) {
		
		// notify strike to players and move the turn to the next player
		StrikeResult strikeResult = new StrikeResult();
		strikeResult.setSubmittedPlayer(submittedPlayerId);
		messagingTemplate.convertAndSend("/topic/game/" + roomId + "/whoAmIStrike", strikeResult);
	}
	
	public void whoAmITimerComplete(String roomId, int questionId, int currentQuestionIndex)
	{
		gameRoomManager.addNoAnswerToGameReport(roomId, String.valueOf(questionId));
		
		GameRoom gameRoom = gameRoomManager.getGameRoomById(roomId);
		if(gameRoom != null)
		{
			proceed(gameRoom, String.valueOf(questionId), currentQuestionIndex, gameRoom.getPlayer1().getPlayerId(), 4);
		}
	}
}
