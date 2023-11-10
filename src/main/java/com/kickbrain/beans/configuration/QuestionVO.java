package com.kickbrain.beans.configuration;

import java.util.List;

import com.kickbrain.beans.AnswerVO;

public class QuestionVO {

	private int id;
	private String promptEn;
	private String promptAr;
	private int category;
	private List<AnswerVO> answers;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPromptEn() {
		return promptEn;
	}

	public void setPromptEn(String promptEn) {
		this.promptEn = promptEn;
	}

	public String getPromptAr() {
		return promptAr;
	}

	public void setPromptAr(String promptAr) {
		this.promptAr = promptAr;
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

}
