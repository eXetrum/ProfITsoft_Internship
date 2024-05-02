package dev.profitsoft.intership.booklibrary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Getter
@Builder
@Jacksonized
public class AuthorDetailsDto {
    private String id;
    private String name;
    private Integer birthdayYear;
    private Instant savedAt;
}
