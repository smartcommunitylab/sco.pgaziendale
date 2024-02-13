package it.smartcommunitylab.pgazienda.security;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import it.smartcommunitylab.pgazienda.domain.UserRole;

public class UserInfo implements Serializable {
	private static final long serialVersionUID = 7955499133667676791L;
	
	private String username;
    private String playerId;
	private List<UserRole> roles = new LinkedList<>();
	
	public UserInfo() {}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public List<UserRole> getRoles() {
		return roles;
	}
	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}
	

}
