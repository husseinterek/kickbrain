package com.kickbrain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kickbrain.beans.BaseResult;
import com.kickbrain.beans.GameHistoryReportResult;
import com.kickbrain.beans.GamesHistoryResult;
import com.kickbrain.beans.SubscribeRequest;
import com.kickbrain.beans.SubscribeResult;
import com.kickbrain.beans.UserVO;
import com.kickbrain.beans.UsersRecordsResult;
import com.kickbrain.manager.GameRoomManager;
import com.kickbrain.manager.UserManager;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserManager userManager;
	
	@Autowired
	private GameRoomManager gameRoomManager;

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes="application/json")
	public SubscribeResult subscribe(@RequestBody SubscribeRequest request) {
		
		SubscribeResult result = userManager.subscribe(request);
		return result;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces="application/json")
	public UserVO retrieveUser(@PathVariable String id) {
		
		return userManager.retrieveUser(Long.valueOf(id));
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, produces="application/json")
	public BaseResult modifyUser(@PathVariable String id, @RequestBody UserVO user) {
		
		BaseResult result = new BaseResult();
		
		userManager.modifyUser(id, user);
		
		result.setStatus(1);
		return result;
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST, produces="application/json")
	public BaseResult deleteUser(@PathVariable String id) {
		
		BaseResult result = new BaseResult();
		
		userManager.deleteUser(id);
		
		result.setStatus(1);
		return result;
	}
	
	@RequestMapping(value = "/{id}/games", method = RequestMethod.GET, produces="application/json")
	public GamesHistoryResult gamesHistory(@PathVariable long id) {
		
		GamesHistoryResult result = gameRoomManager.retrieveGamesHistory(id);
		result.setStatus(1);
		return result;
	}
	
	@RequestMapping(value = "/rankings", method = RequestMethod.GET, produces="application/json")
	public UsersRecordsResult usersRecords() {
		
		UsersRecordsResult result = new UsersRecordsResult();
		
		List<UserVO> users = userManager.retrieveUsersWithScores();
		result.setUsers(users);
		
		result.setStatus(1);
		return result;
	}
	
	@RequestMapping(value = "/{id}/games/{gameId}", method = RequestMethod.GET, produces="application/json")
	public GameHistoryReportResult gameHistoryReport(@PathVariable long id, @PathVariable long gameId) {
		
		GameHistoryReportResult result = gameRoomManager.retrieveGameHistoryReport(gameId, id);
		result.setStatus(1);
		return result;
	}
}
