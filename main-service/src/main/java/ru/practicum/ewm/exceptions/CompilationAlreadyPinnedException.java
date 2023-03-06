package ru.practicum.ewm.exceptions;

public class CompilationAlreadyPinnedException extends RuntimeException {

    public CompilationAlreadyPinnedException(String s) {
        super(s);
    }
}
