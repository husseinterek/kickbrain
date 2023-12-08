package com.kickbrain.challenges;

import java.util.List;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.kickbrain.beans.AnswerVO;
import com.kickbrain.beans.ChallengeBean;
import com.kickbrain.beans.GameReport;
import com.kickbrain.beans.GameReportResult;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.QuestionResult;
import com.kickbrain.beans.SkipQuestionEvent;
import com.kickbrain.beans.ValidateAnswerRequest;
import com.kickbrain.beans.ValidateAnswerResult;
import com.kickbrain.beans.configuration.ChallengeConfig;
import com.kickbrain.beans.configuration.GameConfig;
import com.kickbrain.configuration.XMLConfigurationManager;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.GameTimerManager;
import com.kickbrain.manager.Utility;

public abstract class ChallengeManager {

	protected Environment env;
	protected GameRoomManager gameRoomManager;
	protected GameTimerManager gameTimerManager;
	protected XMLConfigurationManager xmlConfigurationManager;
	protected SimpMessagingTemplate messagingTemplate;
	protected ThreadPoolTaskExecutor executor;
	
	public ValidateAnswerResult validateAnswer(ValidateAnswerRequest request)
	{
		ValidateAnswerResult result = new ValidateAnswerResult();
		try
		{
			int minimumMatchingRatio = Integer.valueOf(env.getProperty("answer.matchingRatio"));

			GameRoom gameRoom = gameRoomManager.getGameRoomById(request.getRoomId());
			if(gameRoom != null)
			{
				Map<Integer, List<AnswerVO>> answersByQuestion = gameRoom.getAnswersByQuestion();
				List<AnswerVO> possibleAnswers = answersByQuestion.get(Integer.valueOf(request.getQuestionId()));
				
				List<AnswerVO> submittedPlayerAnswers = gameRoomManager.getPlayerAnswersByGameAndQuestion(request.getRoomId(), request.getQuestionId(), request.getSubmittedPlayerId());
				List<AnswerVO> opponentPlayerAnswers = gameRoomManager.getPlayerAnswersByGameAndQuestion(request.getRoomId(), request.getQuestionId(), request.getOpponentPlayerId());
				
				int fullMatchRatio = xmlConfigurationManager.getAppConfigurationBean().getAnswerFullMatchRatio();
				int partMatchRatio = xmlConfigurationManager.getAppConfigurationBean().getAnswerPartMatchRatio();
				
				AnswerVO matchingAnswer = Utility.getMatchingAnswerV2(request.getCapturedAnswer(), possibleAnswers, submittedPlayerAnswers, opponentPlayerAnswers, minimumMatchingRatio, fullMatchRatio, partMatchRatio);
				if(matchingAnswer != null)
				{
					gameRoomManager.addSuccessfulAnswerToGameReport(request.getRoomId(), request.getQuestionId(), matchingAnswer, request.getSubmittedPlayerId());
					
					result.setMatchingAnswer(matchingAnswer.getAnswerEn());
					result.setCorrect(matchingAnswer != null);
					result.setSubmittedPlayer(request.getSubmittedPlayerId());
					result.setCurrentTurn(request.getSubmittedPlayerId().equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId());
					result.setAnswerElementId(request.getAnswerElementId());
					
					processCorrectAnswer(request, result, gameRoom, matchingAnswer, possibleAnswers.size(), submittedPlayerAnswers, opponentPlayerAnswers);
				}
				else
				{
					strike(request.getRoomId(), request.getSubmittedPlayerId(), request.getQuestionId(), request.getCurrentQuestionIdx(), gameRoom);
				}
				
				result.setStatus(1);
			}
			else
			{
				result.setStatus(0);
				result.setErrorMessage("Error occured while validating the answer");
			}
		}
		catch(Exception ex)
		{
			result.setErrorMessage("Error occured while validating the answer");
			result.setStatus(0);
			ex.printStackTrace();
		}
		
		return result;
	}
	
