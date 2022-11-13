package org.microservicescoursework.moviecatalogservice.services;

import org.microservicescoursework.moviecatalogservice.models.CatalogItem;
import org.microservicescoursework.moviecatalogservice.models.Movie;
import org.microservicescoursework.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

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
public class MovieInfo {
	@Autowired
	RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "getFallbackCatalogItem")
	public CatalogItem getCatalogItem(Rating rating) {
		Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
		return new CatalogItem(movie.getName(), "Christopher Nolan's masterpiece", rating.getRating());
	}

	public CatalogItem getFallbackCatalogItem(Rating rating) {
		return new CatalogItem("No Movie", "", rating.getRating());
	}
}
