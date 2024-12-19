package org.atlas.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserPhoneRequest {

    @NotNull(message = "Id номера телефона не может быть пустым")
    private Long id;

    @NotNull(message = "Id пользователя не может быть пустым")
    private Long userId;

    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(regexp = "(^$|[0-9]{11})", message = "Невалидный номер телефона")
    private String phone;
}
