package com.kickbrain.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralLookup extends BaseResult {

	private Map<String, Map<String, String>> homePageTranslations = new HashMap<String, Map<String,String>>();
	private Map<String, Map<String, String>> gameRoomTranslations = new HashMap<String, Map<String,String>>();
	private Map<String, String> flags = new HashMap<String, String>();
	private List<ChallengeLookup> challenges = new ArrayList<ChallengeLookup>();

	public Map<String, String> getFlags() {
		return flags;
	}

	public void setFlags(Map<String, String> flags) {
		this.flags = flags;
	}

	public Map<String, Map<String, String>> getHomePageTranslations() {
		return homePageTranslations;
	}

	public void setHomePageTranslations(Map<String, Map<String, String>> homePageTranslations) {
		this.homePageTranslations = homePageTranslations;
	}

	public Map<String, Map<String, String>> getGameRoomTranslations() {
		return gameRoomTranslations;
	}

	public void setGameRoomTranslations(Map<String, Map<String, String>> gameRoomTranslations) {
		this.gameRoomTranslations = gameRoomTranslations;
	}

	public List<ChallengeLookup> getChallenges() {
		return challenges;
	}

	public void setChallenges(List<ChallengeLookup> challenges) {
		this.challenges = challenges;
	}

}
