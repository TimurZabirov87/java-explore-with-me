package ru.practicum.ewm.model.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentUpdateUserRequest {
    @NotNull
    @Size(min = 20, max = 2000)
    private String text;
}
