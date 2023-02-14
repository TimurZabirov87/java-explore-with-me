package ru.practicum.ewm.exceptions;

public class RequestForUnpublishedEventException extends RuntimeException {

    public RequestForUnpublishedEventException(String s) {
        super(s);
    }
}
