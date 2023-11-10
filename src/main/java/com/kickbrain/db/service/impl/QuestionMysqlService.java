package com.kickbrain.db.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kickbrain.beans.AnswerVO;
import com.kickbrain.beans.configuration.QuestionVO;
import com.kickbrain.db.model.Question;
import com.kickbrain.db.model.QuestionAnswers;
import com.kickbrain.db.model.QuestionImport;
import com.kickbrain.db.repository.QuestionAnswersImportRepository;
import com.kickbrain.db.repository.QuestionImportRepository;
import com.kickbrain.db.repository.QuestionRepository;
import com.kickbrain.db.service.QuestionService;
import com.kickbrain.manager.Utility;

@Service
@Transactional
public class QuestionMysqlService implements QuestionService {

	@Autowired
	private QuestionRepository questionDao;
	
	@Autowired
	private QuestionImportRepository questionImportDao;
	
	@Autowired
	private QuestionAnswersImportRepository questionAnswersImportDao;
	
	@Override
	public List<QuestionVO> retrieveRandomQuestions(int category, int limit) {
		
		List<QuestionVO> result = new ArrayList<QuestionVO>();
		
		List<Question> questions = questionDao.findSqlQuery("SELECT * FROM QUESTIONS where category_id = "+category+" order by RAND() limit "+limit, Question.class);
		
		for(Question question : questions)
		{
			QuestionVO questionVO = Utility.convertQuestionModelToVO(question);
			result.add(questionVO);
		}
		
		return result;
	}
	
	@Override
	public List<QuestionVO> retrieveUnPlayedQuestionsForPlayer(int playerId, int category) {
		
		List<QuestionVO> result = new ArrayList<QuestionVO>();
		
		List<Question> questions = questionDao.findSqlQuery("SELECT q.*" + 
				" FROM QUESTIONS q" + 
				" LEFT JOIN (" + 
				"    SELECT DISTINCT gd.question_id" + 
				"    FROM GAME_DETAILS gd" + 
				"    INNER JOIN GAMES g ON gd.game_id = g.id" + 
				"    WHERE g.player1_id = "+playerId+" OR g.player2_id = "+playerId+"" + 
				" ) played_questions ON q.ID = played_questions.question_id" + 
				" WHERE played_questions.question_id IS NULL and q.category_id = "+ category + 
				" ORDER BY RAND()", Question.class);
		
		if(questions != null)
		{
			for(Question question : questions)
			{
				QuestionVO questionVO = Utility.convertQuestionModelToVO(question);
				result.add(questionVO);
			}
		}
		
		return result;
	}
	
	@Override
	public List<QuestionVO> retrievePlayedQuestionsForPlayer(int playerId, int category, int limit) {
		
		List<QuestionVO> result = new ArrayList<QuestionVO>();
		
		List<Question> questions = questionDao.findSqlQuery("SELECT q.*" + 
				" FROM QUESTIONS q" + 
				" JOIN (" + 
				"    SELECT DISTINCT gd.question_id" + 
				"    FROM GAME_DETAILS gd" + 
				"    INNER JOIN GAMES g ON gd.game_id = g.id" + 
				"    WHERE g.player1_id = "+playerId+" OR g.player2_id = "+playerId+"" + 
				" ) played_questions ON q.ID = played_questions.question_id" + 
				" where q.category_id = " + category +
				" ORDER BY RAND()" + 
				" limit "+limit, Question.class);
		
		if(questions != null)
		{
			for(Question question : questions)
			{
				QuestionVO questionVO = Utility.convertQuestionModelToVO(question);
				result.add(questionVO);
			}
		}
		
		return result;
	}
	
	@Override
	public List<QuestionVO> retrieveLeastPlayedQuestionsForPlayer(int playerId, int category, int limit) {
		
		List<QuestionVO> result = new ArrayList<QuestionVO>();
		
		List<Question> questions = questionDao.findSqlQuery("SELECT q.*" + 
				" FROM QUESTIONS q" + 
				" LEFT JOIN (" + 
				"    SELECT DISTINCT gd.question_id" + 
				"    FROM GAME_DETAILS gd" + 
				"    INNER JOIN GAMES g ON gd.game_id = g.id" + 
				"    WHERE g.player1_id = "+playerId+" OR g.player2_id = "+playerId+"" + 
				" ) played_questions ON q.ID = played_questions.question_id" + 
				" ORDER BY (" + 
				"    SELECT COUNT(*)" + 
				"    FROM GAME_DETAILS gd" + 
				"    INNER JOIN GAMES g ON gd.game_id = g.id" + 
				"    WHERE gd.question_id = q.ID" + 
				"    AND (g.player1_id = "+playerId+" OR g.player2_id = "+playerId+")" + 
				" ) ASC" + 
				" limit "+limit, Question.class);
		
		for(Question question : questions)
		{
			QuestionVO questionVO = Utility.convertQuestionModelToVO(question);
			result.add(questionVO);
		}
		
		return result;
	}
	
	@Override
	public List<AnswerVO> retrieveQuestionAnswers(int questionId) {
	
		Question question = questionDao.findById(Long.valueOf(questionId)).get();
		
		List<AnswerVO> answers = new ArrayList<AnswerVO>();
		List<QuestionAnswers> questionAnswersLst = question.getAnswers();
		for(QuestionAnswers questionAnswers : questionAnswersLst)
		{
			answers.add(Utility.convertQuestionAnswerModelToVO(questionAnswers));
		}
		
		return answers;
	}
	
	@Override
	public QuestionVO addQuestion(QuestionVO questionVO) {
		
		Question question = Utility.convertQuestionVOToModel(questionVO);
		question = questionDao.save(question);
		
		return Utility.convertQuestionModelToVO(question);
	}
	
	@Override
	public QuestionVO importQuestion(QuestionVO questionVO) {
		
		QuestionImport question = Utility.convertQuestionVOToImportModel(questionVO);
		question = questionImportDao.save(question);
		
		return Utility.convertQuestionImportModelToVO(question);
	}
	
	@Override
	public void flushQuestionImports() {
		
		questionImportDao.deleteAll();
		questionAnswersImportDao.deleteAll();
	}
	
}
