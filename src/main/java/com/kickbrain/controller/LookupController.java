package com.kickbrain.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kickbrain.beans.ChallengeLookup;
import com.kickbrain.beans.GeneralLookup;

@RestController
@RequestMapping("/lookup")
public class LookupController {
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private Environment env;
	
	@RequestMapping(value = "/dictionary", method = RequestMethod.GET)
	public GeneralLookup dictionary(HttpSession session) {
		
		GeneralLookup result = new GeneralLookup();
		
		Map<String, Map<String, String>> homePageTranslations = new HashMap<String, Map<String, String>>();
		
		// Populate home page dictionary - English
		Map<String, String> homePageDictionaryEn = new HashMap<String, String>();
		homePageDictionaryEn.put("title", messageSource.getMessage("home.title", null, Locale.forLanguageTag("en")));
		homePageDictionaryEn.put("subTitle", messageSource.getMessage("home.subtitle", null, Locale.forLanguageTag("en")));
		
		// Populate home page dictionary - Arabic
		Map<String, String> homePageDictionaryAr = new HashMap<String, String>();
		homePageDictionaryAr.put("title", messageSource.getMessage("home.title", null, Locale.forLanguageTag("ar")));
		homePageDictionaryAr.put("subTitle", messageSource.getMessage("home.subtitle", null, Locale.forLanguageTag("ar")));
		
		homePageTranslations.put("en", homePageDictionaryEn);
		homePageTranslations.put("ar", homePageDictionaryAr);
		
		result.setHomePageTranslations(homePageTranslations);
		
		List<ChallengeLookup> challenges = new ArrayList<ChallengeLookup>();
		
		ChallengeLookup challenge = new ChallengeLookup();
		challenge.setId(1);
		challenge.setActive(true);
		challenge.setTitleAr(messageSource.getMessage("home.challenge1Title", null, Locale.forLanguageTag("ar")));
		challenge.setTitleEn(messageSource.getMessage("home.challenge1Title", null, Locale.forLanguageTag("en")));
		
		List<String> challenge1RulesAr = new ArrayList<String>();
		challenge1RulesAr.add(messageSource.getMessage("home.challenge1Rule1", null, Locale.forLanguageTag("ar")));
		challenge1RulesAr.add(messageSource.getMessage("home.challenge1Rule2", null, Locale.forLanguageTag("ar")));
		challenge1RulesAr.add(messageSource.getMessage("home.challenge1Rule3", null, Locale.forLanguageTag("ar")));
		challenge1RulesAr.add(messageSource.getMessage("home.challenge1Rule4", null, Locale.forLanguageTag("ar")));
		challenge1RulesAr.add(messageSource.getMessage("home.challenge1Rule5", null, Locale.forLanguageTag("ar")));
		
		List<String> challenge1RulesEn = new ArrayList<String>();
		challenge1RulesEn.add(messageSource.getMessage("home.challenge1Rule1", null, Locale.forLanguageTag("en")));
		challenge1RulesEn.add(messageSource.getMessage("home.challenge1Rule2", null, Locale.forLanguageTag("en")));
		challenge1RulesEn.add(messageSource.getMessage("home.challenge1Rule3", null, Locale.forLanguageTag("en")));
		challenge1RulesEn.add(messageSource.getMessage("home.challenge1Rule4", null, Locale.forLanguageTag("en")));
		challenge1RulesEn.add(messageSource.getMessage("home.challenge1Rule5", null, Locale.forLanguageTag("en")));
		
		Map<String, List<String>> challenge1Rules = new HashMap<String, List<String>>();
		challenge1Rules.put("ar", challenge1RulesAr);
		challenge1Rules.put("en", challenge1RulesEn);
		
		challenge.setRules(challenge1Rules);
		challenges.add(challenge);
		
		ChallengeLookup challenge2 = new ChallengeLookup();
		challenge2.setId(2);
		challenge2.setTitleAr(messageSource.getMessage("home.challenge2Title", null, Locale.forLanguageTag("ar")));
		challenge2.setTitleEn(messageSource.getMessage("home.challenge2Title", null, Locale.forLanguageTag("en")));
		
		List<String> challenge2RulesAr = new ArrayList<String>();
		challenge2RulesAr.add(messageSource.getMessage("home.challenge2Rule1", null, Locale.forLanguageTag("ar")));
		challenge2RulesAr.add(messageSource.getMessage("home.challenge2Rule2", null, Locale.forLanguageTag("ar")));
		challenge2RulesAr.add(messageSource.getMessage("home.challenge2Rule3", null, Locale.forLanguageTag("ar")));
		challenge2RulesAr.add(messageSource.getMessage("home.challenge2Rule4", null, Locale.forLanguageTag("ar")));
		challenge2RulesAr.add(messageSource.getMessage("home.challenge2Rule5", null, Locale.forLanguageTag("ar")));
		
		List<String> challenge2RulesEn = new ArrayList<String>();
		challenge2RulesEn.add(messageSource.getMessage("home.challenge2Rule1", null, Locale.forLanguageTag("en")));
		challenge2RulesEn.add(messageSource.getMessage("home.challenge2Rule2", null, Locale.forLanguageTag("en")));
		challenge2RulesEn.add(messageSource.getMessage("home.challenge2Rule3", null, Locale.forLanguageTag("en")));
		challenge2RulesEn.add(messageSource.getMessage("home.challenge2Rule4", null, Locale.forLanguageTag("en")));
		challenge2RulesEn.add(messageSource.getMessage("home.challenge2Rule5", null, Locale.forLanguageTag("en")));
		
		Map<String, List<String>> challenge2Rules = new HashMap<String, List<String>>();
		challenge2Rules.put("ar", challenge2RulesAr);
		challenge2Rules.put("en", challenge2RulesEn);
		
		challenge2.setRules(challenge2Rules);
		challenges.add(challenge2);
		
		Map<String, String> flags = new HashMap<String, String>();
		String defaultLanguage = String.valueOf(env.getProperty("home.defaultLanguage"));
		flags.put("defaultLanguage", defaultLanguage);
		
		String latestiOSVersion = String.valueOf(env.getProperty("home.latestiOSVersion"));
		flags.put("latestiOSVersion", latestiOSVersion);
		
		result.setFlags(flags);
		
		result.setChallenges(challenges);
		result.setStatus(1);
		
		return result;
	}
}