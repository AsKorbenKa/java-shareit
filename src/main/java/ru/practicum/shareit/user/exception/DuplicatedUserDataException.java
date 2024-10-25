package ru.practicum.shareit.user.exception;

public class DuplicatedUserDataException extends RuntimeException {
    public DuplicatedUserDataException(final String message) {
        super(message);
    }
}
