package ru.practicum.ewm.exceptions;

public class LimitOfRequestsReachedException extends RuntimeException {

    public LimitOfRequestsReachedException(String s) {
        super(s);
    }
}
