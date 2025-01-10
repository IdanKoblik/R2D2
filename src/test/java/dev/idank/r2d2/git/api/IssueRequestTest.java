package dev.idank.r2d2.git.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.idank.r2d2.git.request.GithubIssueRequest;
import dev.idank.r2d2.git.request.GitlabIssueRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IssueRequestTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void testSerializeAndDeserializeGithubIssueRequest() throws JsonProcessingException {
        GithubIssueRequest request = new GithubIssueRequest(
                "test",
                "test",
                Set.of("test1"),
                Set.of("me"),
                "prod"
        );

        String json = objectMapper.writeValueAsString(request);
        String expectedJson = "{\"@type\":\"GithubIssueRequest\",\"title\":\"test\",\"body\":\"test\",\"labels\":[\"test1\"],\"assignees\":[\"me\"],\"milestone\":\"prod\"}";

        assertEquals(expectedJson, json);
        assertEquals(request, objectMapper.readValue(expectedJson, GithubIssueRequest.class));
    }

    @Test
    void testSerializeAndDeserializeGithubIssueRequestWithNullables() throws JsonProcessingException {
        GithubIssueRequest request = new GithubIssueRequest(
                "test",
                null,
                Set.of("test1"),
                null,
                "prod"
        );

        String json = objectMapper.writeValueAsString(request);
        String expectedJson = "{\"@type\":\"GithubIssueRequest\",\"title\":\"test\",\"labels\":[\"test1\"],\"milestone\":\"prod\"}";

        assertEquals(expectedJson, json);
        assertEquals(request, objectMapper.readValue(expectedJson, GithubIssueRequest.class));
    }

    @Test
    void testSerializeAndDeserializeGitlabIssueRequest() throws JsonProcessingException {
        GitlabIssueRequest request = new GitlabIssueRequest(
                "test",
                "description",
                Set.of(),
                Set.of(1),
                "4"
        );

        String json = objectMapper.writeValueAsString(request);
        String expectedJson = "{\"@type\":\"GitlabIssueRequest\",\"title\":\"test\",\"description\":\"description\",\"labels\":[],\"assignee_ids\":[1],\"milestone_id\":\"4\"}";

        assertEquals(expectedJson, json);
        assertEquals(request, objectMapper.readValue(expectedJson, GitlabIssueRequest.class));
    }

    @Test
    void testSerializeAndDeserializeGitlabIssueRequestWithNullables() throws JsonProcessingException {
        GitlabIssueRequest request = new GitlabIssueRequest(
                "test",
                "description",
                Set.of(),
                null,
                "4"
        );

        String json = objectMapper.writeValueAsString(request);
        String expectedJson = "{\"@type\":\"GitlabIssueRequest\",\"title\":\"test\",\"description\":\"description\",\"labels\":[],\"milestone_id\":\"4\"}";

        assertEquals(expectedJson, json);
        assertEquals(request, objectMapper.readValue(expectedJson, GitlabIssueRequest.class));
    }
}
