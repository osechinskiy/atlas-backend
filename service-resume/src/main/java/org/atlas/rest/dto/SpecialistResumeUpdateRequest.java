package org.atlas.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistResumeUpdateRequest {

    @NotNull(message = "Номер телефона не может быть пустым")
    private Long userPhoneId;

    @NotBlank(message = "Категория выполняемых работ не может быть пустой")
    private String category;

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 150, message = "Длина названия должна быть не более 150 символов")
    private String title;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 500, message = "Длина описания должна быть не более 500 символов")
    private String description;

    @NotNull(message = "Нужно указать опыт работы")
    private Integer experience;
}
