package com.kickbrain.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.icu.text.Transliterator;
import com.kickbrain.beans.BaseResult;
import com.kickbrain.beans.BotAnswerRequest;
import com.kickbrain.beans.BotAnswerResult;
import com.kickbrain.beans.GameReportResult;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.beans.Player;
import com.kickbrain.beans.StrikeRequest;
import com.kickbrain.beans.StrikeResult;
import com.kickbrain.beans.ValidateAnswerRequest;
import com.kickbrain.beans.ValidateAnswerResult;
import com.kickbrain.beans.ValidateSinglePlayerAnswerRequest;
import com.kickbrain.beans.ValidateSinglePlayerAnswerResult;
import com.kickbrain.beans.configuration.Question;
import com.kickbrain.configuration.XMLConfigurationManager;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.WebSocketManager;

import me.xdrop.fuzzywuzzy.FuzzySearch;

@RestController
@RequestMapping("/game")
public class GameController {
	
	@Autowired
	private XMLConfigurationManager xmlConfigurationManager;
	
	@Autowired
	private GameRoomManager gameRoomManager;
	
	@Autowired
    private WebSocketManager webSocketManager;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private Environment env;
	
	@RequestMapping(value = "/validateAnswer", method = RequestMethod.POST, consumes="application/json")
	public ValidateAnswerResult validateAnswer(@RequestBody ValidateAnswerRequest request, BindingResult bindingResult, ModelMap model) {
		
		ValidateAnswerResult result = new ValidateAnswerResult();
		try
		{
			int minimumMatchingRatio = Integer.valueOf(env.getProperty("answer.matchingRatio"));
			Question question = xmlConfigurationManager.getAppConfigurationBean().getQuestionsMap().get(Integer.valueOf(request.getQuestionId()));
			List<String> answers = question.getAnswers();
			
			List<String> submittedPlayerAnswers = gameRoomManager.getPlayerAnswersByGameAndQuestion(request.getRoomId(), request.getQuestionId(), request.getSubmittedPlayerId());
			List<String> opponentPlayerAnswers = gameRoomManager.getPlayerAnswersByGameAndQuestion(request.getRoomId(), request.getQuestionId(), request.getOpponentPlayerId());
			
			boolean answerMatch = false;
			String matchingAnswer = null;
			int highestMatchingRatio = 0;
			for(String answer : answers)
			{
				answer = StringUtils.stripAccents(answer);
				if((opponentPlayerAnswers != null && opponentPlayerAnswers.contains(answer)) || (submittedPlayerAnswers != null && submittedPlayerAnswers.contains(answer)))
				{
					continue;
				}
				else
				{
					String capturedAnswer = transliterateAnswer(request.getCapturedAnswer());
					int matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), answer.toLowerCase());
					if(matchingRatio >= minimumMatchingRatio)
					{
						if(highestMatchingRatio < matchingRatio)
						{
							highestMatchingRatio = matchingRatio;
							matchingAnswer = answer;
						}
						answerMatch = true;
					}
				}
			}
			
