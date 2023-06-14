package org.repository_getter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.repository_getter.model.Branch;
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
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final RestTemplate restTemplate;
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
        final ObjectMapper mapper = new ObjectMapper();
        final URI uri = getUri(username, repositoryName);
        final ResponseEntity<String> branchResponse = restTemplate.getForEntity(uri, String.class);
        final List<Branch> branchList = new ArrayList<>();
        try {
            final JsonNode branchRoot = mapper.readTree(branchResponse.getBody());
            for (int j = 0; j < branchRoot.size(); j++) {
                final String branchName = branchRoot.path(j).path("name").textValue();
                final String sha = branchRoot.path(j).path("commit").path("sha").textValue();
                branchList.add(new Branch(branchName, sha));
            }
            return branchList;
        } catch (Exception e) {
            log.error("Cannot get branches for username: {}, repositoryName: {}", username, repositoryName, e);
            return Collections.emptyList();
        }
    }

    private URI getUri(final String username, final String repositoryName) {
        return UriComponentsBuilder
                .fromUriString(githubUrl)
                .path(branchesUrl)
                .uriVariables(Map.of(usernameVariable, username, repositoryNameVariable, repositoryName))
                .build()
                .toUri();
    }
}
