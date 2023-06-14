package org.repository_getter.controller;

import lombok.RequiredArgsConstructor;
import org.repository_getter.handler.HttpExceptionHandler;
import org.repository_getter.model.CustomError;
import org.repository_getter.service.RepositoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

@Controller
@RequiredArgsConstructor
public class GithubController extends HttpExceptionHandler {

    private final RepositoryService repositoryService;

    @GetMapping(value = "/{username}")
    public @ResponseBody ResponseEntity<?> getRepositories(@PathVariable final String username) {
        try {
            return new ResponseEntity<>(repositoryService.getRepositories(username), HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(new CustomError(e.getRawStatusCode(), e.getMessage()), e.getStatusCode());
        }
    }
}
