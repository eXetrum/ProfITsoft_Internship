package dev.profitsoft.intership.booklibrary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Getter
@Builder
@Jacksonized
public class BookDetailsDto {
    private String id;
    private String title;
    private Integer publishYear;
    private String genre;
    private AuthorDetailsDto author;
    private Instant savedAt;
}
