package ru.practicum.ewm.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;
    private String status;
    private String reason;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;



    public ApiError(String reason, String message) {
        this.message = message;
        this.reason = reason;
    }

    public ApiError(String status, String reason, String message) {
        this.status = status;
        this.message = message;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }
}
