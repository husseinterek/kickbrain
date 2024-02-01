package com.kickbrain.controller;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kickbrain.beans.GameResult;
import com.kickbrain.beans.GameRoom;
import com.kickbrain.manager.GameRoomManager;

@Controller
@RequestMapping("/")
public class BaseController {
	
	@Autowired
	private GameRoomManager gameRoomManager;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model, HttpSession session, Device device) {
		
		return device.isMobile() ? "home-mobile" : "home";
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public String download(Locale locale, Model model, HttpSession session, Device device) {
		
		String target = device.isMobile() ? "download" : "home";
		return target;
	}
	
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String admin(Locale locale, Model model, HttpSession session, Device device) {
		
		return "admin-questions";
	}
	
	@RequestMapping(value = "/admin-import", method = RequestMethod.GET)
	public String adminImport(Locale locale, Model model, HttpSession session, Device device) {
		
		return "admin-import";
	}
	
	@RequestMapping(value = "/admin-answers", method = RequestMethod.GET)
	public String adminAnswers(Locale locale, Model model, HttpSession session, Device device) {
		
		return "admin-answers";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Locale locale, Model model, HttpSession session, Device device) {
		
		return "login";
	}
	
	@RequestMapping(value = "/privacyPolicy", method = RequestMethod.GET)
	public String privacyPolicy(Locale locale, Model model, HttpSession session, Device device) {
		
		return "privacyPolicy";
	}
	
	@RequestMapping(value = "/generateNewGame", method = RequestMethod.GET)
	public String generateNewGame(@RequestParam(value = "roomId") String roomId, Locale locale, Model model, HttpSession session, Device device) {
		
		GameResult result = new GameResult();
		GameRoom gameRoom = null;
		try
		{
			if(StringUtils.isEmpty(roomId))
			{
				throw new Exception();
			}
			
			gameRoom = gameRoomManager.getGameRoomById(roomId);
			if(gameRoom != null)
			{
				result.setQuestions(gameRoom.getQuestions());
				result.setStatus(1);
			}
			else
			{
				result.setErrorMessage("Could not find the specified game room");
				result.setStatus(0);
				return "";
			}
		}
		catch(Exception ex)
		{
			result.setErrorMessage("Error while compiling the game questions");
			result.setStatus(0);
			ex.printStackTrace();
			return "";
		}
		
		model.addAttribute("roomId", roomId);
		model.addAttribute("player1", gameRoom.getPlayer1());
		model.addAttribute("player2", gameRoom.getPlayer2());
		model.addAttribute("currentTurn", gameRoom.getPlayer1().getPlayerId());
		model.addAttribute("result", result);
		model.addAttribute("isMobile", device.isMobile());
		
		return device.isMobile() ? "whatDoYouKnowChallenge-mobile" : "whatDoYouKnowChallenge";
	}
	
	@RequestMapping(value = "/generateSingleGame", method = RequestMethod.GET)
	public String generateSingleGame(@RequestParam(value = "mode") String mode, @RequestParam(value = "username") String username, Locale locale, Model model, HttpSession session, Device device) {
		
		GameResult result = new GameResult();
		GameRoom gameRoom = null;
		try
		{
			if(StringUtils.isEmpty(mode) || StringUtils.isEmpty(username))
			{
				throw new Exception();
			}
			
			gameRoom = gameRoomManager.createSingleGameRoom(username, null);
			if(gameRoom != null)
			{
				result.setQuestions(gameRoom.getQuestions());
				result.setStatus(1);
			}
		}
		catch(Exception ex)
		{
			result.setErrorMessage("Error while compiling the game questions");
			result.setStatus(0);
			ex.printStackTrace();
			return "";
		}
		
		model.addAttribute("roomId", gameRoom.getRoomId());
		model.addAttribute("player1", gameRoom.getPlayer1());
		model.addAttribute("player2", gameRoom.getPlayer2());
		model.addAttribute("result", result);
		model.addAttribute("isMobile", device.isMobile());
		
		return device.isMobile() ? "singlePlayerGame-mobile" : "singlePlayerGame";
	}
	
	@RequestMapping(value = "/waitingRoom", method = RequestMethod.GET)
	public String waitingRoom(@RequestParam(value = "username") String username, Locale locale, Model model, HttpSession session, Device device) {
		
		model.addAttribute("username", username);
		return "waitingRoom";
	}
	
}
