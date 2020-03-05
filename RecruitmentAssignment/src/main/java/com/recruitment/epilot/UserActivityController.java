package com.recruitment.epilot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserActivityController {

	private static final Logger log = LoggerFactory.getLogger(UserActivityController.class);

	// Note to reader, this is just an exercise, a real application should
	// have a PropertyManager class that dynamically loads and serves all properties
	private final String prefix;
	private final String page;
	private final String itemsPerPage;
	private final int durationInHours;

	@GetMapping("/active/{user}")
	public UserActivityAnswer repoListener(@PathVariable String user) {
		try {
			return fetchData(user);
		} catch (ResponseStatusException e) {
			throw e;
		}
	}

	public UserActivityAnswer fetchData(String user) throws ResponseStatusException {

		List<UserActivity> userActivities = new ArrayList<UserActivity>();

		LocalDateTime last24h = LocalDateTime.now().minusHours(durationInHours);

		// pagination limits response to 30 entries only.
		// load next pages until the entries are older than Xh
		Integer pageCount = 0;
		while (pageCount == 0 || userActivities.get(userActivities.size() - 1).getCreatedAt().isAfter(last24h)) {
			pageCount++;
			try {
				userActivities.addAll(fetchUserActivityFromGithub(user, pageCount));
			} catch (ResponseStatusException e) {
				throw e;
			}
		}

		// If answer resulted in no useful response of null or size 0, return
		// Should actually return some sort of Http status error in a non-exercise
		if (userActivities == null || userActivities.size() == 0) {
			return new UserActivityAnswer(false);
		}

		log.info(new String("Size of List:" + userActivities.size()));

		// Check for any push activities in the last x hours
		boolean pushLast24h = false;
		for (UserActivity userActivity : userActivities) {
			;
			if (userActivity.getCreatedAt() != null && userActivity.getType() != null
					&& userActivity.getType().equalsIgnoreCase("PushEvent")
					&& userActivity.getCreatedAt().isAfter(last24h)) {
				pushLast24h = true;
			}
		}

		return new UserActivityAnswer(pushLast24h);

	}

	private List<UserActivity> fetchUserActivityFromGithub(String user, int pageNumber) throws ResponseStatusException {

		RestTemplate restTemplate = new RestTemplate();

		// get a list of all closed (merged are a subset of closed) pull requests
		ResponseEntity<List<UserActivity>> responseEntity = restTemplate.exchange(
				prefix + user + page + pageNumber + itemsPerPage, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<UserActivity>>() {
				});
		List<UserActivity> userActivities = responseEntity.getBody();
		HttpStatus statusCode = responseEntity.getStatusCode();

		log.info(new String("StatusCode of repoPullRequests:" + statusCode));

		// If status Code of answer is not 2xx, throw an exception
		if (!statusCode.is2xxSuccessful()) {
			throw new ResponseStatusException(statusCode);
		}

		return userActivities;
	}

	@Autowired
	public UserActivityController(@Value("${app.url.github.api.useractivity.prefix}") String prefix,
			@Value("${app.url.github.api.useractivity.page}") String page,
			@Value("${app.url.github.api.useractivity.itemsPerPage}") String itemsPerPage,
			@Value("${app.url.github.api.useractivity.durationInHours}") int durationInDays) {
		this.prefix = prefix;
		this.page = page;
		this.itemsPerPage = itemsPerPage;
		this.durationInHours = durationInDays;
	}

}
