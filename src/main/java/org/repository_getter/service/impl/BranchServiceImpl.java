package org.repository_getter.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.repository_getter.model.Branch;
import org.repository_getter.service.BranchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BranchServiceImpl implements BranchService {

    @Value("${github.api.base.url}")
    private String githubUrl;
    @Value("${github.api.branches}")
    private String branchesUrl;
    @Value("${github.api.branches.variable.username}")
    private String usernameVariable;
    @Value("${github.api.branches.variable.repository-name}")
    private String repositoryNameVariable;

    @Override
    public List<Branch> getBranches(final String username, final String repositoryName) {
        final RestTemplate restTemplate = new RestTemplate();
        final ObjectMapper mapper = new ObjectMapper();
        final URI url = UriComponentsBuilder
                .fromUriString(githubUrl)
                .path(branchesUrl)
                .uriVariables(Map.of(usernameVariable, username, repositoryNameVariable, repositoryName))
                .build()
                .toUri();
        final ResponseEntity<String> branchResponse = restTemplate.getForEntity(url, String.class);
        final List<Branch> branchList = new ArrayList<>();
        try {
            final JsonNode branchRoot = mapper.readTree(branchResponse.getBody());
            for (int j = 0; j < branchRoot.size(); j++) {
                final String branchName = branchRoot.path(j).path("name").textValue();
                final String sha = branchRoot.path(j).path("commit").path("sha").textValue();
                branchList.add(new Branch(branchName, sha));
            }
            return branchList;
        } catch (JsonProcessingException e) {
            log.error("Cannot get branches for username: {}, repositoryName: {}", username, repositoryName, e);
            return Collections.emptyList();
        }
    }
}
