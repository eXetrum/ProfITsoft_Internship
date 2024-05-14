package dev.profitsoft.intership.booklibrary.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
public class AuthorQueryDto {
    private String name;
    private Integer page = 0;
    private Integer size = 5;
}
