package com.kickbrain.beans;

import java.util.List;

public class ChallengeBean {

	private String titleEn;
	private String titleAr;
	private int category;
	private List<QuestionResult> questions;

	public String getTitleEn() {
		return titleEn;
	}

	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}

	public String getTitleAr() {
		return titleAr;
	}

	public void setTitleAr(String titleAr) {
		this.titleAr = titleAr;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public List<QuestionResult> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionResult> questions) {
		this.questions = questions;
	}

}
