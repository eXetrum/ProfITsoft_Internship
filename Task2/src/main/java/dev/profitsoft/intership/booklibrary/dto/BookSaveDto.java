package dev.profitsoft.intership.booklibrary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class BookSaveDto {
    @NotBlank(message = "title is required")
    private String title;

    @NotNull(message = "publishYear is required")
    private Integer publishYear;

    @NotBlank(message = "genre is required")
    private String genre;

    @NotNull(message = "authorId is required")
    @NotEmpty
    private String authorId;
}
