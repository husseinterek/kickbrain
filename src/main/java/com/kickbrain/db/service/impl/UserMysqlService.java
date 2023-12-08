package com.kickbrain.db.service.impl;

import java.util.ArrayList;
import java.util.List;

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
		
		User user = userDao.findById(userId).get();
		user.setTotalScore(user.getTotalScore() + score);
		
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
		
		userDao.save(user);
	}
	
	@Override
	public List<UserVO> retrieveUsersWithScores() {
		
		List<UserVO> result = new ArrayList<UserVO>();
		List<User> users = userDao.findSqlQuery("select * from USERS where id <> 1 and total_score > 0 order by total_score desc", User.class);
		
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
	public void deleteUser(long id) {
		userDao.deleteById(id);
	}
	
}
