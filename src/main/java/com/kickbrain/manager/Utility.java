package com.kickbrain.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kickbrain.beans.AnswerVO;
import com.kickbrain.beans.GameDetailsVO;
import com.kickbrain.beans.GameVO;
import com.kickbrain.beans.Player;
import com.kickbrain.beans.PremiumPointsHistoryVO;
import com.kickbrain.beans.UserVO;
import com.kickbrain.beans.WaitingGameVO;
import com.kickbrain.beans.configuration.QuestionVO;
import com.kickbrain.db.model.Answers;
import com.kickbrain.db.model.Game;
import com.kickbrain.db.model.GameDetails;
import com.kickbrain.db.model.PremiumPointsHistory;
import com.kickbrain.db.model.Question;
import com.kickbrain.db.model.QuestionAnswers;
import com.kickbrain.db.model.QuestionAnswersImport;
import com.kickbrain.db.model.QuestionImport;
import com.kickbrain.db.model.User;
import com.kickbrain.db.model.WaitingGame;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class Utility {

	public static UserVO convertUserModelToVO(User user)
	{
		UserVO userVO = new UserVO();
		userVO.setId(user.getId());
		userVO.setFirstName(user.getFirstName());
		userVO.setLastName(user.getLastName());
		userVO.setUsername(user.getUsername());
		userVO.setCreationDate(user.getCreationDate());
		userVO.setTotalScore(user.getTotalScore());
		userVO.setPremiumPoints(user.getPremiumPoints());
		userVO.setReferredBy(user.getReferredBy());
		
		return userVO;
	}
	
	public static User convertUserVOToModel(UserVO userVO)
	{
		User user = new User();
		user.setId(userVO.getId());
		user.setFirstName(userVO.getFirstName());
		user.setLastName(userVO.getLastName());
		user.setUsername(userVO.getUsername());
		user.setCreationDate(userVO.getCreationDate());
		user.setTotalScore(userVO.getTotalScore());
		user.setPremiumPoints(userVO.getPremiumPoints());
		user.setReferredBy(userVO.getReferredBy());
		
		return user;
	}
	
	public static QuestionVO convertQuestionModelToVO(Question question)
	{
		QuestionVO questionVO = new QuestionVO();
		questionVO.setId((int)question.getId());
		questionVO.setPromptAr(question.getDescriptionAr());
		questionVO.setPromptEn(question.getDescriptionEn());
		questionVO.setCategory(question.getCategoryId());
		questionVO.setTag(question.getTag());
		
		List<AnswerVO> answers = new ArrayList<AnswerVO>();
		List<QuestionAnswers> questionAnswersLst = question.getAnswers();
		if(questionAnswersLst != null)
		{
			for(QuestionAnswers questionAnswers : questionAnswersLst)
			{
				AnswerVO answer = convertQuestionAnswerModelToVO(questionAnswers);
				answers.add(answer);
			}
			
			questionVO.setAnswers(answers);
		}

		return questionVO;
	}
	
	public static AnswerVO convertAnswerModelToVO(Answers answer)
	{
		AnswerVO answerVO = new AnswerVO();
		answerVO.setId((int)answer.getId());
		answerVO.setAnswerAr(answer.getNameAr());
		answerVO.setAnswerEn(answer.getNameEn());
		answerVO.setType(answer.getType());
		
		return answerVO;
	}
	
	public static Answers convertAnswerVOToModel(AnswerVO answerVO)
	{
		Answers answer = new Answers();
		answer.setId(answerVO.getId());
		answer.setNameAr(answerVO.getAnswerAr());
		answer.setNameEn(answerVO.getAnswerEn());
		answer.setType(answerVO.getType());
		
		return answer;
	}
	
	public static QuestionVO convertQuestionImportModelToVO(QuestionImport question)
	{
		QuestionVO questionVO = new QuestionVO();
		questionVO.setId((int)question.getId());
		questionVO.setPromptAr(question.getDescriptionAr());
		questionVO.setPromptEn(question.getDescriptionEn());
		
		List<AnswerVO> answers = new ArrayList<AnswerVO>();
		List<QuestionAnswersImport> questionAnswersLst = question.getAnswers();
		if(questionAnswersLst != null)
		{
			for(QuestionAnswersImport questionAnswers : questionAnswersLst)
			{
				AnswerVO answer = convertQuestionAnswerImportModelToVO(questionAnswers);
				answers.add(answer);
			}
			
			questionVO.setAnswers(answers);
		}

		return questionVO;
	}
	
	public static Question convertQuestionVOToModel(QuestionVO questionVO)
	{
		Question question = new Question();
		question.setId(questionVO.getId());
		question.setDescriptionAr(questionVO.getPromptAr());
		question.setDescriptionEn(questionVO.getPromptEn());
		question.setTag(questionVO.getTag());
		question.setCategoryId(questionVO.getCategory());
		
		if(questionVO.getAnswers() != null)
		{
			List<QuestionAnswers> questionAnswersLst = new ArrayList<QuestionAnswers>();
			List<AnswerVO> answers = questionVO.getAnswers();
			for(AnswerVO answer : answers)
			{
				QuestionAnswers questionAnswers = convertAnswerVOToQuestionAnswer(answer);
				questionAnswersLst.add(questionAnswers);
			}
			question.setAnswers(questionAnswersLst);
		}
		
		return question;
	}
	
	public static QuestionImport convertQuestionVOToImportModel(QuestionVO questionVO)
	{
		QuestionImport question = new QuestionImport();
		question.setId(questionVO.getId());
		question.setDescriptionAr(questionVO.getPromptAr());
		question.setDescriptionEn(questionVO.getPromptEn());
		question.setCategoryId(questionVO.getCategory());
		
		if(questionVO.getAnswers() != null)
		{
			List<QuestionAnswersImport> questionAnswersLst = new ArrayList<QuestionAnswersImport>();
			List<AnswerVO> answers = questionVO.getAnswers();
			for(AnswerVO answer : answers)
			{
				QuestionAnswersImport questionAnswers = convertAnswerVOToQuestionAnswerImport(answer);
				questionAnswersLst.add(questionAnswers);
			}
			question.setAnswers(questionAnswersLst);
		}

		return question;
	}
	
	public static Game convertGameVOToGameModel(GameVO gameVO)
	{
		Game game = new Game();
		
		User player1 = new User();
		player1.setId(gameVO.getAnonymousPlayer1() != null ? 1 : Integer.valueOf(gameVO.getPlayer1().getPlayerId()));
		game.setPlayer1(player1);
		
		if(gameVO.getPlayer2() != null)
		{
			User player2 = new User();
			player2.setId(gameVO.getAnonymousPlayer2() != null ? 1 : Integer.valueOf(gameVO.getPlayer2().getPlayerId()));
			game.setPlayer2(player2);
		}
		
		game.setType(gameVO.getType());
		game.setCreationDate(new Date());
		
		game.setPlayer1Score(gameVO.getPlayer1Score());
		game.setPlayer2Score(gameVO.getPlayer2Score());
		
		game.setAnonymousPlayer1(gameVO.getAnonymousPlayer1());
		game.setAnonymousPlayer2(gameVO.getAnonymousPlayer2());
		
		List<GameDetails> gameDetailsLst = new ArrayList<GameDetails>();
		
		List<GameDetailsVO> gameDetailsVOLst = gameVO.getGameDetails();
		for(GameDetailsVO gameDetailsVO : gameDetailsVOLst)
		{
			GameDetails gameDetails = new GameDetails();
			gameDetails.setGame(game);
			gameDetails.setQuestion(Utility.convertQuestionVOToModel(gameDetailsVO.getQuestion()));
			gameDetails.setPlayer1Score(gameDetailsVO.getPlayer1Score());
			gameDetails.setPlayer2Score(gameDetailsVO.getPlayer2Score());
			gameDetails.setWinnerId(gameDetailsVO.getWinnerId());
			
			gameDetailsLst.add(gameDetails);
		}
		game.setGameDetails(gameDetailsLst);
		
		return game;
	}
	
	public static GameVO convertGameModelToGameVO(Game game)
	{
		GameVO gameVO = new GameVO();
		
		gameVO.setId(game.getId());
		
		String player1Username = game.getAnonymousPlayer1() != null ? game.getAnonymousPlayer1() : (game.getPlayer1().getFirstName() + " " + game.getPlayer1().getLastName());
		Player player1 = new Player(String.valueOf(game.getPlayer1().getId()), player1Username);
		gameVO.setPlayer1(player1);
		
		if(game.getPlayer2() != null)
		{
			String player2Username = game.getAnonymousPlayer2() != null ? game.getAnonymousPlayer2() : (game.getPlayer2().getFirstName() + " " + game.getPlayer2().getLastName());
			Player player2 = new Player(String.valueOf(game.getPlayer2().getId()), player2Username);
			gameVO.setPlayer2(player2);
		}
		
		gameVO.setCreationDate(game.getCreationDate());
		gameVO.setType(game.getType());
		gameVO.setPlayer1Score(game.getPlayer1Score());
		gameVO.setPlayer2Score(game.getPlayer2Score());
		
		List<GameDetailsVO> gameDetailsVOLst = new ArrayList<GameDetailsVO>();
		
		List<GameDetails> gameDetailsLst = game.getGameDetails();
		for(GameDetails gameDetails : gameDetailsLst)
		{
			GameDetailsVO gameDetailsVO = new GameDetailsVO();
			gameDetailsVO.setGameId((int) gameDetails.getGame().getId());
			gameDetailsVO.setPlayer1Score(gameDetails.getPlayer1Score());
			gameDetailsVO.setPlayer2Score(gameDetails.getPlayer2Score());
			gameDetailsVO.setQuestion(Utility.convertQuestionModelToVO(gameDetails.getQuestion()));
			gameDetailsVO.setWinnerId(gameDetails.getWinnerId());
			
			gameDetailsVOLst.add(gameDetailsVO);
		}
		gameVO.setGameDetails(gameDetailsVOLst);
		
		return gameVO;
	}
	
	public static AnswerVO convertQuestionAnswerModelToVO(QuestionAnswers questionAnswers)
	{
		AnswerVO answer = new AnswerVO();
		answer.setId((int)questionAnswers.getId());
		answer.setAnswerAr(questionAnswers.getAnswer().getNameAr());
		answer.setAnswerEn(questionAnswers.getAnswer().getNameEn());
		answer.setQuestionId((int) questionAnswers.getQuestion().getId());
		answer.setAnswerId((int)questionAnswers.getAnswer().getId());
		
		return answer;
	}
	
	public static AnswerVO convertQuestionAnswerImportModelToVO(QuestionAnswersImport questionAnswers)
	{
		AnswerVO answer = new AnswerVO();
		answer.setId((int)questionAnswers.getId());
		answer.setAnswerAr(questionAnswers.getAnswerAr());
		answer.setAnswerEn(questionAnswers.getAnswerEn());
		answer.setQuestionId((int) questionAnswers.getQuestion().getId());
		
		return answer;
	}
	
	public static QuestionAnswers convertAnswerVOToQuestionAnswer(AnswerVO answer)
	{
		QuestionAnswers questionAnswers = new QuestionAnswers();
		questionAnswers.setId(answer.getId());
		questionAnswers.setAnswerAr(answer.getAnswerAr());
		questionAnswers.setAnswerEn(answer.getAnswerEn());
		
		Answers answerModel = new Answers();
		answerModel.setId(answer.getAnswerId());
		questionAnswers.setAnswer(answerModel);
		
		Question question = new Question();
		question.setId(answer.getQuestionId());
		questionAnswers.setQuestion(question);
		
		return questionAnswers;
	}
	
	public static QuestionAnswersImport convertAnswerVOToQuestionAnswerImport(AnswerVO answer)
	{
		QuestionAnswersImport questionAnswers = new QuestionAnswersImport();
		questionAnswers.setId(answer.getId());
		questionAnswers.setAnswerAr(answer.getAnswerAr());
		questionAnswers.setAnswerEn(answer.getAnswerEn());
		
		QuestionImport question = new QuestionImport();
		question.setId(answer.getQuestionId());
		questionAnswers.setQuestion(question);
		
		return questionAnswers;
	}
	
	public static boolean isArabicText(String s) {
	    for (int i = 0; i < s.length();) {
	        int c = s.codePointAt(i);
	        if (c >= 0x0600 && c <= 0x06E0)
	            return true;
	        i += Character.charCount(c);            
	    }
	    return false;
	}
	
	public static WaitingGameVO convertWaitingGameModelToVO(WaitingGame waitingGame)
	{
		WaitingGameVO waitingGameVO = new WaitingGameVO();
		waitingGameVO.setId(waitingGame.getId());
		
		User user = waitingGame.getPlayer();
		Player player = new Player(String.valueOf(user.getId()), user.getUsername());
		player.setAnonymous(user.getId() == 1);
		player.setFirstName(user.getFirstName());
		player.setLastName(user.getLastName());
		player.setTotalScore(user.getTotalScore());
		
		waitingGameVO.setPlayer(player);
		waitingGameVO.setAnonymousPlayer(waitingGame.getAnonymousPlayer());
		waitingGameVO.setCreationDate(waitingGame.getCreationDate());
		waitingGameVO.setStatus(waitingGame.getStatus());
		waitingGameVO.setSessionId(waitingGame.getSessionId());
		waitingGameVO.setDeviceToken(waitingGame.getDeviceToken());
		waitingGameVO.setIsPrivate(waitingGame.getIsPrivate());
		waitingGameVO.setPasscode(waitingGame.getPasscode());
		
		return waitingGameVO;
	}
	
	public static WaitingGame convertWaitingGameVOToModel(WaitingGameVO waitingGameVO)
	{
		WaitingGame waitingGame = new WaitingGame();
		waitingGame.setId(waitingGameVO.getId());
		waitingGame.setCreationDate(waitingGameVO.getCreationDate());
		waitingGame.setAnonymousPlayer(waitingGameVO.getAnonymousPlayer());
		
		Player player = waitingGameVO.getPlayer();
		User user = new User();
		user.setId(player.getPlayerId() == null ? 1 : Long.valueOf(player.getPlayerId()));
		user.setUsername(player.getUsername());
		
		waitingGame.setPlayer(user);
		waitingGame.setStatus(waitingGameVO.getStatus());
		waitingGame.setSessionId(waitingGameVO.getSessionId());
		waitingGame.setDeviceToken(waitingGameVO.getDeviceToken());
		waitingGame.setIsPrivate(waitingGameVO.getIsPrivate());
		waitingGame.setPasscode(waitingGameVO.getPasscode());
		
		return waitingGame;
	}
	
	public static AnswerVO getMatchingAnswerV2(String capturedAnswer, List<AnswerVO> possibleAnswers, List<AnswerVO> submittedPlayerAnswers, List<AnswerVO> opponentPlayerAnswers, int minimumMatchingRatio, int fullMatchRatio, int partMatchRatio)
	{
		//System.out.println("Checking for a match of captured answer: " + capturedAnswer);
		AnswerVO result = null;
		List<AnswerVO> matchingAnswers = new ArrayList<>();
		
		int highestMatchingRatio = 0;
		boolean isNumericAnswer = StringUtils.isNumeric(capturedAnswer);
		boolean isArabicAnswer = Utility.isArabicText(capturedAnswer);
		
		for(AnswerVO answerVO : possibleAnswers)
		{
			String possibleAnswer = isArabicAnswer ? answerVO.getAnswerAr() : StringUtils.stripAccents(answerVO.getAnswerEn());
			if(isArabicAnswer)
			{
				capturedAnswer = capturedAnswer.replaceAll("أ", "ا");
				capturedAnswer = capturedAnswer.replaceAll("إ", "ا");
				capturedAnswer = capturedAnswer.replaceAll("آ", "ا");
				possibleAnswer = possibleAnswer.replaceAll("أ", "ا");
				possibleAnswer = possibleAnswer.replaceAll("إ", "ا");
				possibleAnswer = possibleAnswer.replaceAll("آ", "ا");
				
				if(possibleAnswer.startsWith("ال"))
				{
					possibleAnswer = possibleAnswer.replaceFirst("ال", "");
				}
				
				if(capturedAnswer.startsWith("ال"))
				{
					capturedAnswer = capturedAnswer.replaceFirst("ال", "");
				}
			}
			
			if(isNumericAnswer)
			{
				if(capturedAnswer.equals(possibleAnswer))
				{
					matchingAnswers.add(answerVO);
					break;
				}
			}
			else
			{
				int matchingRatio = 0;
				String capturedAnswerArr[] = capturedAnswer.split("\\s+");
				String storedAnswerArr[] = possibleAnswer.split("\\s+");
				if(capturedAnswerArr.length > 1)
				{
					// 1st step match (Full name)
					if((capturedAnswerArr.length >= storedAnswerArr.length))
					{
						matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), possibleAnswer.toLowerCase());
						
						if(matchingRatio >= fullMatchRatio)
						{
							if(highestMatchingRatio <= matchingRatio)
							{
								highestMatchingRatio = matchingRatio;
								matchingAnswers.add(answerVO);
							}
						}
					}
					else
					{
						int capturedAnsLength = capturedAnswerArr.length;
						// If the stored answer is larger than the captured answer, then we compare the tokens of the captured answer by same tokens of stored answer
						for(int i=0; i<storedAnswerArr.length; i++)
						{
							String token = "";
							for(int j=0; j < capturedAnsLength; j++)
							{
								if((i+j) < storedAnswerArr.length)
								{
									if(StringUtils.isNoneEmpty(token))
									{
										token += " ";
									}
									token += storedAnswerArr[i+j];
								}
							}
							
							matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), token.toLowerCase());
							if(matchingRatio >= fullMatchRatio)
							{
								if(highestMatchingRatio <= matchingRatio)
								{
									highestMatchingRatio = matchingRatio;
									matchingAnswers.add(answerVO);
								}
							}
						}
					}
				}
				else
				{
					/*if(storedAnswerArr.length <= 2)
					{*/
						// if the captured answer is one token and the possible answer is 2 tokens, then the captured answer should be minimum 3 characters
						int capturedAnswerLength = capturedAnswer.length();
						if(capturedAnswerLength >= 3)
						{
							for(String storedAnswer : storedAnswerArr)
							{
								matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), storedAnswer.toLowerCase());
								
								if(matchingRatio >= partMatchRatio)
								{
									if(highestMatchingRatio <= matchingRatio)
									{
										highestMatchingRatio = matchingRatio;
										matchingAnswers.add(answerVO);
									}
								}
							}
						}
					/*}
					else
					{
						// if the captured answer is one token and the possible answer is more than 2 tokens, then the captured answer length should be minimum 50% of the total length of the stored answer
						float lengthPercentage = (float)capturedAnswer.length() / (float)(possibleAnswer.length());
						if(lengthPercentage >= 0.5)
						{
							for(String storedAnswer : storedAnswerArr)
							{
								matchingRatio = FuzzySearch.ratio(capturedAnswer.toLowerCase(), storedAnswer.toLowerCase());
								
								if(matchingRatio >= partMatchRatio)
								{
									if(highestMatchingRatio <= matchingRatio)
									{
										highestMatchingRatio = matchingRatio;
										matchingAnswers.add(answerVO);
									}
								}
							}
						}
					}*/
				}
			}
		}
		
		// Check if the matching answer is already answered by the submitted player or the opponent player
		List<AnswerVO> matchingAnswersUpdatedLst = new ArrayList<AnswerVO>();
		if(!matchingAnswers.isEmpty())
		{
			for(AnswerVO matchingAnswerVO : matchingAnswers)
			{
				if(opponentPlayerAnswers != null)
				{
					for(AnswerVO oppoAnswerVO : opponentPlayerAnswers)
					{
						if(matchingAnswerVO.getId() == oppoAnswerVO.getId())
						{
							// Answer already answered
							matchingAnswerVO = null;
							break;
						}
					}
				}
				
				if(matchingAnswerVO != null && submittedPlayerAnswers != null)
				{
					for(AnswerVO subAnswerVO : submittedPlayerAnswers)
					{
						if(matchingAnswerVO.getId() == subAnswerVO.getId())
						{
							matchingAnswerVO = null;
							break;
						}
					}
				}
				
				if(matchingAnswerVO != null)
				{
					matchingAnswersUpdatedLst.add(matchingAnswerVO);
				}
			}
		}
		
		result = matchingAnswersUpdatedLst.size() > 0 ? matchingAnswersUpdatedLst.get(0) : null;
		return result;
	}
	
	public static PremiumPointsHistory convertPremiumPointsHistoryVOToModel(PremiumPointsHistoryVO premiumPointsHistoryVO)
	{
		PremiumPointsHistory premiumPointsHistory = new PremiumPointsHistory();
		premiumPointsHistory.setPlayerId(premiumPointsHistoryVO.getPlayerId());
		premiumPointsHistory.setGameId(premiumPointsHistoryVO.getGameId());
		premiumPointsHistory.setScorePoints(premiumPointsHistoryVO.getScorePoints());
		premiumPointsHistory.setPremiumPoints(premiumPointsHistoryVO.getPremiumPoints());
		premiumPointsHistory.setConversionRatio(premiumPointsHistoryVO.getConversionRatio());
		premiumPointsHistory.setCreationDate(premiumPointsHistoryVO.getCreationDate());
		
		return premiumPointsHistory;
	}
	
	public static PremiumPointsHistoryVO convertPremiumPointsHistoryModelToVO(PremiumPointsHistory premiumPointsHistory)
	{
		PremiumPointsHistoryVO premiumPointsHistoryVO = new PremiumPointsHistoryVO();
		premiumPointsHistoryVO.setId(premiumPointsHistory.getId());
		premiumPointsHistoryVO.setPlayerId(premiumPointsHistory.getPlayerId());
		premiumPointsHistoryVO.setGameId(premiumPointsHistory.getGameId());
		premiumPointsHistoryVO.setScorePoints(premiumPointsHistory.getScorePoints());
		premiumPointsHistoryVO.setPremiumPoints(premiumPointsHistory.getPremiumPoints());
		premiumPointsHistoryVO.setConversionRatio(premiumPointsHistory.getConversionRatio());
		premiumPointsHistoryVO.setCreationDate(premiumPointsHistory.getCreationDate());
		
		return premiumPointsHistoryVO;
	}
	
	public static void main(String[] args) {
		
		String capturedAnswer = "sévilla";
		
		List<AnswerVO> possibleAnswers = new ArrayList<AnswerVO>();
		AnswerVO answer = new AnswerVO();
		answer.setAnswerAr("اشبيلية");
		answer.setAnswerEn("Sevilla");
		possibleAnswers.add(answer);
		
		AnswerVO matchedAnswer = getMatchingAnswerV2(capturedAnswer, possibleAnswers, null, null, 55, 70, 75);
		System.out.println(matchedAnswer.getAnswerEn());
	}
	
}
