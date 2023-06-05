package org.atipera.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.atipera.model.Branch;
import org.atipera.model.CustomError;
import org.atipera.model.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GithubController {
    @GetMapping(value = "/{githubUsername}")
    public @ResponseBody ResponseEntity<?> getRepositories(@PathVariable String githubUsername) throws JsonProcessingException {
        return process(githubUsername);
    }

    private ResponseEntity<?> process(String username) throws JsonProcessingException {
        final RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.github.com/users/" + username + "/repos";
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            final JsonNode root = mapper.readTree(response.getBody());
            final List<Repository> repositories = new ArrayList<>();
            for (int i = 0; i < root.size(); i++) {
                if (!root.path(i).path("fork").booleanValue()) {
                    final Repository repository = new Repository();
                    final String repositoryName = root.path(i).path("name").textValue();
                    repository.setName(repositoryName);
                    repository.setLogin(root.path(i).path("owner").path("login").textValue());
                    getBranchList(username, restTemplate, mapper, repository, repositoryName);
                    repositories.add(repository);
                }
            }
            return new ResponseEntity<>(repositories, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(CustomError.builder()
                    .status(e.getRawStatusCode())
                    .message(e.getMessage())
                    .build(), e.getStatusCode());
        }
    }

    private static void getBranchList(String username, RestTemplate restTemplate, ObjectMapper mapper, Repository repository, String repositoryName)
            throws JsonProcessingException {
        final String url = "https://api.github.com/repos/" + username + "/" + repositoryName + "/branches";
        final ResponseEntity<String> branchResponse = restTemplate.getForEntity(url, String.class);
        final JsonNode branchRoot = mapper.readTree(branchResponse.getBody());
        final List<Branch> branchList = new ArrayList<>();
        for (int j = 0; j < branchRoot.size(); j++) {
            branchList.add(Branch.builder()
                    .branchName(branchRoot.path(j).path("name").textValue())
                    .sha(branchRoot.path(j).path("commit").path("sha").textValue())
                    .build());
        }
        repository.setBranchList(branchList);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public final ResponseEntity<?> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).contentType(MediaType.APPLICATION_JSON)
                .body(CustomError.builder()
                        .status(406)
                        .message(e.getMessage())
                        .build());
    }
}
