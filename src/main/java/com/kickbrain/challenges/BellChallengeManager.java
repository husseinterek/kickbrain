package com.kickbrain.challenges;

import java.util.List;

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

public class BellChallengeManager extends ChallengeManager{

	public BellChallengeManager(Environment env, GameRoomManager gameRoomManager, GameTimerManager gameTimerManager, XMLConfigurationManager xmlConfigurationManager, SimpMessagingTemplate messagingTemplate, ThreadPoolTaskExecutor executor)
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
		
		// Bell Challenge
		result.setAllAnswersProvided(true);
		result.setWinner(request.getSubmittedPlayerId());
		result.setStatus(1);
		messagingTemplate.convertAndSend("/topic/game/" + request.getRoomId() + "/answer", result);
		
		// Submitted player wins the point
		gameRoomManager.addPlayerScoreToGameReport(gameRoom.getRoomId(), request.getSubmittedPlayerId(), request.getQuestionId());
		
		// Schedule the 'proceed' function to run in a separate thread after 8 seconds
		executor.execute(() -> {
            try {
                Thread.sleep(2000);
                proceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getSubmittedPlayerId(), request.getChallengeCategory());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
		
		return result;
	}
	
	public void strike(String roomId, String submittedPlayerId, String questionId, int currentQuestionIndex, GameRoom gameRoom) {
		
		gameRoomManager.addPlayerStrikeToGameReport(roomId, questionId, submittedPlayerId);
		
		// Check if the opponent player already got a strike, if that's the case then end the question and don't give the point to any player. Otherwise, switch turn to another player
		String opponentPlayer = submittedPlayerId.equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
		Integer opponentStrikes = gameRoomManager.getPlayerStrikesByGameAndQuestion(roomId, questionId, opponentPlayer);
		
		if(opponentStrikes != null && opponentStrikes >= 1)
		{
			// end the question and no one gets the point
			gameRoomManager.addNoAnswerToGameReport(roomId, String.valueOf(questionId));
			
			// notify player that opponent committed all strikes
			messagingTemplate.convertAndSend("/topic/game/"+gameRoom.getRoomId()+ "/bellNoWinner", "");
						
			proceed(gameRoom, questionId, currentQuestionIndex, opponentPlayer, 3);
		}
		else
		{
			// notify strike to players and move the turn to the next player
			StrikeResult strikeResult = new StrikeResult();
			strikeResult.setSubmittedPlayer(submittedPlayerId);
			strikeResult.setCurrentTurn(opponentPlayer);
			strikeResult.setNbStrikes(1);
			messagingTemplate.convertAndSend("/topic/game/" + roomId + "/bellStrike", strikeResult);
			
			gameTimerManager.refreshGameTimer(roomId, opponentPlayer, questionId, 3, null);
		}
	}
	
	public void skipBell(String roomId, int questionId, int currentQuestionIndex)
	{
		gameRoomManager.addNoAnswerToGameReport(roomId, String.valueOf(questionId));
		
		GameRoom gameRoom = gameRoomManager.getGameRoomById(roomId);
		if(gameRoom != null)
		{
			proceed(gameRoom, String.valueOf(questionId), currentQuestionIndex, gameRoom.getPlayer1().getPlayerId(), 3);
		}
	}
}
