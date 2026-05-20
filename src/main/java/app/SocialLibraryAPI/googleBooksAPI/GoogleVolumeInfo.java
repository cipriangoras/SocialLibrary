package app.SocialLibraryAPI.googleBooksAPI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleVolumeInfo(
        @JsonProperty("title") String title,
        @JsonProperty("authors") List<String> authors,
        @JsonProperty("description") String description,
        @JsonProperty("categories") List<String> categories,
        @JsonProperty("imageLinks") GoogleImageLinks imageLinks,
        @JsonProperty("industryIdentifiers") List<GoogleIdentifier> industryIdentifiers
) {}
