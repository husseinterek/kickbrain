package com.kickbrain.manager;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kickbrain.beans.SubscribeRequest;
import com.kickbrain.beans.SubscribeResult;
import com.kickbrain.beans.UserVO;
import com.kickbrain.db.service.UserService;

@Component
public class UserManager{

	@Autowired
	private UserService userService;
	
	public SubscribeResult subscribe(SubscribeRequest request)
	{
		SubscribeResult result = new SubscribeResult();
	
		String playerId = "";
		UserVO existingUser = userService.findByUsername(request.getUsername());
		if(existingUser.getUsername() == null)
		{
			UserVO userVO = new UserVO();
			userVO.setFirstName(request.getFirstName().replaceAll("[^\u0000-\uFFFF]", ""));
			userVO.setLastName(request.getLastName().replaceAll("[^\u0000-\uFFFF]", ""));
			userVO.setUsername(request.getUsername());
			userVO.setCreationDate(new Date());

			userVO = userService.createUser(userVO);
			playerId = String.valueOf(userVO.getId());
		}
		else
		{
			playerId = String.valueOf(existingUser.getId());
		}
		
		result.setPlayerId(playerId);
		result.setStatus(1);
		
		return result;
	}
	
	public UserVO retrieveUser(long id)
	{
		return userService.findById(id);
	}
	
	public void modifyUser(String id, UserVO user)
	{
		userService.modifyUser(Long.valueOf(id), user);
	}
	
	public List<UserVO> retrieveUsersWithScores()
	{
		return userService.retrieveUsersWithScores();
	}
}
