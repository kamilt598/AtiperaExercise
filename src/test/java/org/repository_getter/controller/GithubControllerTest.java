package org.repository_getter.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.repository_getter.model.CustomError;
import org.repository_getter.model.Repository;
import org.repository_getter.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class GithubControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private RepositoryService repositoryService;
    @Value(value = "${local.server.port}")
    private int port;

    @Test
    void shouldGetAllRepositories() {
        final String username = "username";
        when(repositoryService.getRepositories(anyString()))
                .thenReturn(List.of(new Repository("name", "login", Collections.emptyList())));
        final GithubController githubController = new GithubController(repositoryService);
        final ResponseEntity<?> response = githubController.getRepositories(username);
        final ResponseEntity<?> expected = new ResponseEntity<>(List.of(new Repository("name", "login", Collections.emptyList())), HttpStatus.OK);
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void shouldGetResponseWhenHttpMediaTypeNotAcceptableException() {
        final CustomError expected = new CustomError(HttpStatus.NOT_ACCEPTABLE.value(), "Could not find acceptable representation");
        when(repositoryService.getRepositories(anyString()))
                .thenReturn(List.of(new Repository("name", "login", Collections.emptyList())));
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/xml");
        final CustomError response = restTemplate
                .exchange("http://localhost:" + port + "/username", HttpMethod.GET, new HttpEntity<>(headers), CustomError.class)
                .getBody();
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void shouldGetResponseWhenHttpClientErrorException() {
        final CustomError expected = new CustomError(HttpStatus.NOT_FOUND.value(), "404 NOT_FOUND");
        when(repositoryService.getRepositories(anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        final GithubController githubController = new GithubController(repositoryService);
        final Object response = githubController.getRepositories("username").getBody();
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}