package dev.profitsoft.intership.booklibrary.data;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "author")
public class AuthorData {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String name;
    @Column(name = "birthday_year")
    private Integer birthdayYear;

    @Column(name = "saved_at")
    private Instant savedAt;
}