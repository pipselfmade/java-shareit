package ru.practicum.shareitgateway.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotNull
    private Long id;

    private String name;

    @Email
    @NotBlank
    private String email;
}
