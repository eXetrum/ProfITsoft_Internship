package dev.profitsoft.intership.booklibrary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class BookUploadDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("publishYear")
    private Integer publishYear;

    @JsonProperty("genre")
    private String genre;

    @JsonProperty("author")
    private AuthorDetailsDto author;
}
