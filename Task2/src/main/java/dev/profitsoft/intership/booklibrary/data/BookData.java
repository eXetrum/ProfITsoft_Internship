package dev.profitsoft.intership.booklibrary.data;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "book")
public class BookData {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String title;

    @Column(name = "publish_year")
    private Integer publishYear;

    private String genre;

    @ManyToOne
    private AuthorData author;

    @Column(name = "saved_at")
    private Instant savedAt;
}