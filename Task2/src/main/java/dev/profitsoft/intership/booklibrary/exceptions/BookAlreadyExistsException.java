package dev.profitsoft.intership.booklibrary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Book with specified attributes already exists")
public class BookAlreadyExistsException extends RuntimeException {
}