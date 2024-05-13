package dev.profitsoft.intership.booklibrary.repository;

import dev.profitsoft.intership.booklibrary.data.BookData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookData, String>, JpaSpecificationExecutor<BookData> {

    @Query("SELECT COUNT(book) = 1 FROM BookData AS book " +
            "WHERE book.title = :title " +
            "AND book.genre = :genre " +
            "AND book.publishYear = :publishYear " +
            "AND book.author.name = :authorName " +
            "AND book.author.birthdayYear = :authorBirthdayYear"
    )
    boolean existsBookDataByFullDescription(
            @Param("title") String title,
            @Param("genre") String genre,
            @Param("publishYear") Integer publishYear,
            @Param("authorName") String authorName,
            @Param("authorBirthdayYear") Integer authorBirthdayYear
    );
}