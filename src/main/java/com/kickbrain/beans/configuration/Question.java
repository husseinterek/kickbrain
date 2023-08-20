package com.kickbrain.beans.configuration;

import java.util.List;

public class Question {

	private int id;
	private String promptEn;
	private String promptAr;
	private List<String> answers;

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

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

}
