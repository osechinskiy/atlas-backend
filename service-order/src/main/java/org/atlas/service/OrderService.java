package org.atlas.service;

import java.util.Collection;
import java.util.List;
import org.atlas.exception.OrderNotFoundException;
import org.atlas.model.Order;
import org.atlas.model.OrderTypes;
import org.atlas.rest.dto.NewOrderRequest;
import org.atlas.model.dto.OrderDto;

/**
 * Интерфейс для управления заказами в системе
 */
public interface OrderService {

    /**
     * Находит все заказы, связанные с указанным идентификатором пользователя.
     *
     * @param userId Идентификатор пользователя, для которого необходимо найти заказы.
     * @return Список заказов, связанных с указанным пользователем. Если заказы не найдены, возвращается пустой список.
     */
    List<Order> findOrdersByUserId(Long userId);

    /**
     * Находит все заказы, принадлежащие указанным категориям.
     *
     * @param userId уникальный идентификатор пользователя
     * @return Список заказов, соответствующих указанным категориям. Если заказы не найдены, возвращается пустой список.
     */
    List<Order> findOrdersByCategories(Long userId);

    /**
     * Находит заказ по его идентификатору.
     *
     * @param orderId Идентификатор заказа, который необходимо найти.
     * @return Объект OrderDto, представляющий найденный заказ. Если заказ не найден, может быть возвращено значение
     * null или выброшено исключение.
     * @throws OrderNotFoundException Если заказ с указанным идентификатором не найден.
     */
    OrderDto findOrderById(Long orderId);

    /**
     * Создает новый заказ на основе предоставленных данных.
     *
     * @param request Объект, содержащий информацию о новом заказе.
     */
    void createOrder(NewOrderRequest request);

    /**
     * Обновляет существующий заказ.
     *
     * @param order Объект заказа с обновленными данными.
     * @throws OrderNotFoundException Если заказ с указанным идентификатором не найден.
     */
    void updateOrder(Order order);

    /**
     * Удаляет заказ по его идентификатору.
     *
     * @param orderId Идентификатор заказа, который необходимо удалить.
     */
    void deleteOrder(Long orderId);
}