	protected void proceed(GameRoom gameRoom, String questionId, int currentQuestionIndex, String playerId, Integer challengeCategory)
	{
		String roomId = gameRoom.getRoomId();
		
		boolean isGameComplete = false;
		ChallengeBean returnedChallenge = new ChallengeBean();
		if(challengeCategory != null)
		{
			Map<Integer, ChallengeBean> challengesMap = gameRoom.getChallengesMap();
			ChallengeBean currentChallenge = challengesMap.get(challengeCategory);
			
			List<QuestionResult> challengeQuestions = currentChallenge.getQuestions();
			if(currentQuestionIndex == challengeQuestions.size())
			{
				// Challenge is completed, move on to the next challenge in the game
				returnedChallenge = challengesMap.get((challengeCategory + 1));
				isGameComplete = returnedChallenge == null;
			}
			else
			{
				returnedChallenge = currentChallenge;
			}
		}
		else
		{
			returnedChallenge = gameRoom.getChallenges().get(0);
			isGameComplete = currentQuestionIndex == returnedChallenge.getQuestions().size();
		}

		// open new question or complete game
		if(isGameComplete)
		{
			System.out.println("The game with room Id: " + gameRoom.getRoomId() + " is completed!");
			
			// complete the game
			GameReportResult gameReportResult = gameRoomManager.generateGameReportResult(roomId);
			
			messagingTemplate.convertAndSend("/topic/game/" + roomId + "/complete", gameReportResult);
			gameTimerManager.removeGameTimer(roomId);
			
			// Save the game details in the database
			GameReport gameReport = gameRoomManager.getGameReport(gameRoom.getRoomId());
			gameRoomManager.persistGameRoom(gameRoom, gameReport);
			
			gameRoomManager.flushGame(gameRoom);
		}
		else
		{
			// open new question
			Map<String, Integer> playersScores = gameRoomManager.getPlayersScoresPerGame(roomId);
			messagingTemplate.convertAndSend("/topic/game/"+roomId+"/updateScore", playersScores);
			
			SkipQuestionEvent skipQuestionEvent = new SkipQuestionEvent();
			QuestionResult nextQuestion = null;
			boolean delayNewChallenge = false;
			if(challengeCategory != null)
			{
				if(returnedChallenge.getCategory() != challengeCategory)
				{
					// Challenge has been changed
					skipQuestionEvent.setNextQuestionIndex(1);
					
					boolean isLastQuestion = false;
					if(returnedChallenge.getQuestions().size() == 1 && (gameRoom.getChallengesMap().get((challengeCategory + 1)) == null))
					{
						// If it is the last question in the current challenge and there are no remaining challenges
						isLastQuestion = true;
					}
					skipQuestionEvent.setLastQuestion(isLastQuestion);
					
					nextQuestion = returnedChallenge.getQuestions().get(0);
					delayNewChallenge = true;
				}
				else
				{
					// Get next index for the same challenge
					skipQuestionEvent.setNextQuestionIndex(currentQuestionIndex + 1);
					nextQuestion = returnedChallenge.getQuestions().get(currentQuestionIndex);
					
					boolean isLastQuestion = false;
					if((returnedChallenge.getQuestions().size() == (currentQuestionIndex + 1)) && (gameRoom.getChallengesMap().get((currentQuestionIndex + 1)) == null))
					{
						// If it is the last question in the current challenge and there are no remaining challenges
						isLastQuestion = true;
					}
					skipQuestionEvent.setLastQuestion(isLastQuestion);
				}
			}
			else
			{
				skipQuestionEvent.setNextQuestionIndex(currentQuestionIndex + 1);
				skipQuestionEvent.setLastQuestion(returnedChallenge.getQuestions().size() == (currentQuestionIndex + 1));
				nextQuestion = returnedChallenge.getQuestions().get(currentQuestionIndex);
			}
			
			if(delayNewChallenge)
			{
				messagingTemplate.convertAndSend("/topic/game/"+gameRoom.getRoomId()+"/nextChallengePopup", returnedChallenge.getCategory());
				// delay the trigger of the new question by 5 seconds
				int delayInSecs = xmlConfigurationManager.getAppConfigurationBean().getDelayNextChallenge();
				try
				{
					Thread.sleep(delayInSecs * 1000);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			skipQuestionEvent.setChallengeCategory(returnedChallenge.getCategory());
			messagingTemplate.convertAndSend("/topic/game/"+roomId+"/newQuestion", skipQuestionEvent);
			
			String currentTurn = null;
			if(playerId != null)
			{
				currentTurn = playerId.equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId();
				messagingTemplate.convertAndSend("/topic/game/"+roomId+"/switchTurn", currentTurn);
			}
			
			int nextQuestionId = nextQuestion.getId();
			
			Integer nonAnswerTimer = null;
			if(returnedChallenge.getCategory() == 2)
			{
    			GameConfig gameConfig = xmlConfigurationManager.getAppConfigurationBean().getOnlineGameConfig();
				List<ChallengeConfig> challenges = gameConfig.getChallenges();
				for(ChallengeConfig challenge : challenges)
				{
					if(challenge.getCategory() == returnedChallenge.getCategory())
					{
						nonAnswerTimer = challenge.getBidTimer();
					}
				}
			}
			if(returnedChallenge.getCategory() == 3)
			{
    			GameConfig gameConfig = xmlConfigurationManager.getAppConfigurationBean().getOnlineGameConfig();
				List<ChallengeConfig> challenges = gameConfig.getChallenges();
				for(ChallengeConfig challenge : challenges)
				{
					if(challenge.getCategory() == returnedChallenge.getCategory())
					{
						nonAnswerTimer = challenge.getBellTimer();
					}
				}
			}
			
			gameTimerManager.refreshGameTimer(roomId, currentTurn, String.valueOf(nextQuestionId), nextQuestion.getChallengeCategory(), nonAnswerTimer);
		}
	}
	
	public abstract ValidateAnswerResult processCorrectAnswer(ValidateAnswerRequest request, ValidateAnswerResult result, GameRoom gameRoom, AnswerVO matchingAnswer, int numOfPossibleAnswers, List<AnswerVO> submittedPlayerAnswers, List<AnswerVO> opponentPlayerAnswers);

	public abstract void strike(String roomId, String submittedPlayerId, String questionId, int currentQuestionIndex, GameRoom gameRoom);
}
