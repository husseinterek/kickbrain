package com.kickbrain.challenges;

import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.kickbrain.beans.AnswerVO;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.StrikeResult;
import com.kickbrain.beans.ValidateAnswerRequest;
import com.kickbrain.beans.ValidateAnswerResult;
import com.kickbrain.configuration.XMLConfigurationManager;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.GameTimerManager;

public class BellChallengeManager extends ChallengeManager{

	public BellChallengeManager(Environment env, GameRoomManager gameRoomManager, GameTimerManager gameTimerManager, XMLConfigurationManager xmlConfigurationManager, SimpMessagingTemplate messagingTemplate)
	{
		super.env = env;
		super.gameRoomManager = gameRoomManager;
		super.gameTimerManager = gameTimerManager;
		super.xmlConfigurationManager = xmlConfigurationManager;
		super.messagingTemplate = messagingTemplate;
	}
	
	public ValidateAnswerResult processCorrectAnswer(ValidateAnswerRequest request, ValidateAnswerResult result, GameRoom gameRoom, AnswerVO matchingAnswer, int numOfPossibleAnswers, List<AnswerVO> submittedPlayerAnswers, List<AnswerVO> opponentPlayerAnswers) {
		
		// Bell Challenge
		result.setAllAnswersProvided(true);
		result.setWinner(request.getSubmittedPlayerId());
		result.setStatus(1);
		messagingTemplate.convertAndSend("/topic/game/" + request.getRoomId() + "/answer", result);
		
		// Submitted player wins the point
		gameRoomManager.addPlayerScoreToGameReport(gameRoom.getRoomId(), request.getSubmittedPlayerId(), request.getQuestionId());
		proceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getSubmittedPlayerId(), request.getChallengeCategory());
		
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
			proceed(gameRoom, questionId, currentQuestionIndex, opponentPlayer, 2);
			
			// notify player that opponent committed all strikes
			messagingTemplate.convertAndSend("/topic/game/"+gameRoom.getRoomId()+ "/bellNoWinner", "");
		}
		else
		{
			// notify strike to players and move the turn to the next player
			StrikeResult strikeResult = new StrikeResult();
			strikeResult.setSubmittedPlayer(submittedPlayerId);
			strikeResult.setCurrentTurn(opponentPlayer);
			strikeResult.setNbStrikes(1);
			messagingTemplate.convertAndSend("/topic/game/" + roomId + "/bellStrike", strikeResult);
			
			gameTimerManager.refreshGameTimer(roomId, opponentPlayer, questionId, 2, null);
		}
	}
	
	public void skipBell(String roomId, int questionId, int currentQuestionIndex)
	{
		gameRoomManager.addNoAnswerToGameReport(roomId, String.valueOf(questionId));
		
		GameRoom gameRoom = gameRoomManager.getGameRoomById(roomId);
		proceed(gameRoom, String.valueOf(questionId), currentQuestionIndex, null, 2);
	}
}
