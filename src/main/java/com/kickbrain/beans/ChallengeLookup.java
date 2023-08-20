package com.kickbrain.beans;

import java.util.List;
import java.util.Map;

public class ChallengeLookup {

	private int id;
	private String titleEn;
	private String titleAr;
	private Map<String, List<String>> rules;
	private boolean active;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

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

	public Map<String, List<String>> getRules() {
		return rules;
	}

	public void setRules(Map<String, List<String>> rules) {
		this.rules = rules;
	}

}
