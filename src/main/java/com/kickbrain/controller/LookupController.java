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
		homePageDictionaryEn.put("loginMessage", messageSource.getMessage("login.message", null, Locale.forLanguageTag("en")));
		
		// Populate home page dictionary - Arabic
		Map<String, String> homePageDictionaryAr = new HashMap<String, String>();
		homePageDictionaryAr.put("title", messageSource.getMessage("home.title", null, Locale.forLanguageTag("ar")));
		homePageDictionaryAr.put("subTitle", messageSource.getMessage("home.subtitle", null, Locale.forLanguageTag("ar")));
		homePageDictionaryAr.put("loginMessage", messageSource.getMessage("login.message", null, Locale.forLanguageTag("ar")));
		
		homePageTranslations.put("en", homePageDictionaryEn);
		homePageTranslations.put("ar", homePageDictionaryAr);
		
		result.setHomePageTranslations(homePageTranslations);
		
		Map<String, Map<String, String>> gameRoomTranslations = new HashMap<String, Map<String, String>>();
		
		// Populate home page dictionary - English
		Map<String, String> gameRoomDictionaryEn = new HashMap<String, String>();
		gameRoomDictionaryEn.put("strike1", messageSource.getMessage("gameRoom.strike1", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("strike2", messageSource.getMessage("gameRoom.strike2", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("strike3", messageSource.getMessage("gameRoom.strike3", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("allAnswersProvided", messageSource.getMessage("gameRoom.allAnswersProvided", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("allAnswersProvidedByOpponent", messageSource.getMessage("gameRoom.allAnswersProvidedByOpponent", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("allAnswersProvidedTie", messageSource.getMessage("gameRoom.allAnswersProvidedTie", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("skipQuestionNotification", messageSource.getMessage("gameRoom.skipQuestionNotification", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("allStrikesCommitted", messageSource.getMessage("gameRoom.allStrikesCommitted", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("gameComplete", messageSource.getMessage("gameRoom.gameComplete", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("waitingMessage", messageSource.getMessage("gameRoom.waitingMessage", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("gameEnd", messageSource.getMessage("gameRoom.gameEnd", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("shareRoomLabel", messageSource.getMessage("waitingRoom.shareLabel", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("bellNoAnswer", messageSource.getMessage("gameRoom.bellNoAnswer", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("bellNoWinner", messageSource.getMessage("gameRoom.bellNoWinner", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("bellStrike", messageSource.getMessage("gameRoom.bellStrike", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("startGamePopup", messageSource.getMessage("gameRoom.startGamePopup", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("nextChallengePopup", messageSource.getMessage("gameRoom.nextChallengePopup", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("whoAmIStrike", messageSource.getMessage("gameRoom.whoAmIStrike", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("whoAmINoWinner", messageSource.getMessage("gameRoom.whoAmINoWinner", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("whoAmIOpponentWrongAnswer", messageSource.getMessage("gameRoom.whoAmIOpponentWrongAnswer", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("submitBid", messageSource.getMessage("gameRoom.submitBid", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("endBid", messageSource.getMessage("gameRoom.endBid", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("bidSuccess", messageSource.getMessage("gameRoom.bidSuccess", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("bidQuestionWinner", messageSource.getMessage("gameRoom.bidQuestionWinner", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("auctionStrike", messageSource.getMessage("gameRoom.auctionStrike", null, Locale.forLanguageTag("en")));
		gameRoomDictionaryEn.put("allBidAnswersProvidedByOpponent", messageSource.getMessage("gameRoom.allBidAnswersProvidedByOpponent", null, Locale.forLanguageTag("en")));
		
		// Populate home page dictionary - Arabic
		Map<String, String> gameRoomDictionaryAr = new HashMap<String, String>();
		gameRoomDictionaryAr.put("strike1", messageSource.getMessage("gameRoom.strike1", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("strike2", messageSource.getMessage("gameRoom.strike2", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("strike3", messageSource.getMessage("gameRoom.strike3", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("allAnswersProvided", messageSource.getMessage("gameRoom.allAnswersProvided", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("allAnswersProvidedByOpponent", messageSource.getMessage("gameRoom.allAnswersProvidedByOpponent", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("allAnswersProvidedTie", messageSource.getMessage("gameRoom.allAnswersProvidedTie", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("skipQuestionNotification", messageSource.getMessage("gameRoom.skipQuestionNotification", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("allStrikesCommitted", messageSource.getMessage("gameRoom.allStrikesCommitted", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("gameComplete", messageSource.getMessage("gameRoom.gameComplete", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("waitingMessage", messageSource.getMessage("gameRoom.waitingMessage", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("gameEnd", messageSource.getMessage("gameRoom.gameEnd", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("shareRoomLabel", messageSource.getMessage("waitingRoom.shareLabel", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("bellNoAnswer", messageSource.getMessage("gameRoom.bellNoAnswer", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("bellNoWinner", messageSource.getMessage("gameRoom.bellNoWinner", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("bellStrike", messageSource.getMessage("gameRoom.bellStrike", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("startGamePopup", messageSource.getMessage("gameRoom.startGamePopup", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("nextChallengePopup", messageSource.getMessage("gameRoom.nextChallengePopup", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("whoAmIStrike", messageSource.getMessage("gameRoom.whoAmIStrike", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("whoAmINoWinner", messageSource.getMessage("gameRoom.whoAmINoWinner", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("whoAmIOpponentWrongAnswer", messageSource.getMessage("gameRoom.whoAmIOpponentWrongAnswer", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("submitBid", messageSource.getMessage("gameRoom.submitBid", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("endBid", messageSource.getMessage("gameRoom.endBid", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("bidSuccess", messageSource.getMessage("gameRoom.bidSuccess", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("bidQuestionWinner", messageSource.getMessage("gameRoom.bidQuestionWinner", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("auctionStrike", messageSource.getMessage("gameRoom.auctionStrike", null, Locale.forLanguageTag("ar")));
		gameRoomDictionaryAr.put("allBidAnswersProvidedByOpponent", messageSource.getMessage("gameRoom.allBidAnswersProvidedByOpponent", null, Locale.forLanguageTag("ar")));
		
		gameRoomTranslations.put("en", gameRoomDictionaryEn);
		gameRoomTranslations.put("ar", gameRoomDictionaryAr);
		
		result.setGameRoomTranslations(gameRoomTranslations);
		
		List<ChallengeLookup> challenges = new ArrayList<ChallengeLookup>();
		
		ChallengeLookup challenge = new ChallengeLookup();
		challenge.setId(1);
		challenge.setActive(true);
		challenge.setTitleAr(messageSource.getMessage("home.challenge1Title", null, Locale.forLanguageTag("ar")));
		challenge.setTitleEn(messageSource.getMessage("home.challenge1Title", null, Locale.forLanguageTag("en")));
		
		List<String> challenge1RulesAr = new ArrayList<String>();
		challenge1RulesAr.add(messageSource.getMessage("home.challenge1Rule2", null, Locale.forLanguageTag("ar")));
		challenge1RulesAr.add(messageSource.getMessage("home.challenge1Rule3", null, Locale.forLanguageTag("ar")));
		challenge1RulesAr.add(messageSource.getMessage("home.challenge1Rule4", null, Locale.forLanguageTag("ar")));
		challenge1RulesAr.add(messageSource.getMessage("home.challenge1Rule5", null, Locale.forLanguageTag("ar")));
		
		List<String> challenge1RulesEn = new ArrayList<String>();
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
		challenge2.setActive(true);
		challenge2.setTitleAr(messageSource.getMessage("home.challenge2Title", null, Locale.forLanguageTag("ar")));
		challenge2.setTitleEn(messageSource.getMessage("home.challenge2Title", null, Locale.forLanguageTag("en")));
		
		List<String> challenge2RulesAr = new ArrayList<String>();
		challenge2RulesAr.add(messageSource.getMessage("home.challenge2Rule1", null, Locale.forLanguageTag("ar")));
		challenge2RulesAr.add(messageSource.getMessage("home.challenge2Rule2", null, Locale.forLanguageTag("ar")));
		challenge2RulesAr.add(messageSource.getMessage("home.challenge2Rule3", null, Locale.forLanguageTag("ar")));
		challenge2RulesAr.add(messageSource.getMessage("home.challenge2Rule4", null, Locale.forLanguageTag("ar")));
		
		List<String> challenge2RulesEn = new ArrayList<String>();
		challenge2RulesEn.add(messageSource.getMessage("home.challenge2Rule1", null, Locale.forLanguageTag("en")));
		challenge2RulesEn.add(messageSource.getMessage("home.challenge2Rule2", null, Locale.forLanguageTag("en")));
		challenge2RulesEn.add(messageSource.getMessage("home.challenge2Rule3", null, Locale.forLanguageTag("en")));
		challenge2RulesEn.add(messageSource.getMessage("home.challenge2Rule4", null, Locale.forLanguageTag("en")));
		
		Map<String, List<String>> challenge2Rules = new HashMap<String, List<String>>();
		challenge2Rules.put("ar", challenge2RulesAr);
		challenge2Rules.put("en", challenge2RulesEn);
		
		challenge2.setRules(challenge2Rules);
		challenges.add(challenge2);
		
		ChallengeLookup challenge3 = new ChallengeLookup();
		challenge3.setId(3);
		challenge3.setActive(true);
		challenge3.setTitleAr(messageSource.getMessage("home.challenge3Title", null, Locale.forLanguageTag("ar")));
		challenge3.setTitleEn(messageSource.getMessage("home.challenge3Title", null, Locale.forLanguageTag("en")));
		
		List<String> challenge3RulesAr = new ArrayList<String>();
		challenge3RulesAr.add(messageSource.getMessage("home.challenge3Rule1", null, Locale.forLanguageTag("ar")));
		challenge3RulesAr.add(messageSource.getMessage("home.challenge3Rule2", null, Locale.forLanguageTag("ar")));
		challenge3RulesAr.add(messageSource.getMessage("home.challenge3Rule3", null, Locale.forLanguageTag("ar")));
		challenge3RulesAr.add(messageSource.getMessage("home.challenge3Rule4", null, Locale.forLanguageTag("ar")));
		
		List<String> challenge3RulesEn = new ArrayList<String>();
		challenge3RulesEn.add(messageSource.getMessage("home.challenge3Rule1", null, Locale.forLanguageTag("en")));
		challenge3RulesEn.add(messageSource.getMessage("home.challenge3Rule2", null, Locale.forLanguageTag("en")));
		challenge3RulesEn.add(messageSource.getMessage("home.challenge3Rule3", null, Locale.forLanguageTag("en")));
		challenge3RulesEn.add(messageSource.getMessage("home.challenge3Rule4", null, Locale.forLanguageTag("en")));
		
		Map<String, List<String>> challenge3Rules = new HashMap<String, List<String>>();
		challenge3Rules.put("ar", challenge3RulesAr);
		challenge3Rules.put("en", challenge3RulesEn);
		
		challenge3.setRules(challenge3Rules);
		challenges.add(challenge3);
		
		ChallengeLookup challenge4 = new ChallengeLookup();
		challenge4.setId(4);
		challenge4.setActive(true);
		challenge4.setTitleAr(messageSource.getMessage("home.challenge4Title", null, Locale.forLanguageTag("ar")));
		challenge4.setTitleEn(messageSource.getMessage("home.challenge4Title", null, Locale.forLanguageTag("en")));
		
		List<String> challenge4RulesAr = new ArrayList<String>();
		challenge4RulesAr.add(messageSource.getMessage("home.challenge4Rule1", null, Locale.forLanguageTag("ar")));
		challenge4RulesAr.add(messageSource.getMessage("home.challenge4Rule2", null, Locale.forLanguageTag("ar")));
		challenge4RulesAr.add(messageSource.getMessage("home.challenge4Rule3", null, Locale.forLanguageTag("ar")));
		challenge4RulesAr.add(messageSource.getMessage("home.challenge4Rule4", null, Locale.forLanguageTag("ar")));
		
		List<String> challenge4RulesEn = new ArrayList<String>();
		challenge4RulesEn.add(messageSource.getMessage("home.challenge4Rule1", null, Locale.forLanguageTag("en")));
		challenge4RulesEn.add(messageSource.getMessage("home.challenge4Rule2", null, Locale.forLanguageTag("en")));
		challenge4RulesEn.add(messageSource.getMessage("home.challenge4Rule3", null, Locale.forLanguageTag("en")));
		challenge4RulesEn.add(messageSource.getMessage("home.challenge4Rule4", null, Locale.forLanguageTag("en")));
		
		Map<String, List<String>> challenge4Rules = new HashMap<String, List<String>>();
		challenge4Rules.put("ar", challenge4RulesAr);
		challenge4Rules.put("en", challenge4RulesEn);
		
		challenge4.setRules(challenge4Rules);
		challenges.add(challenge4);
		
		Map<String, String> flags = new HashMap<String, String>();
		String defaultLanguage = String.valueOf(env.getProperty("home.defaultLanguage"));
		flags.put("defaultLanguage", defaultLanguage);
		
		String latestiOSVersion = String.valueOf(env.getProperty("home.latestiOSVersion"));
		flags.put("latestiOSVersion", latestiOSVersion);
		
		String latestAndroidVersion = String.valueOf(env.getProperty("home.latestAndroidVersion"));
		flags.put("latestAndroidVersion", latestAndroidVersion);
		
		result.setFlags(flags);
		
		result.setChallenges(challenges);
		result.setStatus(1);
		
		return result;
	}
}
