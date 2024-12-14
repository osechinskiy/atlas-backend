package org.atlas.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представляет запрос на создание нового заказа. Этот класс содержит всю необходимую информацию, требуемую для
 * размещения нового заказа.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewOrderRequest {

    /**
     * Уникальный идентификатор пользователя, размещающего заказ.
     */
    private long userId;


    /**
     * Id телефона пользователя. Не может быть пустым.
     */
    @NotNull(message = "Номер телефона пользователя не может быть пустым")
    private Long phoneId;

    /**
     * Адрес пользователя. Не может быть пустым или состоять только из пробелов.
     */
    @NotBlank(message = "Адрес пользователя не может быть пустым")
    private String userAddress;

    /**
     * Информация о местоположении пользователя. Не может быть пустой или состоять только из пробелов.
     */
    @NotEmpty(message = "Местоположение пользователя не может быть пустым")
    private Float [] userLocation;

    /**
     * Категория заказа, который размещается. Не может быть пустой или состоять только из пробелов.
     */
    @NotBlank(message = "Категория заказа не может быть пустой")
    private String orderCategory;

    /**
     * Название заказа, который размещается. Не может быть пустым или состоять только из пробелов.
     */
    @NotBlank(message = "Название заказа не может быть пустым")
    private String orderName;

    /**
     * Подробное описание заказа. Не может превышать 1000 символов.
     */
    @NotBlank
    @Size(max = 1000, message = "Описание заказа не может превышать 1000 символов")
    private String orderDescription;

    /**
     * Дата, к которой заказ должен быть завершен. Должна быть датой в будущем.
     */
    private LocalDate finishOrderDate;

    /**
     * Количество заказа. Должно быть положительным числом.
     */
    @Positive(message = "Сумма заказа должно быть положительным")
    private int orderAmount;
}
