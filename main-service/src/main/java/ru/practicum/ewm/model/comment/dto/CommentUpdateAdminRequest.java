package ru.practicum.ewm.model.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.model.comment.CommentStateActionAdmin;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentUpdateAdminRequest {
    @Size(min = 20, max = 2000)
    private String text;
    private CommentStateActionAdmin action;
}
