package org.repository_getter.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.repository_getter.model.Branch;
import org.repository_getter.model.Repository;
import org.repository_getter.service.BranchService;
import org.repository_getter.service.RepositoryService;
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
@RequiredArgsConstructor
@Slf4j
public class RepositoryServiceImpl implements RepositoryService {

    private final BranchService branchService;
    @Value("${github.api.base.url}")
    private String githubUrl;
    @Value("${github.api.repositories}")
    private String repositoriesPath;
    @Value("${github.api.repositories.variable.username}")
    private String repositoriesPathVariable;

    @Override
    public List<Repository> getRepositories(final String username) {
        final RestTemplate restTemplate = new RestTemplate();
        final ObjectMapper mapper = new ObjectMapper();
        final URI url = UriComponentsBuilder
                .fromUriString(githubUrl)
                .path(repositoriesPath)
                .uriVariables(Map.of(repositoriesPathVariable, username))
                .build()
                .toUri();
        try {
            final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            final JsonNode root = mapper.readTree(response.getBody());
            final List<Repository> repositories = new ArrayList<>();
            for (int i = 0; i < root.size(); i++) {
                if (!root.path(i).path("fork").booleanValue()) {
                    final String repositoryName = root.path(i).path("name").textValue();
                    final String login = root.path(i).path("owner").path("login").textValue();
                    final List<Branch> branches = branchService.getBranches(username, repositoryName);
                    repositories.add(new Repository(repositoryName, login, branches));
                }
            }
            return repositories;
        } catch (JsonProcessingException e) {
            log.error("Cannot get repositories for username: {}", username, e);
            return Collections.emptyList();
        }
    }
}
