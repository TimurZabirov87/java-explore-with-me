package ru.practicum.ewm.hit.model;


import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 1, max = 64)
    @Column(name = "app", nullable = false)
    private String app;
    @NotBlank
    @Size(min = 1, max = 64)
    @Column(name = "uri", nullable = false)
    private String uri;
    @NotBlank
    @Size(min = 1, max = 64)
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name =  "created", nullable = false)
    private LocalDateTime created;
}
