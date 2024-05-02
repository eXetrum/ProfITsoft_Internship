package dev.profitsoft.intership.booklibrary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Book not found")
public class BookNotFoundException extends RuntimeException {
}