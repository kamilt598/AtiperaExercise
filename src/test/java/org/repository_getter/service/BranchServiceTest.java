package org.repository_getter.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.repository_getter.TestConfig;
import org.repository_getter.model.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(classes = TestConfig.class)
public class BranchServiceTest {

    public static final String EXPECTED_JSON = "[\n" +
            "  {\n" +
            "    \"name\": \"master\",\n" +
            "    \"commit\": {\n" +
            "      \"sha\": \"1111111111111111111111111111111111111111\"\n" +
            "    },\n" +
            "    \"protected\": false\n" +
            "  }\n" +
            "]";
    @Autowired
    private BranchService branchService;
    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    @Value("${github.api.base.url}")
    private String githubUrl;
    @Value("${github.api.branches}")
    private String branchesUrl;
    @Value("${github.api.branches.variable.username}")
    private String usernameVariable;
    @Value("${github.api.branches.variable.repository-name}")
    private String repositoryNameVariable;

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldGetAllBranches() {
        final String username = "username";
        final String repositoryName = "repository";
        mockServer.expect(ExpectedCount.once(), requestTo(getUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(EXPECTED_JSON));
        final List<Branch> branches = branchService.getBranches(username, repositoryName);

        Assertions.assertEquals("master", branches.get(0).getBranchName());
        Assertions.assertEquals("1111111111111111111111111111111111111111", branches.get(0).getSha());
    }

    @Test
    void shouldNotGetAllBranchesWhenException() {
        final String username = "username";
        final String repositoryName = "repository";
        mockServer.expect(ExpectedCount.once(), requestTo(getUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("x"));

        final List<Branch> branches = branchService.getBranches(username, repositoryName);
        Assertions.assertEquals(Collections.emptyList(), branches);
    }

    private URI getUri() {
        return UriComponentsBuilder
                .fromUriString(githubUrl)
                .path(branchesUrl)
                .uriVariables(Map.of(usernameVariable, "username", repositoryNameVariable, "repository"))
                .build()
                .toUri();
    }

}