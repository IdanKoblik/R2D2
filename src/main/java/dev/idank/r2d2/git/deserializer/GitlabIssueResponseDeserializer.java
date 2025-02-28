package dev.idank.r2d2.git.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.idank.r2d2.git.response.GitlabIssueResponse;

import java.io.IOException;

public class GitlabIssueResponseDeserializer extends JsonDeserializer<GitlabIssueResponse> {
    @Override
    public GitlabIssueResponse deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = parser.getCodec().readTree(parser);
        System.out.println(node);
        return new GitlabIssueResponse(
                node.get("web_url").asText(),
                node.get("title").asText(),
                node.get("description").asText(null)
        );
    }
}
