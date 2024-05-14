package dev.profitsoft.intership.booklibrary.controller;

import dev.profitsoft.intership.booklibrary.dto.*;
import dev.profitsoft.intership.booklibrary.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<BookPaginationDto> getAll(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "5", required = false) int size) {

        BookQueryDto queryDto = new BookQueryDto();
        queryDto.setPage(page);
        queryDto.setSize(size);
        BookPaginationDto books = bookService.searchBooks(queryDto);
        return ResponseEntity.ok().body(books);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createBook(@Valid @RequestBody BookSaveDto dto) {
        String id = bookService.createBook(dto);
        return new RestResponse(id);
    }

    @GetMapping("/{id}")
    public BookDetailsDto getBookById(@PathVariable("id") String id) {
        return bookService.getBook(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestResponse updateBook(@PathVariable("id") String id, @Valid @RequestBody BookSaveDto dto) {
        bookService.updateBook(id, dto);
        return new RestResponse("OK");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestResponse deleteBook(@PathVariable("id") String id) {
        bookService.deleteBook(id);
        return new RestResponse("OK");
    }

    @PostMapping("/_list")
    public ResponseEntity<BookPaginationDto> searchBooksPaginate(@RequestBody BookQueryDto bookQueryDto) {
        BookPaginationDto books = bookService.searchBooks(bookQueryDto);
        return ResponseEntity.ok().body(books);
    }

    @PostMapping("/_report")
    public void generateReport(@RequestBody BookQueryDto bookQueryDto, HttpServletResponse response) {
        bookQueryDto.setPage(0);
        bookQueryDto.setSize(Integer.MAX_VALUE);
        BookPaginationDto books = bookService.searchBooks(bookQueryDto);

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.csv");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("title,genre,publishYear,authorName\n");
            for (BookDetailsDto book : books.getList()) {
                sb.append(book.getTitle())
                        .append(",")
                        .append(book.getGenre())
                        .append(",")
                        .append(book.getPublishYear())
                        .append(",")
                        .append(book.getAuthor().getName())
                        .append("\n");
            }
            response.getOutputStream().write(sb.toString().getBytes());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("Error generating text file", e);
        }
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse uploadFile(@RequestParam("file") MultipartFile multipart) {
        return new RestResponse(bookService.uploadFromFile(multipart));
    }
}
