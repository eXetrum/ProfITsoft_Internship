package dev.profitsoft.intership.booklibrary.repository;

import dev.profitsoft.intership.booklibrary.data.AuthorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorData, String> {
    AuthorData findByName(String name);
}