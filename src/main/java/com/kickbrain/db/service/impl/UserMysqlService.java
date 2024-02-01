package com.kickbrain.db.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kickbrain.beans.UserVO;
import com.kickbrain.db.model.User;
import com.kickbrain.db.repository.UserRepository;
import com.kickbrain.db.service.UserService;
import com.kickbrain.manager.Utility;

@Service
@Transactional
public class UserMysqlService implements UserService {

	@Autowired
	private UserRepository userDao;
	
	public UserVO findByUsername(String username) {
		List<User> users = userDao.findSqlQuery("select * from USERS where username = '" + username + "'", User.class);
		
		User user = new User();
		if(users.size() > 0)
		{
			user = users.get(0);
		}
		return Utility.convertUserModelToVO(user);
	}
	
	public UserVO findById(long id) {
		List<User> users = userDao.findSqlQuery("select * from USERS where id = " + id, User.class);
		
		User user = new User();
		if(users.size() > 0)
		{
			user = users.get(0);
		}
		return Utility.convertUserModelToVO(user);
	}
	
	@Override
	public void addUserScore(long userId, int score) {
		
		try
		{
			User user = userDao.findById(userId).get();
			user.setTotalScore(user.getTotalScore() + score);
			
			userDao.save(user);
		}
		catch(Exception ex)
		{
			System.out.println("An error occurred while adding the score of user: " + userId);
		}
	}
	
	@Override
	public void addUserPremiumPoints(long userId, float premiumPoints) {
		
		User user = userDao.findById(userId).get();
		user.setPremiumPoints(user.getPremiumPoints() + premiumPoints);
		
		userDao.save(user);
	}
	
	@Override
	public void deductUserPremiumPoints(long userId, float premiumPoints) {
		
		User user = userDao.findById(userId).get();
		user.setPremiumPoints(user.getPremiumPoints() - premiumPoints);
		
		userDao.save(user);
	}
	
	@Override
	public UserVO createUser(UserVO userVO) {
		
		User user = Utility.convertUserVOToModel(userVO);
		user = userDao.save(user);
		
		return Utility.convertUserModelToVO(user);
	}
	
	@Override
	public void modifyUser(long id, UserVO userVO) {
		
		User user = userDao.findById(id).get();
		
		if(userVO.getFirstName() != null)
		{
			user.setFirstName(userVO.getFirstName().replaceAll("[^\u0000-\uFFFF]", ""));
		}
		
		if(userVO.getLastName() != null)
		{
			user.setLastName(userVO.getLastName().replaceAll("[^\u0000-\uFFFF]", ""));
		}
		
		user.setReferredBy(userVO.getReferredBy());
		
		userDao.save(user);
	}
	
	@Override
	public List<UserVO> retrieveUsersWithScores() {
		
		List<UserVO> result = new ArrayList<UserVO>();
		List<User> users = userDao.findSqlQuery("select * from USERS where id <> 1 and total_score > 0 order by total_score desc limit 50", User.class);
		
		for(int i=0; i<users.size(); i++)
		{
			User user = users.get(i);
			UserVO userVO = Utility.convertUserModelToVO(user);
			userVO.setRank((i+1));
			result.add(userVO);
		}
		
		return result;
	}
	
	@Override
	public List<UserVO> retrieveTopUsersThisMonth() {
		
		List<UserVO> result = new ArrayList<UserVO>();
		
		String queryFilter = "SELECT u.id, u.first_name, u.last_name , SUM(player_scores.player_score) AS player_total_score" + 
				" FROM (" + 
				"    SELECT player1_id AS player_id, player1_score AS player_score, creation_date" + 
				"    FROM GAMES" + 
				"    WHERE MONTH(creation_date) = MONTH(CURRENT_DATE())" + 
				"    AND YEAR(CREATION_DATE) = YEAR(CURRENT_DATE())" + 
				"    " + 
				"    UNION ALL" + 
				"    " + 
				"    SELECT player2_id AS player_id, player2_score AS player_score, creation_date" + 
				"    FROM GAMES" + 
				"    WHERE MONTH(creation_date) = MONTH(CURRENT_DATE())" + 
				"    AND YEAR(CREATION_DATE) = YEAR(CURRENT_DATE())" + 
				" ) AS player_scores" + 
				" join USERS u on u.id = player_scores.player_id and player_scores.player_score <> 0 and u.id <> 1" + 
				" GROUP BY u.id, u.first_name, u.last_name" + 
				" ORDER BY player_total_score DESC"
				+ " limit 50";
		
		List<Tuple> records = userDao.findSqlQuery(queryFilter);
		int i=0;
		for(Tuple record : records)
		{
			UserVO user = new UserVO();
			user.setId(Integer.valueOf(String.valueOf(record.get(0))));
			user.setFirstName(String.valueOf(record.get(1)));
			user.setLastName(String.valueOf(record.get(2)));
			user.setTotalScore(Integer.valueOf(String.valueOf(record.get(3))));
			user.setRank((++i));

			result.add(user);
		}
		
		return result;
	}
	
	@Override
	public void deleteUser(long id) {
		userDao.deleteById(id);
	}
	
}
