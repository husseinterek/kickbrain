package com.kickbrain.beans;

import java.util.List;

public class QuestionAdminResult {

	private int id;
	private String descriptionAr;
	private String descriptionEn;
	private List<AnswerVO> answers;
	private int category;
	private int tag;
	private String answersAr;
	private String answersEn;

	public String getDescriptionAr() {
		return descriptionAr;
	}

	public void setDescriptionAr(String descriptionAr) {
		this.descriptionAr = descriptionAr;
	}

	public String getDescriptionEn() {
		return descriptionEn;
	}

	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}

	public List<AnswerVO> getAnswers() {
		return answers;
	}

	public void setAnswers(List<AnswerVO> answers) {
		this.answers = answers;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAnswersAr() {
		return answersAr;
	}

	public void setAnswersAr(String answersAr) {
		this.answersAr = answersAr;
	}

	public String getAnswersEn() {
		return answersEn;
	}

	public void setAnswersEn(String answersEn) {
		this.answersEn = answersEn;
	}

}
