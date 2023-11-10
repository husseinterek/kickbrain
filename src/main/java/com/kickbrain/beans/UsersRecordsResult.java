package com.kickbrain.beans;

import java.util.List;

public class UsersRecordsResult extends BaseResult {

	private List<UserVO> users;

	public List<UserVO> getUsers() {
		return users;
	}

	public void setUsers(List<UserVO> users) {
		this.users = users;
	}

}
