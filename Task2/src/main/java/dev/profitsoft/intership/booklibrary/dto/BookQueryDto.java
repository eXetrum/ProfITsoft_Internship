package dev.profitsoft.intership.booklibrary.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
public class BookQueryDto {
    private String title;
    private String genre;
    private Integer publishYear;
    private String authorId;
    private String authorName;
    private Integer page = 0;
    private Integer size = 5;
}

