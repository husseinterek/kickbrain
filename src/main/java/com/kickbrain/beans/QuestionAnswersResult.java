package com.kickbrain.beans;

import java.util.List;

public class QuestionAnswersResult extends BaseResult {

	private int questionId;
	private List<AnswerVO> answers;

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public List<AnswerVO> getAnswers() {
		return answers;
	}

	public void setAnswers(List<AnswerVO> answers) {
		this.answers = answers;
	}

}
