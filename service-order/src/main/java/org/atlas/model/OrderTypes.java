package org.atlas.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderTypes {

    TUTORING("Репетиторство"),
    CLEAN_UP("Уборка"),
    MINOR_REPAIRS("Мелкий ремонт"),
    GRAPHIC_DESIGN("Графический дизайн"),
    PHOTOGRAPHER_SERVICES("Услуги фотографа"),
    CULINARY_MASTER_CLASSES("Кулинарные мастер-классы"),
    TRANSFER("Переводы"),
    FITNESS_TRAINER("Фитнес-тренер"),
    COMPUTER_REPAIR_SERVICES("Услуги по ремонту компьютеров"),
    CHILDCARE_SERVICES("Услуги по уходу за детьми"),
    PSYCHOLOGY_CONSULTATIONS("Консультации по психологии"),
    MANICURE_SERVICES("Услуги по маникюру"),
    ELDERLY_CARE_SERVICES("Услуги по уходу за пожилыми людьми");

    private final String title;

    public static List<OrderTypes> getOrderTypesByString(Collection<String> orderTypes) {
        List<OrderTypes> result = new ArrayList<>();
        for (String orderType : orderTypes) {
            result.add(OrderTypes.valueOf(orderType));
        }
        return result;
    }
}
