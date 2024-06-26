package dev.profitsoft.intership.booklibrary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class AuthorPaginationDto {
    private List<AuthorDetailsDto> list;
	private Long totalItems;
    private Integer totalPages;
}