package com.server.sellernexus.repository.user;

import java.util.List;

import com.server.sellernexus.model.users.User;

public interface UserRepositoryCustom {

	public List<User> search(String filter);

}
