package dev.profitsoft.intership.booklibrary.service;

import dev.profitsoft.intership.booklibrary.dto.AuthorDetailsDto;
import dev.profitsoft.intership.booklibrary.dto.AuthorSaveDto;

import java.util.List;

public interface AuthorService {

    List<AuthorDetailsDto> getAllAuthors();

    String createAuthor(AuthorSaveDto dto);

    AuthorDetailsDto getAuthor(String id);

    void updateAuthor(String id, AuthorSaveDto dto);

    void deleteAuthor(String id);
}
