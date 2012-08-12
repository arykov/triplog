package com.ryaltech.ww.dao;

import com.ryaltech.orm.Entity;

public class User implements Entity {
	public enum AuthProvider {
		GOOGLE, FACEBOOK, TWITTER
	}

	private long userId;
	private String userName;
	private AuthProvider authProvider;
	private String authProviderId;

	public long getUserId() {
		return userId;
	}

	public User setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public String getUserName() {
		return userName;
	}

	public User setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public AuthProvider getAuthProvider() {
		return authProvider;
	}

	public User setAuthProvider(AuthProvider authProvider) {
		this.authProvider = authProvider;
		return this;
	}

	
	@Override
	public long getId() {
		return getUserId();
	}

	@Override
	public void setId(long id) {
		setUserId(id);
		

	}

	public String getAuthProviderId() {
		return authProviderId;
	}

	public User setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
		return this;
	}

}
