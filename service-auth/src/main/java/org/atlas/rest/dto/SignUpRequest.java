package org.atlas.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @Size(min = 4, max = 50, message = "Имя пользователя должно содержать от 4 до 50 символов")
    private String firstName;

    @NotBlank(message = "Фамилия пользователя не может быть пустой")
    private String lastName;

    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}
