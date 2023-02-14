package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.ExceptionUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ NoSuchUserException.class,
                        NoSuchEventException.class,
                        NoSuchCompilationException.class,
                        NoSuchParticipationRequestException.class,
                        NoSuchCategoryException.class})
    protected ResponseEntity<ApiError> handleEntityNotFoundEx(RuntimeException ex, WebRequest request) {
        ApiError apiError = new ApiError("NOT_FOUND","Entity Not Found Exception", ex.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ CompilationAlreadyPinnedException.class,
                        CompilationAlreadyUnpinnedException.class})
    protected ResponseEntity<ApiError> handleDataEx(RuntimeException ex, WebRequest request) {
        ApiError apiError = new ApiError("BAD_REQUEST","Request Data Exception", ex.getMessage());
        return new ResponseEntity<>(apiError, BAD_REQUEST);
    }

    @ExceptionHandler({
            IllegalEventDateTimeException.class,
            IllegalEventSortingException.class,
            IllegalEventStateException.class,
            RequesterIsInitiatorException.class,
            RequestForUnpublishedEventException.class,
            DoubleEventAddingException.class,
            NoPendingRequestConfirmingException.class,
            LimitOfRequestsReachedException.class,
            DoubleRequestException.class})
    protected ResponseEntity<ApiError> handleReqDataEx(RuntimeException ex) {
        ApiError apiError = new ApiError("CONFLICT","Request Data Exception", ex.getMessage());
        return new ResponseEntity<>(apiError, CONFLICT);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleInvalidParameterException(ConstraintViolationException ex) {
        ApiError apiError = new ApiError("BAD_REQUEST","Constraint Violation Exception", ex.getMessage());
        return new ResponseEntity<>(apiError, BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationHibernateException(DataIntegrityViolationException ex) {
        ApiError apiError = new ApiError("CONFLICT","Constraint Violation Exception", ex.getMessage());
        return new ResponseEntity<>(apiError, CONFLICT);
    }

    @ExceptionHandler(CategoryIsNotEmptyException.class)
    public ResponseEntity<ApiError> handleCategoryIsNotEmptyException(CategoryIsNotEmptyException ex) {
        ApiError apiError = new ApiError("CONFLICT","Constraint Violation Exception", ex.getMessage());
        return new ResponseEntity<>(apiError, CONFLICT);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex,
            WebRequest request) {
        ApiError apiError =
                new ApiError("BAD_REQUEST","An invalid argument was passed to the method, or a required argument was not passed.",
                        ex.getMessage());

        return new ResponseEntity<>(apiError, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(status.name(), "Malformed JSON Request", ex.getMessage());

        return new ResponseEntity<>(apiError, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

        ApiError apiError = new ApiError(status.name(),"Method Argument Not Valid", ex.getMessage());

        return new ResponseEntity<>(apiError,status);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(new ApiError(status.name(), "No Handler Found", ex.getMessage()), status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(
                new ApiError(status.name(),"Method '" + ex.getMethod() + "' not allowed", ex.getMessage()), status);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setReason(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
        apiError.setMessage(ex.getMessage());
        apiError.setStatus("BAD_REQUEST");
        apiError.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(apiError, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setReason(String.format("The parameter '%s' is required",
                ex.getParameterName()));
        apiError.setMessage(ex.getMessage());
        apiError.setStatus("BAD_REQUEST");
        apiError.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(apiError, BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        log.error(ExceptionUtils.getStackTrace(ex));
        ApiError apiError = new ApiError("INTERNAL_SERVER_ERROR","Internal Exception", ex.getMessage());

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
