package org.microservicescoursework.moviecatalogservice.models;

import java.util.List;

public class UserRating {
	private List<Rating> userRating;

	

	public void setUserRating(List<Rating> userRating) {
		this.userRating = userRating;
	}

	public List<Rating> getUserRating() {
		return userRating;
	}

	public UserRating() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}