package dev.profitsoft.intership.booklibrary.service;

import dev.profitsoft.intership.booklibrary.dto.AuthorDetailsDto;
import dev.profitsoft.intership.booklibrary.dto.AuthorPaginationDto;
import dev.profitsoft.intership.booklibrary.dto.AuthorQueryDto;
import dev.profitsoft.intership.booklibrary.dto.AuthorSaveDto;

public interface AuthorService {

    AuthorPaginationDto searchAuthors(AuthorQueryDto query);

    String createAuthor(AuthorSaveDto dto);

    AuthorDetailsDto getAuthor(String id);

    void updateAuthor(String id, AuthorSaveDto dto);

    void deleteAuthor(String id);
}
