package com.kickbrain.beans;

import java.util.List;

public class ValidateSinglePlayerAnswerRequest {

	private int currentQuestionIdx;
	private String questionId;
	private String capturedAnswer;
	private String answerElementId;
	private List<String> submittedPlayerAnswers;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getCapturedAnswer() {
		return capturedAnswer;
	}

	public void setCapturedAnswer(String capturedAnswer) {
		this.capturedAnswer = capturedAnswer;
	}

	public int getCurrentQuestionIdx() {
		return currentQuestionIdx;
	}

	public void setCurrentQuestionIdx(int currentQuestionIdx) {
		this.currentQuestionIdx = currentQuestionIdx;
	}

	public String getAnswerElementId() {
		return answerElementId;
	}

	public void setAnswerElementId(String answerElementId) {
		this.answerElementId = answerElementId;
	}

	public List<String> getSubmittedPlayerAnswers() {
		return submittedPlayerAnswers;
	}

	public void setSubmittedPlayerAnswers(List<String> submittedPlayerAnswers) {
		this.submittedPlayerAnswers = submittedPlayerAnswers;
	}
}
