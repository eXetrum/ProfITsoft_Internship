package dev.profitsoft.intership.booklibrary.service;

import org.springframework.web.multipart.MultipartFile;
import dev.profitsoft.intership.booklibrary.dto.BookDetailsDto;
import dev.profitsoft.intership.booklibrary.dto.BookPaginationDto;
import dev.profitsoft.intership.booklibrary.dto.BookQueryDto;
import dev.profitsoft.intership.booklibrary.dto.BookSaveDto;

import java.util.List;

public interface BookService {

    List<BookDetailsDto> getAllBooks();

    String createBook(BookSaveDto dto);

    BookDetailsDto getBook(String id);

    void updateBook(String id, BookSaveDto dto);

    void deleteBook(String id);

    BookPaginationDto searchBooks(BookQueryDto dto);

    String uploadFromFile(MultipartFile multipart);
}
