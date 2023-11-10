package com.kickbrain.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRoom {

	private String roomId;
	private Player player1;
	private Player player2;
	private List<ChallengeBean> challenges;
	private Map<Integer, ChallengeBean> challengesMap;
	private List<QuestionResult> questions;
	private List<String> playersSessions = new ArrayList<String>();
	private Map<Integer, List<AnswerVO>> answersByQuestion = new HashMap<Integer, List<AnswerVO>>();

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public List<QuestionResult> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionResult> questions) {
		this.questions = questions;
	}
	
	public boolean isFull()
	{
		return player1 != null && player2 != null;
	}

	public List<String> getPlayersSessions() {
		return playersSessions;
	}

	public void setPlayersSessions(List<String> playersSessions) {
		this.playersSessions = playersSessions;
	}

	public Map<Integer, List<AnswerVO>> getAnswersByQuestion() {
		return answersByQuestion;
	}

	public void setAnswersByQuestion(Map<Integer, List<AnswerVO>> answersByQuestion) {
		this.answersByQuestion = answersByQuestion;
	}

	public List<ChallengeBean> getChallenges() {
		return challenges;
	}

	public void setChallenges(List<ChallengeBean> challenges) {
		this.challenges = challenges;
	}

	public Map<Integer, ChallengeBean> getChallengesMap() {
		return challengesMap;
	}

	public void setChallengesMap(Map<Integer, ChallengeBean> challengesMap) {
		this.challengesMap = challengesMap;
	}

}