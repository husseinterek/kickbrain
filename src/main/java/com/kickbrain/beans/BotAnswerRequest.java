package com.kickbrain.beans;

import java.util.List;

public class BotAnswerRequest extends BaseResult {

	private int questionId;
	private List<String> player2Answers;
	private List<String> player1Answers;

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public List<String> getPlayer2Answers() {
		return player2Answers;
	}

	public void setPlayer2Answers(List<String> player2Answers) {
		this.player2Answers = player2Answers;
	}

	public List<String> getPlayer1Answers() {
		return player1Answers;
	}

	public void setPlayer1Answers(List<String> player1Answers) {
		this.player1Answers = player1Answers;
	}

}
