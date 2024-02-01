package com.kickbrain.manager;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kickbrain.beans.PremiumPointsHistoryVO;
import com.kickbrain.beans.SubscribeRequest;
import com.kickbrain.beans.SubscribeResult;
import com.kickbrain.beans.UserVO;
import com.kickbrain.configuration.XMLConfigurationManager;
import com.kickbrain.db.service.GameService;
import com.kickbrain.db.service.UserService;

@Component
public class UserManager{

	@Autowired
	private UserService userService;
	
	@Autowired
	private GameService gameService;

	@Autowired
    private XMLConfigurationManager xmlConfigurationManager;
	
	private DecimalFormat df = new DecimalFormat("0.00");
	
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
		UserVO user = userService.findById(id);
		
		float premiumPoints = user.getPremiumPoints();
		premiumPoints = Float.valueOf(df.format(premiumPoints));
		user.setPremiumPoints(premiumPoints);
		
		return user;
	}
	
	public void modifyUser(String id, UserVO user)
	{
		userService.modifyUser(Long.valueOf(id), user);
	}
	
	public List<UserVO> retrieveUsersWithScores()
	{
		return userService.retrieveUsersWithScores();
	}
	
	public List<UserVO> retrieveTopUsersThisMonth()
	{
		return userService.retrieveTopUsersThisMonth();
	}
	
	public void deleteUser(String id)
	{
		gameService.deleteWaitingGamesForPlayer(Long.valueOf(id));
		gameService.deleteGamesForPlayer(Long.valueOf(id));
		userService.deleteUser(Long.valueOf(id));
	}
	
	public void deductPrivateGamePoints(String id, float premiumPoints)
	{
		userService.deductUserPremiumPoints(Long.valueOf(id), premiumPoints);
		
		PremiumPointsHistoryVO premiumPointsHistoryVO = new PremiumPointsHistoryVO();
		premiumPointsHistoryVO.setPlayerId(Long.valueOf(id));
		premiumPointsHistoryVO.setPremiumPoints(premiumPoints * -1);
		premiumPointsHistoryVO.setCreationDate(new Date());
		
		gameService.addPremiumPointsHistoryRecord(premiumPointsHistoryVO);
	}
	
	public void completeWatchingAd(String userId)
	{
		float adToPremiumPoints = xmlConfigurationManager.getAppConfigurationBean().getAdToPremiumPoints();
		
		userService.addUserPremiumPoints(Long.valueOf(userId), adToPremiumPoints);
		
		PremiumPointsHistoryVO premiumPointsHistoryVO = new PremiumPointsHistoryVO();
		premiumPointsHistoryVO.setPlayerId(Long.valueOf(userId));
		premiumPointsHistoryVO.setPremiumPoints(adToPremiumPoints);
		premiumPointsHistoryVO.setCreationDate(new Date());
		
		gameService.addPremiumPointsHistoryRecord(premiumPointsHistoryVO);
	}
}
