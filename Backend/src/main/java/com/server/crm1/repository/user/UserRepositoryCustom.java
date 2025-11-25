package com.server.crm1.repository.user;

import java.util.List;

import com.server.crm1.model.users.User;

public interface UserRepositoryCustom {

	public List<User> search(String filter);

}
