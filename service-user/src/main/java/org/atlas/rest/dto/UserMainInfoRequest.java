package org.atlas.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMainInfoRequest {

    @NotNull(message = "Id пользователя не может быть пустым")
    private Long id;

    @Size(min = 4, max = 50, message = "Имя пользователя должно содержать от 4 до 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия пользователя не может быть пустой")
    private String lastName;

    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    private LocalDate birthday;

    private String gender;

}
