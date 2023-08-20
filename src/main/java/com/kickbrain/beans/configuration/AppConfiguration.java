package com.kickbrain.beans.configuration;

import java.util.List;
import java.util.Map;

public class AppConfiguration {

	private Map<Integer, Question> questionsMap;
	private List<Question> questions;

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public Map<Integer, Question> getQuestionsMap() {
		return questionsMap;
	}

	public void setQuestionsMap(Map<Integer, Question> questionsMap) {
		this.questionsMap = questionsMap;
	}

}
