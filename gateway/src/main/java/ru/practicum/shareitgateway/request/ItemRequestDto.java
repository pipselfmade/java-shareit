package ru.practicum.shareitgateway.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;

    @NotBlank
    @Size(max = 250)
    private String description;

    private Long requestorId;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDateTime created;
}
