package com.leonardo.dscommerce.repositories;

import com.leonardo.dscommerce.entities.Order;
import com.leonardo.dscommerce.entities.OrderItem;
import com.leonardo.dscommerce.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
}
