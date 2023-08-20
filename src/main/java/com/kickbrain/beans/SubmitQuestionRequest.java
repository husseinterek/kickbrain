package com.kickbrain.beans;

public class SubmitQuestionRequest {

	private String questionEn;
	private String questionAr;
	private String answers;

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

	public String getAnswers() {
		return answers;
	}

	public void setAnswers(String answers) {
		this.answers = answers;
	}

}
