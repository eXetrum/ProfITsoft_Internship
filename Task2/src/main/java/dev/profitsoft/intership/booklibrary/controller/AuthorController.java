package dev.profitsoft.intership.booklibrary.controller;

import dev.profitsoft.intership.booklibrary.dto.*;
import dev.profitsoft.intership.booklibrary.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<AuthorPaginationDto> getAll(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "5", required = false) int size
    ) {

        AuthorQueryDto queryDto = new AuthorQueryDto();
        queryDto.setPage(page);
        queryDto.setSize(size);
        AuthorPaginationDto authors = authorService.searchAuthors(queryDto);
        return ResponseEntity.ok().body(authors);
    }

    @GetMapping("/{id}")
    public AuthorDetailsDto getAuthorById(@PathVariable("id") String id) {
        return authorService.getAuthor(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDetailsDto createAuthor(@Valid @RequestBody AuthorSaveDto dto) {
        return authorService.createAuthor(dto);
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

    @PostMapping("/_list")
    public ResponseEntity<AuthorPaginationDto> searchAuthorsPaginate(@RequestBody AuthorQueryDto authorQueryDto) {
        AuthorPaginationDto authors = authorService.searchAuthors(authorQueryDto);
        return ResponseEntity.ok().body(authors);
    }

}
