package com.kickbrain.db.service;

import java.util.List;

import com.kickbrain.beans.AnswerVO;
import com.kickbrain.beans.configuration.QuestionVO;

public interface QuestionService {

	public List<QuestionVO> retrieveRandomQuestions(int category, int limit);
	public List<AnswerVO> retrieveQuestionAnswers(int questionId);
	public QuestionVO addQuestion(QuestionVO question);
	public AnswerVO addAnswer(AnswerVO answer);
	public QuestionVO importQuestion(QuestionVO questionVO);
	public void flushQuestionImports();
	public List<QuestionVO> retrieveUnPlayedQuestionsForPlayer(int playerId, int category);
	public List<QuestionVO> retrieveLeastPlayedQuestionsForPlayer(int playerId, int category, int limit);
	public List<QuestionVO> retrievePlayedQuestionsForPlayer(int playerId, int category, int limit);
	public void removeQuestionAnswers(int questionId);
	public List<QuestionVO> retrieveAllQuestions(String search, Integer offset, Integer max);
	public List<AnswerVO> retrieveAllAnswers(String search, Integer offset, Integer max);
}
