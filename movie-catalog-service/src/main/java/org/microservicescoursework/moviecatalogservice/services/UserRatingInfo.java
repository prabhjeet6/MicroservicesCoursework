package org.microservicescoursework.moviecatalogservice.services;

import java.util.Arrays;

import org.microservicescoursework.moviecatalogservice.models.Rating;
import org.microservicescoursework.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

/**
 * We have separated each external microservice call with a separate circuit
 * breaker instead of using a generic fallback for different circuit
 * breakers.This allows us to get fallback data only for the failing service
 * while getting actual data for the service that is working fine.
 * 
 * Further, we needed to separate out microservice calls and their fallback
 * methods into their separate classes annotated with @Service respectively as
 * when Spring calls and API wrapped with Hystrix, it gets the Hystrix's proxy
 * instance and it is able to call the fallback method, but, in this particular
 * case, two Hystrix circuit breakers were present in the same API class. When
 * Spring calls the API, it gets the API instance instead of Hystrix
 * proxy,(probably, because it is not able to decide as to which of the two
 * Hystrix circuit breakers will create proxy instance here to be returned to
 * the external call ) which is not able to call fallback methods.
 **/

@Service
public class UserRatingInfo {
	@Autowired
	RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "getFallbackUserRating",
			/**
			 * Configuring Hystrix Properties to implement Bulk Head Pattern. According to
			 * Bulk Head Pattern, We create separate thread pools for each micro-service
			 * restTemplate calls, so, that the slow microservice does not consume all
			 * threads rendering normal service to eventually become slow due to thread pool
			 * getting consumed due to slow service.Instead it has its separate thread pool.
			 **/
			threadPoolKey = "UserRatingPool", threadPoolProperties = {
					// number of concurrent threads microservice call allows
					@HystrixProperty(name = "coreSize", value = "20"),
					// number of requests Queued in buffer
					@HystrixProperty(name = "maxQueueSize", value = "10")

			}, commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000") })
	public UserRating getUserRating(String userId) {
		return restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);
	}

	public UserRating getFallbackUserRating(String userId) {
		UserRating userRating = new UserRating();
		userRating.setUserRating(Arrays.asList(new Rating("0", 0)));
		return userRating;
	}

}
