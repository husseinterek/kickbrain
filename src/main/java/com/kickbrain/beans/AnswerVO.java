package com.kickbrain.beans;

public class AnswerVO {

	private int id;
	private String answerEn;
	private String answerAr;
	private int questionId;
	private int answerId;
	private int type;

	public String getAnswerEn() {
		return answerEn;
	}

	public void setAnswerEn(String answerEn) {
		this.answerEn = answerEn;
	}

	public String getAnswerAr() {
		return answerAr;
	}

	public void setAnswerAr(String answerAr) {
		this.answerAr = answerAr;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAnswerId() {
		return answerId;
	}

	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