			GameRoom gameRoom = gameRoomManager.getGameRoomById(request.getRoomId());
			if(answerMatch)
			{
				gameRoomManager.addSuccessfulAnswerToGameReport(request.getRoomId(), request.getQuestionId(), matchingAnswer, request.getSubmittedPlayerId());
				
				result.setMatchingAnswer(matchingAnswer);
				result.setCorrect(answerMatch);
				result.setSubmittedPlayer(request.getSubmittedPlayerId());
				result.setCurrentTurn(request.getSubmittedPlayerId().equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId());
				result.setAnswerElementId(request.getAnswerElementId());
				
				// End the question if all possible answers are provided by both players
				List<String> allSubmittedAnswers = new ArrayList<String>();
				allSubmittedAnswers.add(matchingAnswer);
				
				int numOfPossibleAnswers = answers.size();
				if(submittedPlayerAnswers == null)
				{
					submittedPlayerAnswers = new ArrayList<String>();
				}
				submittedPlayerAnswers.add(matchingAnswer);
				int submittedAnswers = submittedPlayerAnswers.size() + (opponentPlayerAnswers == null ? 0 : opponentPlayerAnswers.size());
				
				result.setAllAnswersProvided(submittedAnswers == numOfPossibleAnswers);
				messagingTemplate.convertAndSend("/topic/game/" + request.getRoomId() + "/answer", result);
				
				if(submittedAnswers == numOfPossibleAnswers)
				{
					// All possible answers are submitted. Give the point of the question to the player who gave more answers. In case of tie, both players get the point
					if(submittedPlayerAnswers.size() > opponentPlayerAnswers.size())
					{
						// Submitted player wins the point
						addPointAndProceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getSubmittedPlayerId());
					}
					else
					{
						if(opponentPlayerAnswers.size() > submittedPlayerAnswers.size())
						{
							// opponent player wins the point
							addPointAndProceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getOpponentPlayerId());
						}
						else
						{
							// both players take the point
							gameRoomManager.addPlayerScoreToGameReport(gameRoom.getRoomId(), request.getSubmittedPlayerId(), request.getQuestionId());
							addPointAndProceed(gameRoom, request.getQuestionId(), request.getCurrentQuestionIdx(), request.getOpponentPlayerId());
						}
					}
				}
			}
			else
			{
				strike(request.getRoomId(), request.getSubmittedPlayerId(), request.getQuestionId(), request.getCurrentQuestionIdx(), gameRoom);
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
	
	@RequestMapping(value = "/validateSinglePlayerAnswer", method = RequestMethod.POST, consumes="application/json")
	public ValidateSinglePlayerAnswerResult validateSinglePlayerAnswer(@RequestBody ValidateSinglePlayerAnswerRequest request, BindingResult bindingResult, ModelMap model) {
		
		ValidateSinglePlayerAnswerResult result = new ValidateSinglePlayerAnswerResult();
		try
		{
			int minimumMatchingRatio = Integer.valueOf(env.getProperty("answer.matchingRatio"));
			Question question = xmlConfigurationManager.getAppConfigurationBean().getQuestionsMap().get(Integer.valueOf(request.getQuestionId()));
			List<String> answers = question.getAnswers();
			
			List<String> submittedPlayerAnswers = request.getSubmittedPlayerAnswers();
			
			boolean answerMatch = false;
			String matchingAnswer = null;
			int highestMatchingRatio = 0;
			for(String answer : answers)
			{
				answer = StringUtils.stripAccents(answer);
				if((submittedPlayerAnswers != null && submittedPlayerAnswers.contains(answer)))
				{
					continue;
				}
				else
				{
					String capturedAnswer = transliterateAnswer(request.getCapturedAnswer());
					int matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), answer.toLowerCase());
					if(matchingRatio >= minimumMatchingRatio)
					{
						if(highestMatchingRatio < matchingRatio)
						{
							highestMatchingRatio = matchingRatio;
							matchingAnswer = answer;
						}
						answerMatch = true;
					}
				}
			}
			
			result.setCorrect(answerMatch);
			result.setAnswerElementId(request.getAnswerElementId());
			if(answerMatch)
			{
				result.setMatchingAnswer(matchingAnswer);

				// End the question if all possible answers are provided
				List<String> allSubmittedAnswers = new ArrayList<String>();
				allSubmittedAnswers.add(matchingAnswer);
				
				int numOfPossibleAnswers = answers.size();
				if(submittedPlayerAnswers == null)
				{
					submittedPlayerAnswers = new ArrayList<String>();
				}
				submittedPlayerAnswers.add(matchingAnswer);
				
				result.setAllAnswersProvided(submittedPlayerAnswers.size() == numOfPossibleAnswers);
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
	
	@RequestMapping(value = "/strike", method = RequestMethod.POST, consumes="application/json")
	public void strike(@RequestBody StrikeRequest request, BindingResult bindingResult, ModelMap model) {

		GameRoom gameRoom = gameRoomManager.getGameRoomById(request.getRoomId());
		if(gameRoom != null)
		{
			strike(request.getRoomId(), request.getSubmittedPlayer(), request.getQuestionId(), request.getCurrentQuestionIdx(), gameRoom);
		}
	}
	
	@RequestMapping(value = "/{roomId}/join", method = RequestMethod.POST, consumes="application/json")
	public BaseResult joinGame(@PathVariable String roomId, @RequestParam String username) {
		
		BaseResult result = new BaseResult();
		GameRoom gameRoom = gameRoomManager.getWaitingGameById(roomId);
		if(gameRoom != null)
		{
			// An available game room is found, add the player to the room as player2
			Player player = new Player(null, username);
        	GameRoom activeRoom = gameRoomManager.createActiveGameRoom(gameRoom, player);
        	
            // Notify both players in the room about the game start
        	messagingTemplate.convertAndSend("/topic/game/start/" + activeRoom.getPlayer1().getUsername(), gameRoom.getRoomId());
            messagingTemplate.convertAndSend("/topic/game/start/" + activeRoom.getPlayer2().getUsername(), gameRoom.getRoomId());
            
            result.setStatus(1);
		}
		else
		{
			result.setStatus(0);
			result.setErrorMessage("Game room is invalid!");
		}
		
		return result;
	}

	@RequestMapping(value = "/generateBotAnswer", method = RequestMethod.POST, consumes="application/json")
	public BotAnswerResult generateBotAnswer(@RequestBody BotAnswerRequest request, BindingResult bindingResult, ModelMap model) {
		
		BotAnswerResult result = new BotAnswerResult();
		
		Question question = xmlConfigurationManager.getAppConfigurationBean().getQuestionsMap().get(request.getQuestionId());
		List<String> answers = question.getAnswers();
		String suggestedAnswer = null;
		for(String answer : answers)
		{
			if((request.getPlayer2Answers() != null && request.getPlayer2Answers().contains(answer)) || (request.getPlayer1Answers() != null && request.getPlayer1Answers().contains(answer)))
			{
				continue;
			}
			else
			{
				suggestedAnswer = answer;
				break;
			}
		}
		
		result.setAnswer(suggestedAnswer);
		result.setStatus(suggestedAnswer != null ? 1 : 0);
		result.setErrorMessage("Bot failed to find a new answer!");
		
		return result;
	}
	
	@RequestMapping(value = "/cancelGame", method = RequestMethod.POST, consumes="application/json")
	public BaseResult cancelGame(@RequestParam(value = "roomId") String roomId) {
	
		BaseResult result = new BaseResult();
		
		GameRoom gameRoom = gameRoomManager.getWaitingGameById(roomId);
		if(gameRoom != null)
    	{
    		gameRoomManager.flushWaitingGame(gameRoom.getRoomId());
    		
    		for(String session : gameRoom.getPlayersSessions())
    		{
    			webSocketManager.getWaitingSessions().remove(session);
    			webSocketManager.getLastWaitPingTimes().remove(session);
    		}
    	}
		
		result.setStatus(1);
		return result;
	}
	
	private void strike(String roomId, String submittedPlayerId, String questionId, int currentQuestionIndex, GameRoom gameRoom) {
		
		Integer strikes = gameRoomManager.getPlayerStrikesByGameAndQuestion(roomId, questionId, submittedPlayerId);
		if(strikes == null || strikes < 2)
		{
			gameRoomManager.addPlayerStrikeToGameReport(roomId, questionId, submittedPlayerId);
			
			StrikeResult strikeResult = new StrikeResult();
			strikeResult.setSubmittedPlayer(submittedPlayerId);
			strikeResult.setCurrentTurn(submittedPlayerId.equals(gameRoom.getPlayer1().getPlayerId()) ? gameRoom.getPlayer2().getPlayerId() : gameRoom.getPlayer1().getPlayerId());
			strikeResult.setNbStrikes(strikes == null ? 1 : strikes+1);
			
			messagingTemplate.convertAndSend("/topic/game/" + roomId + "/strike", strikeResult);
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
			
			addPointAndProceed(gameRoom, questionId, currentQuestionIndex, playerId);
		}
	}
	
	private void addPointAndProceed(GameRoom gameRoom, String questionId, int currentQuestionIndex, String playerId)
	{
		String roomId = gameRoom.getRoomId();
		
		gameRoomManager.addPlayerScoreToGameReport(roomId, playerId, questionId);
		
		// open new question or complete game
		if(currentQuestionIndex == 10)
		{
			// complete the game
			GameReportResult gameReportResult = gameRoomManager.generateGameReportResult(roomId);
			gameRoomManager.flushGame(roomId);
			messagingTemplate.convertAndSend("/topic/game/" + roomId + "/complete", gameReportResult);
		}
		else
		{
			// open new question
			Map<String, Integer> playersScores = gameRoomManager.getPlayersScoresPerGame(roomId);
			messagingTemplate.convertAndSend("/topic/game/"+roomId+"/updateScore", playersScores);
			messagingTemplate.convertAndSend("/topic/game/"+roomId+"/newQuestion", currentQuestionIndex + 1);
		}
	}
	
	/*private static boolean isAnswerMatching(List<String> answers, String capturedAnswer, List<String> opponentPlayerAnswers, List<String> submittedPlayerAnswers, int minimumMatchingRatio)
	{
		boolean answerMatch = false;
		String matchingAnswer = null;
		int highestMatchingRatio = 0;
		for(String answer : answers)
		{
			if((opponentPlayerAnswers != null && opponentPlayerAnswers.contains(answer)) || (submittedPlayerAnswers != null && submittedPlayerAnswers.contains(answer)))
			{
				continue;
			}
			else
			{
				int matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), answer.toLowerCase());
				if(matchingRatio >= minimumMatchingRatio)
				{
					if(highestMatchingRatio < matchingRatio)
					{
						highestMatchingRatio = matchingRatio;
						matchingAnswer = answer;
					}
					answerMatch = true;
					//gameRoomManager.addSuccessfulAnswerToGameReport(request.getRoomId(), request.getQuestionId(), matchingAnswer, request.getSubmittedPlayerId());
				}
			}
		}
		
		System.out.println("The matching answer is: " + matchingAnswer);
		return answerMatch;
	}*/
	
	/*public static void main(String[] args) {
		
		
		List<String> answers = new ArrayList<String>();
		answers.add("Lionel Messi");
		answers.add("Cristiano Ronaldo");
		answers.add("Pele");
		answers.add("Didier Drogba");
		answers.add("Al Ahly");
		answers.add("Manchester City");
		answers.add("Manchester United");
		answers.add("Sun Di Jong");
		answers.add("De Yong");
		
		String capturedAnswer = "Yong";
		
		boolean isAnswerMatch = isAnswerMatching(answers, capturedAnswer, null, null, 53);
		System.out.println(isAnswerMatch ? "Correct Answer" : "Incorrect Answer");
	}*/
	
	private String transliterateAnswer(String capturedAnswer)
	{
		String LANGUAGE_COMBINATION_NO_ACCENTS = "Any-Eng; nfd; [:nonspacing mark:] remove; nfc";
		Transliterator transliterator = Transliterator.getInstance(LANGUAGE_COMBINATION_NO_ACCENTS);
		
		return transliterator.transliterate(capturedAnswer);
	}

}
