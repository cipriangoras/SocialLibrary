package app.SocialLibraryAPI.googleBooksAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleImageLinks(
        @JsonProperty("thumbnail") String thumbnail
) {}
