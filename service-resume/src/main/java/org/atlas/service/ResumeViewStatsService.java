package org.atlas.service;

import java.util.List;
import org.atlas.model.ResumeViewStats;

/**
 * Интерфейс для работы со статистикой просмотров резюме. Предоставляет методы для записи просмотров и получения
 * статистики просмотров.
 */
public interface ResumeViewStatsService {

    /**
     * Записывает факт просмотра анкеты.
     *
     * @param resumeId Идентификатор резюме, который был просмотрен. Должен быть не null.
     */
    void recordResumeView(Long resumeId);

    /**
     * Получает статистику просмотров для указанной резюме.
     *
     * @param resumeId Идентификатор резюме, для которого требуется получить статистику. Должен быть не null.
     * @return Список объектов {@link ResumeViewStats}, содержащих информацию о просмотрах указанного резюме. Если для
     * резюме не зафиксировано ни одного просмотра, возвращается пустой список.
     */
    List<ResumeViewStats> getOrderViewStats(Long resumeId);

    /**
     * Удаляет статистику по идентификатору резюме.
     *
     * @param resumeId Идентификатор резюме, который необходимо удалить.
     */
    void deleteBySpecialistId(Long resumeId);
}
