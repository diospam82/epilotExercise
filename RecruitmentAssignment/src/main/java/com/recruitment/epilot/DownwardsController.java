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
public class DownwardsController {

	private static final Logger log = LoggerFactory.getLogger(DownwardsController.class);

	// Note to reader, this is just an exercise, a real application should
	// have a PropertyManager class that dynamically loads and serves all properties
	private final String prefix;
	private final String listOfMerged;
	private final String pull;
	private final String page;
	private final String itemsPerPage;
	private final int durationInDays;
	
	@GetMapping("/downwards/{owner}/{repo}")
	public DownwardsAnswer repoListener(@PathVariable String owner, @PathVariable String repo) {
		try {
			return fetchData(owner+"/"+repo);
		}
		catch (ResponseStatusException e) {
			throw e;
		}
	}
	
	//fetch data from Github API v3 and calculate whether more additions or deletions have occured
	private DownwardsAnswer fetchData(String repo) throws ResponseStatusException {

		List<RepoPullRequest> repoPullRequests = new ArrayList<RepoPullRequest>();

		LocalDateTime last7d = LocalDateTime.now().minusDays(durationInDays);

		// pagination limits response to 100 entries only.
		// load next pages until the last updated entries are older than 7d
		int pageCount = 0;
		while (pageCount == 0 || repoPullRequests.get(repoPullRequests.size() - 1).getUpdated_at().isAfter(last7d)) {
			pageCount++;
			try {
				repoPullRequests.addAll(fetchRepoFromGithub(repo, pageCount));
			} catch (ResponseStatusException e) {
				throw e;
			}
		}

		// If answer resulted in no useful response of null or size 0, return false
		// Should actually return some sort of Http status error in a non-exercise
		if (repoPullRequests == null || repoPullRequests.size() == 0) {
			return new DownwardsAnswer(false);
		}

		List<Integer> toLoadPullRequestNumbers = new ArrayList<Integer>();

		// extract all succesfull merge numbers from list of pulls
		for (RepoPullRequest repoPullRequest : repoPullRequests) {
			if (repoPullRequest.getMerged_at() != null && repoPullRequest.getNumber() != null
					&& repoPullRequest.getMerged_at().isAfter(last7d)) {
				toLoadPullRequestNumbers.add(repoPullRequest.getNumber());
			}
		}

		// for all repo pulls that have been merged within the last X days,
		// now the detailed information must be fetched individually

		// tally up all additions minus all deletions
		Integer tallyOfChanges = 0;

		if (toLoadPullRequestNumbers.size() > 0) {
			for (Integer pullRequestNumber : toLoadPullRequestNumbers) {
				try {
					RepoPullRequest mergedPull = fetchRepoPullNumberFromGithub(repo, pullRequestNumber);

					// should check for notNull of additions and deletes? Will every successful
					// merge have additions and deletions NotNull?
					log.info(new String("mergedPull.getAdditions(): " + mergedPull.getAdditions()));
					log.info(new String("mergedPull.getDeletions(): " + mergedPull.getDeletions()));
					tallyOfChanges += mergedPull.getAdditions();
					tallyOfChanges -= mergedPull.getDeletions();
				} catch (ResponseStatusException e) {
					throw e;
				}
			}
		}

		log.info(new String("tallyOfChanges:" + tallyOfChanges.toString()));
		
		return tallyOfChanges < 0 ? new DownwardsAnswer(true) : new DownwardsAnswer(false); 
	}

	private List<RepoPullRequest> fetchRepoFromGithub(String repoName, int pageNumber) throws ResponseStatusException {

		RestTemplate restTemplate = new RestTemplate();

		// get a list of all closed (merged are a subset of closed) pull requests
		ResponseEntity<List<RepoPullRequest>> responseEntity = restTemplate.exchange(
				prefix + repoName + listOfMerged + page + pageNumber + itemsPerPage, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<RepoPullRequest>>() {
				});
		List<RepoPullRequest> repoPullRequests = responseEntity.getBody();
		HttpStatus statusCode = responseEntity.getStatusCode();

		log.info(new String("StatusCode of repoPullRequests:" + statusCode));

		// If status Code of answer is not 2xx, throw an exception
		if (!statusCode.is2xxSuccessful()) {
			throw new ResponseStatusException(statusCode);
		}

		return repoPullRequests;
	}

	private RepoPullRequest fetchRepoPullNumberFromGithub(String repoName, int pullNumber) throws ResponseStatusException {

		RestTemplate restTemplate = new RestTemplate();

		//get JSON of pullNumber from repoName
		ResponseEntity<RepoPullRequest> responseEntityLoop = restTemplate.exchange(
				prefix + repoName + pull + pullNumber, HttpMethod.GET, null,
				new ParameterizedTypeReference<RepoPullRequest>() {
				});
		RepoPullRequest mergedPull = responseEntityLoop.getBody();
		HttpStatus statusCode = responseEntityLoop.getStatusCode();

		log.info(new String("StatusCode of fetchRepoPullNumberFromGithub:" + statusCode + " for: " + pullNumber));
		
		// If status Code of answer is not 2xx, throw an exception
		if (!statusCode.is2xxSuccessful()) {
			throw new ResponseStatusException(statusCode);
		}

		return mergedPull;
	}

	@Autowired
	public DownwardsController(@Value("${app.url.github.api.repopullrequests.prefix}") String prefix,
			@Value("${app.url.github.api.repopullrequests.listOfMerged}") String listOfMerged,
			@Value("${app.url.github.api.repopullrequests.page}") String page,
			@Value("${app.url.github.api.repopullrequests.itemsPerPage}") String itemsPerPage,
			@Value("${app.url.github.api.repopullrequests.pull}") String pull,
			@Value("${app.url.github.api.repopullrequests.durationInDays}") int durationInDays) {
		this.prefix = prefix;
		this.listOfMerged = listOfMerged;
		this.page = page;
		this.itemsPerPage = itemsPerPage;
		this.pull = pull;
		this.durationInDays = durationInDays;
	}

}
