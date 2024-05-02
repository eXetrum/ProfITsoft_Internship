package dev.profitsoft.intership.booklibrary.controller;

import dev.profitsoft.intership.booklibrary.dto.AuthorDetailsDto;
import dev.profitsoft.intership.booklibrary.dto.AuthorSaveDto;
import dev.profitsoft.intership.booklibrary.dto.RestResponse;
import dev.profitsoft.intership.booklibrary.service.AuthorService;
import dev.profitsoft.intership.booklibrary.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/{id}")
    public AuthorDetailsDto getAuthorById(@PathVariable("id") String id) {
        return authorService.getAuthor(id);
    }

    @GetMapping
    public List<AuthorDetailsDto> getAll() {
        return authorService.getAllAuthors();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createAuthor(@Valid @RequestBody AuthorSaveDto dto) {
        String id = authorService.createAuthor(dto);
        return new RestResponse(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestResponse updateAuthor(@PathVariable("id") String id, @Valid @RequestBody AuthorSaveDto dto) {
        authorService.updateAuthor(id, dto);
        return new RestResponse("OK");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestResponse deleteAuthor(@PathVariable("id") String id) {
        authorService.deleteAuthor(id);
        return new RestResponse("OK");
    }
}
