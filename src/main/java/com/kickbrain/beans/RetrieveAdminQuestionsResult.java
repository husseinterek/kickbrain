package com.kickbrain.beans;

import java.util.List;

public class RetrieveAdminQuestionsResult extends BaseResult {

	private List<QuestionAdminResult> questions;

	public List<QuestionAdminResult> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionAdminResult> questions) {
		this.questions = questions;
	}

}
