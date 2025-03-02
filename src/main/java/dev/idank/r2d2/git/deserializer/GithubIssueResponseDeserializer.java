package dev.idank.r2d2.git.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.idank.r2d2.git.response.GithubIssueResponse;

import java.io.IOException;

public class GithubIssueResponseDeserializer extends JsonDeserializer<GithubIssueResponse> {
    @Override
    public GithubIssueResponse deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = parser.getCodec().readTree(parser);
        return new GithubIssueResponse(
                node.get("html_url").asText(),
                node.get("title").asText(),
                node.get("body").asText(null)
        );
    }
}
