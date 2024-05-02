package dev.profitsoft.intership.booklibrary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Author with specified name already exists")
public class AuthorAlreadyExistsException extends RuntimeException {
}