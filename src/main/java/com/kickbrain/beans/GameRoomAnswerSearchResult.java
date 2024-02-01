package com.kickbrain.beans;

import java.util.List;

public class GameRoomAnswerSearchResult extends BaseResult{

	private List<GameRoomAnswerSearch> answers;

	public List<GameRoomAnswerSearch> getAnswers() {
		return answers;
	}

	public void setAnswers(List<GameRoomAnswerSearch> answers) {
		this.answers = answers;
	}

}
