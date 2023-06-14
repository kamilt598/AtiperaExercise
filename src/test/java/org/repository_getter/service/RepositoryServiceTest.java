package org.repository_getter.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.repository_getter.TestConfig;
import org.repository_getter.model.Branch;
import org.repository_getter.model.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(classes = TestConfig.class)
@ExtendWith(MockitoExtension.class)
class RepositoryServiceTest {

    @Autowired
    private RepositoryService repositoryService;
    @MockBean
    private BranchService branchService;
    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    @Value("${github.api.base.url}")
    private String githubUrl;
    @Value("${github.api.repositories}")
    private String repositoriesPath;
    @Value("${github.api.repositories.variable.username}")
    private String repositoriesPathVariable;

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldGetAllRepositories() {
        final String username = "username";
        final String expectedJson = "[\n" +
                "  {\n" +
                "    \"name\": \"repository1\",\n" +
                "    \"owner\": {\n" +
                "      \"login\": \"login1\",\n" +
                "      \"id\": \"id1\"\n" +
                "    },\n" +
                "    \"fork\": false\n" +
                "  }\n" +
                "]";
        final URI url = UriComponentsBuilder
                .fromUriString(githubUrl)
                .path(repositoriesPath)
                .uriVariables(Map.of(repositoriesPathVariable, username))
                .build()
                .toUri();
        mockServer.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedJson));
        when(branchService.getBranches(anyString(), anyString()))
                .thenReturn(List.of(new Branch("master", "1111111111111111111111111111111111111111")));
        final List<Repository> repositories = repositoryService.getRepositories(username);

        Assertions.assertEquals("repository1", repositories.get(0).getName());
        Assertions.assertEquals("login1", repositories.get(0).getLogin());
        Assertions.assertEquals("master", repositories.get(0).getBranchList().get(0).getBranchName());
        Assertions.assertEquals("1111111111111111111111111111111111111111", repositories.get(0).getBranchList().get(0).getSha());
    }

    @Test
    void shouldNotGetAllRepositoriesWhenException() {
        final String username = "username";
        final URI url = UriComponentsBuilder
                .fromUriString(githubUrl)
                .path(repositoriesPath)
                .uriVariables(Map.of(repositoriesPathVariable, username))
                .build()
                .toUri();
        mockServer.expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("x"));
        final List<Repository> repositories = repositoryService.getRepositories(username);

        Assertions.assertEquals(Collections.emptyList(), repositories);
    }

}