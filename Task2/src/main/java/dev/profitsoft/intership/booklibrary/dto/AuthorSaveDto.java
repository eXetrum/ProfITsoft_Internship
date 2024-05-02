package dev.profitsoft.intership.booklibrary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class AuthorSaveDto {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "birthdayYear is required")
    private Integer birthdayYear;

}