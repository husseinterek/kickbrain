package com.kickbrain.db.service;

import java.util.List;

import com.kickbrain.beans.UserVO;

public interface UserService {

	public void modifyUser(long id, UserVO user);
	public UserVO createUser(UserVO user);
	public UserVO findByUsername(String username);
	public UserVO findById(long id);
	public void addUserScore(long userId, int score);
	public List<UserVO> retrieveUsersWithScores();
	public void deleteUser(long id);
	public void addUserPremiumPoints(long userId, float premiumPoints);
}
