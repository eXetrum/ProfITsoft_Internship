package dev.profitsoft.intership.booklibrary.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.profitsoft.intership.booklibrary.data.AuthorData;
import dev.profitsoft.intership.booklibrary.data.BookData;
import dev.profitsoft.intership.booklibrary.dto.*;
import dev.profitsoft.intership.booklibrary.exceptions.AuthorNotFoundException;
import dev.profitsoft.intership.booklibrary.exceptions.BookAlreadyExistsException;
import dev.profitsoft.intership.booklibrary.exceptions.BookNotFoundException;
import dev.profitsoft.intership.booklibrary.exceptions.ImportBooksException;
import dev.profitsoft.intership.booklibrary.repository.AuthorRepository;
import dev.profitsoft.intership.booklibrary.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<BookDetailsDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookServiceImpl::convertToDetailsDto)
                .collect(Collectors.toList());
    }

    @Override
    public String createBook(BookSaveDto dto) {
        validateBook(dto);
        BookData data = new BookData();
        updateDataFromDto(data, dto);

        ensureUniqueBook(data);

        data.setId(UUID.randomUUID().toString());
        BookData savedData = bookRepository.save(data);
        return savedData.getId();
    }

    @Override
    public BookDetailsDto getBook(String id) {
        BookData data = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);

        return convertToDetailsDto(data);
    }

    @Override
    public void updateBook(String id, BookSaveDto dto) {
        validateBook(dto);

        BookData data = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);

        updateDataFromDto(data, dto);
        bookRepository.save(data);
    }

    @Override
    public void deleteBook(String id) {
        bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);
        bookRepository.deleteById(id);
    }

    @Override
    public BookPaginationDto searchBooks(BookQueryDto dto) {
        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());
        Page<BookData> booksPage = null;
        if (dto.getAuthorId() != null) {
            booksPage = bookRepository.findByAuthorId(dto.getAuthorId(), pageable);
        } else if (dto.getAuthorName() != null) {
            booksPage = bookRepository.findByAuthorName(dto.getAuthorName(), pageable);
        } else {
            booksPage = bookRepository.findByAuthorId("", pageable);
        }
        return convertToPaginationDto(booksPage);
    }

    @Override
    public String uploadFromFile(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            List<BookData> books = objectMapper.readValue(fileBytes, new TypeReference<List<BookUploadDto>>() {})
                    .stream()
                    .map(this::convertFromUpload)
                    .toList();

            int total = books.size();
            books = books.stream()
                    .filter(this::isCompleteBookData)
                    .filter(this::isUniqueBookData)
                    .toList();

            int saved = 0;
            if(!books.isEmpty())
                saved = bookRepository.saveAll(books).size();

            return String.format("Saved: %d, Total: %d", saved, total);
        } catch (Exception e) {
            throw new ImportBooksException(e.getMessage());
        }
    }

    //// Utils
    private void ensureUniqueBook(BookData book) {
        List<BookData> exists = bookRepository.findByFullDescription(
                book.getTitle(),
                book.getGenre(),
                book.getPublishYear(),
                book.getAuthor().getName(),
                book.getAuthor().getBirthdayYear()
        );

        if(exists != null && !exists.isEmpty()) {
            throw new BookAlreadyExistsException();
        }
    }
    // Returns true if specified book is complete object (object that ready to push into repo)
    private boolean isCompleteBookData(BookData book) {
        return book.getPublishYear() != null
                && book.getTitle() != null
                && book.getGenre() != null
                && book.getAuthor() != null
                && book.getAuthor().getName() != null
                && book.getAuthor().getBirthdayYear() != null;
    }

    // Returns true if specified book is unique (no duplicates allowed)
    private boolean isUniqueBookData(BookData book) {
        List<BookData> queryResult = bookRepository.findByFullDescription(
                book.getTitle(),
                book.getGenre(),
                book.getPublishYear(),
                book.getAuthor().getName(),
                book.getAuthor().getBirthdayYear()
        );
        return queryResult == null || queryResult.isEmpty();
    }

    private BookData convertFromUpload(BookUploadDto dto) {
        BookData bookData = new BookData();
        bookData.setId(UUID.randomUUID().toString());
        bookData.setTitle(dto.getTitle());
        bookData.setGenre(dto.getGenre());
        bookData.setPublishYear(dto.getPublishYear());
        bookData.setAuthor(getOrCreateAuthor(dto.getAuthor()));
        bookData.setSavedAt(Instant.now());

        return bookData;
    }

    // Get from repo or put new author into repo and return saved author
    private AuthorData getOrCreateAuthor(AuthorDetailsDto dto) {
        AuthorData repoAuthor = authorRepository.findByName(dto.getName());
        if(repoAuthor == null) {
            AuthorData authorData = new AuthorData();
            authorData.setId(UUID.randomUUID().toString());
            authorData.setName(dto.getName());
            authorData.setBirthdayYear(dto.getBirthdayYear());
            authorData.setSavedAt(Instant.now());
            authorRepository.save(authorData);
            return authorData;
        }
        return repoAuthor;
    }

    private static BookPaginationDto convertToPaginationDto(Page<BookData> booksPage) {
        return BookPaginationDto.builder()
                .list(booksPage.getContent()
                        .stream()
                        .map(BookServiceImpl::convertToDetailsDto)
                        .collect(Collectors.toList())
                )
                .totalPages(booksPage.getTotalPages())
                .build();
    }

    private static BookDetailsDto convertToDetailsDto(BookData data) {
        return BookDetailsDto.builder()
                .id(data.getId())
                .title(data.getTitle())
                .genre(data.getGenre())
                .publishYear(data.getPublishYear())
                .savedAt(data.getSavedAt())
                .author(AuthorServiceImpl.convertToDetailsDto(data.getAuthor()))
                .build();
    }

    private static void validateBook(BookSaveDto dto) {
        if (dto.getPublishYear() != null
                && dto.getPublishYear() > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("publishYear should be before now");
        }
    }

    private void updateDataFromDto(BookData data, BookSaveDto dto) {
        data.setTitle(dto.getTitle());
        data.setGenre(dto.getGenre());
        data.setPublishYear(dto.getPublishYear());
        data.setAuthor(authorRepository
                        .findById(dto.getAuthorId())
                        .orElseThrow(AuthorNotFoundException::new)
        );
        data.setSavedAt(Instant.now());
    }
}
