package com.kickbrain.beans;

public class QuestionResult {

	private int id;
	private String questionEn;
	private String questionAr;
	private int possibleAnswers;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQuestionEn() {
		return questionEn;
	}

	public void setQuestionEn(String questionEn) {
		this.questionEn = questionEn;
	}

	public String getQuestionAr() {
		return questionAr;
	}

	public void setQuestionAr(String questionAr) {
		this.questionAr = questionAr;
	}

	public int getPossibleAnswers() {
		return possibleAnswers;
	}

	public void setPossibleAnswers(int possibleAnswers) {
		this.possibleAnswers = possibleAnswers;
	}

}
