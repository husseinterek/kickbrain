package com.kickbrain.beans;

import java.util.List;

public class GameResult extends BaseResult {

	private List<QuestionResult> questions;

	public List<QuestionResult> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionResult> questions) {
		this.questions = questions;
	}

}
