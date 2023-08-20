package com.kickbrain.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameReport {

	private String roomId;
	private Map<String, Map<String, List<String>>> playersAnswersByQuestion = new HashMap<String, Map<String, List<String>>>();
	private Map<String, Map<String, Integer>> playersStrikesByQuestion = new HashMap<String, Map<String, Integer>>();
	private Map<String, Integer> playersScore = new HashMap<String, Integer>();
	private Map<String, String> questionsResult = new HashMap<String, String>();

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Map<String, Map<String, List<String>>> getPlayersAnswersByQuestion() {
		return playersAnswersByQuestion;
	}

	public void setPlayersAnswersByQuestion(Map<String, Map<String, List<String>>> playersAnswersByQuestion) {
		this.playersAnswersByQuestion = playersAnswersByQuestion;
	}

	public Map<String, Map<String, Integer>> getPlayersStrikesByQuestion() {
		return playersStrikesByQuestion;
	}

	public void setPlayersStrikesByQuestion(Map<String, Map<String, Integer>> playersStrikesByQuestion) {
		this.playersStrikesByQuestion = playersStrikesByQuestion;
	}

	public Map<String, Integer> getPlayersScore() {
		return playersScore;
	}

	public void setPlayersScore(Map<String, Integer> playersScore) {
		this.playersScore = playersScore;
	}

	public Map<String, String> getQuestionsResult() {
		return questionsResult;
	}

	public void setQuestionsResult(Map<String, String> questionsResult) {
		this.questionsResult = questionsResult;
	}

}
