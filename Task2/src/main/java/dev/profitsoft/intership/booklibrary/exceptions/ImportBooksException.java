package dev.profitsoft.intership.booklibrary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Upload books failed")
public class ImportBooksException extends RuntimeException {
    public ImportBooksException(String msg) {
        super(msg);
    }

    public ImportBooksException() {
        super();
    }
}