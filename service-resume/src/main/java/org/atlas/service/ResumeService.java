package org.atlas.service;

import java.util.List;
import org.atlas.exception.ResumeNotFound;
import org.atlas.model.TypeOfPerformed;
import org.atlas.model.dto.ResumeDto;
import org.atlas.rest.dto.CreateResumeRequest;
import org.atlas.rest.dto.SpecialistResumeUpdateRequest;


/**
 * Сервисный интерфейс для управления резюме.
 */
public interface ResumeService {

    /**
     * Возвращает все резюме по указанной категории.
     *
     * @param typeOfPerformed категория специалистов, которые должны быть получены
     * @return список объектов {@link ResumeDto}, которые принадлежат указанной категории
     */
    List<ResumeDto> getAllResumeByCategory(TypeOfPerformed typeOfPerformed);

    /**
     * Возвращает резюме по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор резюме
     * @return объект {@link ResumeDto}, соответствующий указанному идентификатору
     * @throws ResumeNotFound если резюме не найден
     */
    ResumeDto getResumeById(Long id);

    /**
     * Возвращает список резюме по уникальному идентификатору пользователя
     *
     * @param userId уникальный идентификатор пользователя
     * @return список резюме
     */
    List<ResumeDto> getResumeListByUserId(Long userId);

    /**
     * Создает новое резюме.
     *
     * @param request объект {@link CreateResumeRequest}, представляющий создаваемого резюме
     */
    void createResume(CreateResumeRequest request);

    /**
     * Обновляет данные существующего резюме.
     *
     * @param request объект {@link SpecialistResumeUpdateRequest}, представляющий резюме с обновленными данными
     */
    void updateResume(Long resumeId, SpecialistResumeUpdateRequest request);

    /**
     * Удаляет резюме по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор резюме, который должен быть удален
     */
    void deleteResumeById(Long id);

    /**
     * Возвращает тимы выполняемых работ по уникальному идентификатору пользователя
     *
     * @param userId уникальный идентификатор пользователя
     * @return список выполняемых работ
     */
    List<String> getTypesOfPerformed(Long userId);

    List<Long> getUsersByCategory(String category);
}
