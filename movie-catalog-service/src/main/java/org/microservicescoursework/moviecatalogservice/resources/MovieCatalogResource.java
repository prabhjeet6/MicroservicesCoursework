package org.microservicescoursework.moviecatalogservice.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.microservicescoursework.moviecatalogservice.models.*;
import org.microservicescoursework.moviecatalogservice.services.MovieInfo;
import org.microservicescoursework.moviecatalogservice.services.UserRatingInfo;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	WebClient.Builder webClientBuilder;

	/**
	 * DiscoveryClient represents read operations commonly available to Discovery
	 * service such as Netflix Eureka or consul.io
	 * 
	 * List<ServiceInstance> getInstances(String serviceId) Get all ServiceInstances
	 * associated with a particular serviceId
	 * 
	 * List<String> getServices() Returns: all known service ids
	 **/
	@Autowired
	DiscoveryClient discoveryClient;

	@Autowired
	UserRatingInfo userRatingInfo;

	@Autowired
	MovieInfo movieInfo;

	@RequestMapping("/{userId}")
	// @HystrixCommand(fallbackMethod = "getFallbackCatalog")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

		// get all rated movie ids
		UserRating ratings = userRatingInfo.getUserRating(userId);
		return ratings.getUserRating().stream().map(rating ->

		/*
		 * For each movieId, call movie-info service and get details Using RestTemplate
		 * Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" +
		 * rating.getMovieId(), Movie.class);
		 */
		/*
		 * Using WebClient:Seprate Webflux maven dependency added to incorporate
		 * WebClient.Builder class in the classpath
		 *
		 * Movie movie = webClientBuilder.build() .get()
		 * .uri("http://localhost:8082/movies/" + rating.getMovieId()) .retrieve()
		 * .bodyToMono(Movie.class) .block(); //here, .block() ensures that operation is
		 * blocked until a result is returned
		 */

		// Put them all together
		// return new CatalogItem(movie.getName(), "Christopher Nolan's masterpiece",
		// rating.getRating());

		movieInfo.getCatalogItem(rating)).collect(Collectors.toList());

	}

	/*
	 * public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String
	 * userId) {
	 * 
	 * return Arrays.asList(new CatalogItem("No Movie", userId, 0)); }
	 */

}