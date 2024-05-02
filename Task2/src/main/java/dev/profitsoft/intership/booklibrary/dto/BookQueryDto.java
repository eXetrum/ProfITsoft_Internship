package dev.profitsoft.intership.booklibrary.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
public class BookQueryDto {
    private String authorId;
    private String authorName;
    private int page = 0;
    private int size = 5;
}

