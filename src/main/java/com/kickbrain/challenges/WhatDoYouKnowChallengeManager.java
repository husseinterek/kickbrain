package com.kickbrain.challenges;

import java.util.ArrayList;
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

public class WhatDoYouKnowChallengeManager extends ChallengeManager{
	
	public WhatDoYouKnowChallengeManager(Environment env, GameRoomManager gameRoomManager, GameTimerManager gameTimerManager, XMLConfigurationManager xmlConfigurationManager, SimpMessagingTemplate messagingTemplate, ThreadPoolTaskExecutor executor)
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
				
		// What do you know challenge
		List<AnswerVO> allSubmittedAnswers = new ArrayList<AnswerVO>();
		allSubmittedAnswers.add(matchingAnswer);
		
		if(submittedPlayerAnswers == null)
		{
			submittedPlayerAnswers = new ArrayList<AnswerVO>();
			submittedPlayerAnswers.add(matchingAnswer);
		}
		if(opponentPlayerAnswers == null)
		{
			opponentPlayerAnswers = new ArrayList<AnswerVO>();
		}
		
		int submittedAnswers = submittedPlayerAnswers.size() + opponentPlayerAnswers.size();
		
		result.setAllAnswersProvided(submittedAnswers == numOfPossibleAnswers);
		result.setTie(opponentPlayerAnswers.size() == submittedPlayerAnswers.size());
		if(submittedAnswers == numOfPossibleAnswers && !result.isTie())
		{
			result.setWinner((submittedPlayerAnswers.size() > opponentPlayerAnswers.size()) ? request.getSubmittedPlayerId() : request.getOpponentPlayerId());
		}
		result.setStatus(1);
		messagingTemplate.convertAndSend("/topic/game/" + request.getRoomId() + "/answer", result);
		
		final List<AnswerVO> submittedPlayerAnswersFinal = submittedPlayerAnswers;
		final List<AnswerVO> opponentPlayerAnswersFinal = opponentPlayerAnswers;
		if(submittedAnswers == numOfPossibleAnswers)
		{
			// Schedule the 'proceed' function to run in a separate thread=
			executor.execute(() -> {
	            try {
	                Thread.sleep(2000);
	                
	                // All possible answers are submitted. Give the point of the question to the player who gave more answers. In case of tie, both players get the point
	    			if(submittedPlayerAnswersFinal.size() > opponentPlayerAnswersFinal.size())
	    			{
	    				// Submitted player wins the point
	    				gameRoomManager.addPlayerScoreToGameReport(request.getRoomId(), request.getSubmittedPlayerId(), request.getQuestionId());
	    				proceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getSubmittedPlayerId(), request.getChallengeCategory());
	    			}
	    			else
	    			{
	    				if(opponentPlayerAnswersFinal.size() > submittedPlayerAnswersFinal.size())
	    				{
	    					// opponent player wins the point
	    					gameRoomManager.addPlayerScoreToGameReport(gameRoom.getRoomId(), request.getOpponentPlayerId(), request.getQuestionId());
	    					proceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getOpponentPlayerId(), request.getChallengeCategory());
	    				}
	    				else
	    				{
	    					// both players take the point
	    					gameRoomManager.addPlayerScoreToGameReport(gameRoom.getRoomId(), request.getSubmittedPlayerId(), request.getQuestionId());
	    					gameRoomManager.addPlayerScoreToGameReport(gameRoom.getRoomId(), request.getOpponentPlayerId(), request.getQuestionId());
	    					gameRoomManager.setQuestionResultasTie(gameRoom.getRoomId(), request.getQuestionId(), request.getSubmittedPlayerId(), request.getOpponentPlayerId());
	    					proceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getOpponentPlayerId(), request.getChallengeCategory());
	    				}
	    			}
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	        });
		}
		else
		{
			String currentTurn = request.getSubmittedPlayerId().equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
			gameTimerManager.refreshGameTimer(request.getRoomId(), currentTurn, request.getQuestionId(), request.getChallengeCategory(), null);
		}
		
		return result;
	}
	
	public void strike(String roomId, String submittedPlayerId, String questionId, int currentQuestionIndex, GameRoom gameRoom) {
		
		// what do you know challenge
		Integer strikes = gameRoomManager.getPlayerStrikesByGameAndQuestion(roomId, questionId, submittedPlayerId);
		String currentTurn = submittedPlayerId.equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
		StrikeResult strikeResult = new StrikeResult();
		strikeResult.setSubmittedPlayer(submittedPlayerId);
		strikeResult.setCurrentTurn(currentTurn);
		strikeResult.setNbStrikes(strikes == null ? 1 : strikes+1);
		messagingTemplate.convertAndSend("/topic/game/" + roomId + "/strike", strikeResult);
		
		if(strikes == null || strikes < 2)
		{
			gameRoomManager.addPlayerStrikeToGameReport(roomId, questionId, submittedPlayerId);
			gameTimerManager.refreshGameTimer(roomId, currentTurn, questionId, 1, null);
		}
		else
		{
			// add point to the opponent
			String playerId;
			if(gameRoom.getPlayer1().getPlayerId().equals(submittedPlayerId))
			{
				playerId = gameRoom.getPlayer2().getPlayerId();
			}
			else
			{
				playerId = gameRoom.getPlayer1().getPlayerId();
			}
			
			gameRoomManager.addPlayerScoreToGameReport(gameRoom.getRoomId(), playerId, questionId);
			
			// notify player that opponent committed all strikes
			String opponentPlayerId = submittedPlayerId.equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
			messagingTemplate.convertAndSend("/topic/game/"+gameRoom.getRoomId()+ "/" + opponentPlayerId + "/allStrikesCommitted", "");
						
			proceed(gameRoom, questionId, currentQuestionIndex, playerId, 1);
		}
	}
}
