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

public class AuctionChallengeManager extends ChallengeManager{

	public AuctionChallengeManager(Environment env, GameRoomManager gameRoomManager, GameTimerManager gameTimerManager, XMLConfigurationManager xmlConfigurationManager, SimpMessagingTemplate messagingTemplate, ThreadPoolTaskExecutor executor)
	{
		super.env = env;
		super.gameRoomManager = gameRoomManager;
		super.gameTimerManager = gameTimerManager;
		super.xmlConfigurationManager = xmlConfigurationManager;
		super.messagingTemplate = messagingTemplate;
		super.executor = executor;
	}
	
	public ValidateAnswerResult processCorrectAnswer(ValidateAnswerRequest request, ValidateAnswerResult result, GameRoom gameRoom, AnswerVO matchingAnswer, int numOfPossibleAnswers, List<AnswerVO> submittedPlayerAnswers, List<AnswerVO> opponentPlayerAnswers) {

		result.setStatus(1);
		
		List<AnswerVO> answers = gameRoomManager.getPlayerAnswersByGameAndQuestion(request.getRoomId(), request.getQuestionId(), request.getSubmittedPlayerId());
		int nbAnswersProvided = answers.size();
		
		// Get player's total bid
		Map<String, Integer> playersBids = gameRoomManager.getPlayerBidsFromGameReport(request.getRoomId(), request.getQuestionId());
		int bid = playersBids.get(request.getSubmittedPlayerId());
		
		// If player already gave all answers as per his bid, then finish the question and give him the point
		if(nbAnswersProvided == bid)
		{
			result.setAllAnswersProvided(true);
			result.setWinner(request.getSubmittedPlayerId());
		}
		result.setCurrentTurn(request.getSubmittedPlayerId());
		
		messagingTemplate.convertAndSend("/topic/game/" + request.getRoomId() + "/answer", result);
		
		if(nbAnswersProvided == bid)
		{
			// stop the timer
			gameTimerManager.removeGameTimer(request.getRoomId());
			
			// Schedule the 'proceed' function to run in a separate thread after 2 seconds
			executor.execute(() -> {
	            try {
	                Thread.sleep(2000);
	                
	                gameRoomManager.addPlayerScoreToGameReport(request.getRoomId(), request.getSubmittedPlayerId(), request.getQuestionId());
    				proceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getSubmittedPlayerId(), request.getChallengeCategory());
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	        });
		}
		
		return result;
	}
	
	public void strike(String roomId, String submittedPlayerId, String questionId, int currentQuestionIndex, GameRoom gameRoom) {
		
		// notify strike to players and move the turn to the next player
		StrikeResult strikeResult = new StrikeResult();
		strikeResult.setSubmittedPlayer(submittedPlayerId);
		messagingTemplate.convertAndSend("/topic/game/" + roomId + "/"+questionId+"/auctionStrike", strikeResult);
	}
	
	public void auctionTimerComplete(String roomId, String questionId, String submittedPlayerId, int currentQuestionIndex)
	{
		// If timer completes without giving all bid answers, then the opponent gets the point of the question
		GameRoom gameRoom = gameRoomManager.getGameRoomById(roomId);
    	String winningPlayer = submittedPlayerId.equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
		
    	gameRoomManager.addPlayerScoreToGameReport(roomId, winningPlayer, questionId);
    	
    	messagingTemplate.convertAndSend("/topic/game/" + roomId + "/"+questionId+"/"+winningPlayer+"/bidQuestionWinner", "");
		
		if(gameRoom != null)
		{
			proceed(gameRoom, String.valueOf(questionId), currentQuestionIndex, submittedPlayerId, 2);
		}
	}
}
