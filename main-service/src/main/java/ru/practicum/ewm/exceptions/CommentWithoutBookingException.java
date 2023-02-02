package ru.practicum.ewm.exceptions;

public class CommentWithoutBookingException extends RuntimeException {

    public CommentWithoutBookingException(String s) {
        super(s);
    }
}
