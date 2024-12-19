package org.atlas.service;

import java.util.List;
import org.atlas.model.OrderViewStats;

/**
 * Интерфейс для работы со статистикой просмотров заказов. Предоставляет методы для записи просмотров заказов и
 * получения статистики просмотров.
 */
public interface OrderViewStatsService {

    /**
     * Записывает факт просмотра заказа.
     *
     * @param orderId Идентификатор заказа, который был просмотрен. Должен быть не null.
     */
    void recordOrderView(Long orderId);

    /**
     * Получает статистику просмотров для указанного заказа.
     *
     * @param orderId Идентификатор заказа, для которого требуется получить статистику. Должен быть не null.
     * @return Список объектов {@link OrderViewStats}, содержащих информацию о просмотрах указанного заказа. Если для
     * заказа не зафиксировано ни одного просмотра, возвращается пустой список.
     */
    List<OrderViewStats> getOrderViewStats(Long orderId);

    /**
     * Удаляет статистику заказ по идентификатору заказа.
     *
     * @param orderId Идентификатор заказа, который необходимо удалить.
     */
    void deleteByOrderId(Long orderId);
}
