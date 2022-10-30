package org.microservicescoursework.ratingsdataservice.resources;

import java.util.Arrays;
import java.util.List;

import org.microservicescoursework.ratingsdataservice.models.Rating;
import org.microservicescoursework.ratingsdataservice.models.UserRating;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratingsdata")
public class RatingsResource {
	@RequestMapping("/movieId")
	public Rating getRating(@PathVariable("movieId") String movieId) {
		return new Rating(movieId, 4);
	}
	
	@RequestMapping("/users/{userId}")
	public UserRating getUserRating(@PathVariable("userId") String userId) {
		List<Rating> ratings = Arrays.asList(new Rating("10", 5), new Rating("11", 4));
        UserRating userRating=new UserRating();
        userRating.setUserRating(ratings);
		return userRating;
	}

}
