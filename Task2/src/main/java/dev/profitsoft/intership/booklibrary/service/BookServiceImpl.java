package dev.profitsoft.intership.booklibrary.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.profitsoft.intership.booklibrary.data.AuthorData;
import dev.profitsoft.intership.booklibrary.data.BookData;
import dev.profitsoft.intership.booklibrary.dto.*;
import dev.profitsoft.intership.booklibrary.exceptions.*;
import dev.profitsoft.intership.booklibrary.repository.AuthorRepository;
import dev.profitsoft.intership.booklibrary.repository.BookRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
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
    public String createBook(BookSaveDto dto) {
        validateBook(dto);
        BookData data = new BookData();
        updateDataFromDto(data, dto);

        if(!isUniqueBookData(data))
            throw new BookAlreadyExistsException();

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
    public BookPaginationDto searchBooks(BookQueryDto queryDto) {
        Pageable pageable = PageRequest.of(queryDto.getPage(), queryDto.getSize());
        Page<BookData> booksPage = bookRepository.findAll(createBookSpecification(queryDto), pageable);
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
    private Specification<BookData> createBookSpecification(BookQueryDto queryDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (queryDto.getAuthorId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("author").get("id"), queryDto.getAuthorId()));
            } else if (queryDto.getAuthorName() != null) {
                predicates.add(criteriaBuilder.like(root.get("author").get("name"), "%" + queryDto.getAuthorName() + "%"));
            }

            if (queryDto.getTitle() != null) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + queryDto.getTitle() + "%"));
            }

            if (queryDto.getGenre() != null) {
                predicates.add(criteriaBuilder.like(root.get("genre"), "%" + queryDto.getGenre() + "%"));
            }

            if (queryDto.getPublishYear() != null) {
                predicates.add(criteriaBuilder.equal(root.get("publishYear"), queryDto.getPublishYear()));
            }

            query.orderBy(criteriaBuilder.asc(root.get("title")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
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
        return !bookRepository.existsBookDataByFullDescription(
                book.getTitle(),
                book.getGenre(),
                book.getPublishYear(),
                book.getAuthor().getName(),
                book.getAuthor().getBirthdayYear()
        );
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
                .totalItems(booksPage.getTotalElements())
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
            throw new BookInvalidPublishYearException();
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
