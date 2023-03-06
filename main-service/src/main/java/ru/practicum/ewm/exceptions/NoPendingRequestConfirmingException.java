package ru.practicum.ewm.exceptions;

public class NoPendingRequestConfirmingException extends RuntimeException {

    public NoPendingRequestConfirmingException(String s) {
        super(s);
    }
}
