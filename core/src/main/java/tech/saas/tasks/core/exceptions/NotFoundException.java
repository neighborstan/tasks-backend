package tech.saas.tasks.core.exceptions;

import lombok.RequiredArgsConstructor;


public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
