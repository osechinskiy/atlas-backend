package org.atlas.mapper;

import java.util.Collection;
import java.util.List;
import org.atlas.model.Order;
import org.atlas.model.dto.OrderDto;
import org.atlas.model.OrderInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "phoneId", source = "userPhone")
    Order map(OrderDto orderDto);

    @Mapping(target = "userPhone", source = "phoneId")
    OrderDto map(Order order);

    List<OrderDto> mapOrders(Collection<Order> orders);

    List<Order> mapOrderDtos(Collection<OrderDto> orderDtos);

    @Mapping(target = "orderId", source = "id")
    OrderInfo mapOrderInfo(Order order);
}
