package app.SocialLibraryAPI.googleBooksAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksResponse(
        @JsonProperty("items") List<GoogleBookItem> items
) {}

