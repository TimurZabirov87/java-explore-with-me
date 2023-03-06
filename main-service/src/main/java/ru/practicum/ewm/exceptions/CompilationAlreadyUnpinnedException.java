package ru.practicum.ewm.exceptions;

public class CompilationAlreadyUnpinnedException extends RuntimeException {

    public CompilationAlreadyUnpinnedException(String s) {
        super(s);
    }
}
