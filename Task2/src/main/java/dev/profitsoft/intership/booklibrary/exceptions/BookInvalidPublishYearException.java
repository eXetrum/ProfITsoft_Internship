package dev.profitsoft.intership.booklibrary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="publishYear should be before now")
public class BookInvalidPublishYearException extends RuntimeException {
}