package dev.profitsoft.intership.booklibrary.repository;

import dev.profitsoft.intership.booklibrary.data.BookData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookData, String> {

    @Query("SELECT book FROM BookData AS book WHERE book.author.id = :authorId")
    Page<BookData> findByAuthorId(@Param("authorId") String authorId, Pageable pageable);

    @Query("SELECT book FROM BookData AS book WHERE LOWER(book.author.name) LIKE LOWER(concat('%', :authorName, '%'))")
    Page<BookData> findByAuthorName(@Param("authorName") String authorName, Pageable pageable);

    @Query("SELECT book FROM BookData AS book " +
            "WHERE book.title = :title " +
            "AND book.genre = :genre " +
            "AND book.publishYear = :publishYear " +
            "AND book.author.name = :authorName " +
            "AND book.author.birthdayYear = :authorBirthdayYear"
    )
    List<BookData> findByFullDescription(
            @Param("title") String title,
            @Param("genre") String genre,
            @Param("publishYear") Integer publishYear,
            @Param("authorName") String authorName,
            @Param("authorBirthdayYear") Integer authorBirthdayYear
    );
}