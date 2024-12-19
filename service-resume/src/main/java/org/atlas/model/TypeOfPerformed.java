package org.atlas.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TypeOfPerformed {

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

    private final String name;

}
