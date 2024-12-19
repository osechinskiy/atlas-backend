package org.atlas.repository;

import java.util.Collection;
import java.util.List;
import org.atlas.model.Order;
import org.atlas.model.OrderTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.userId = :userId")
    List<Order> findOrdersByUserId(@Param("userId") Long userId);

    @Query("select o from Order o where o.category in (:categories)")
    List<Order> findOrdersByCategories(@Param("categories") Collection<OrderTypes> categories);

}
