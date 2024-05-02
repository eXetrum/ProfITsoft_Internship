package dev.profitsoft.intership.booklibrary.service;

import dev.profitsoft.intership.booklibrary.exceptions.AuthorAlreadyExistsException;
import dev.profitsoft.intership.booklibrary.exceptions.AuthorNotFoundException;
import lombok.RequiredArgsConstructor;
import dev.profitsoft.intership.booklibrary.data.AuthorData;
import dev.profitsoft.intership.booklibrary.dto.AuthorDetailsDto;
import dev.profitsoft.intership.booklibrary.dto.AuthorSaveDto;
import dev.profitsoft.intership.booklibrary.repository.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorDetailsDto> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(AuthorServiceImpl::convertToDetailsDto)
                .toList();
    }

    @Override
    public String createAuthor(AuthorSaveDto dto) {
        validateAuthor(dto);
        AuthorData data = new AuthorData();
        updateDataFromDto(data, dto);
        data.setId(UUID.randomUUID().toString());
        try {
            AuthorData savedData = authorRepository.save(data);
            return savedData.getId();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e.getMessage().contains("duplicate key"))
                throw new AuthorAlreadyExistsException();
            throw e;
        }
    }

    @Override
    public AuthorDetailsDto getAuthor(String id) {
        AuthorData data = authorRepository.findById(id)
                .orElseThrow(AuthorNotFoundException::new);

        return convertToDetailsDto(data);
    }

    @Override
    public void updateAuthor(String id, AuthorSaveDto dto) {
        validateAuthor(dto);

        AuthorData data = authorRepository.findById(id)
                .orElseThrow(AuthorNotFoundException::new);

        if(!data.getName().equals(dto.getName())) {
            data.setName(dto.getName());
        }
        data.setBirthdayYear(dto.getBirthdayYear());
        data.setSavedAt(Instant.now());

        try {
            authorRepository.save(data);
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e.getMessage().contains("duplicate key"))
                throw new AuthorAlreadyExistsException();
            throw e;
        }
    }

    @Override
    public void deleteAuthor(String id) {
        AuthorData data = authorRepository.findById(id)
                .orElseThrow(AuthorNotFoundException::new);
        authorRepository.deleteById(id);
    }

    public static AuthorDetailsDto convertToDetailsDto(AuthorData data) {
        return AuthorDetailsDto.builder()
                .id(data.getId())
                .name(data.getName())
                .birthdayYear(data.getBirthdayYear())
                .savedAt(data.getSavedAt())
                .build();
    }

    private static void validateAuthor(AuthorSaveDto dto) {
        if (dto.getBirthdayYear() != null
                && dto.getBirthdayYear() > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("birthYear should be before now");
        }
    }

    private void updateDataFromDto(AuthorData data, AuthorSaveDto dto) {
        data.setName(dto.getName());
        data.setBirthdayYear(dto.getBirthdayYear());
        data.setSavedAt(Instant.now());
    }
}