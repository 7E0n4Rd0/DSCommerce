package com.leonardo.dscommerce.services;

import com.leonardo.dscommerce.DTO.OrderDTO;
import com.leonardo.dscommerce.DTO.OrderItemDTO;
import com.leonardo.dscommerce.entities.Order;
import com.leonardo.dscommerce.entities.OrderItem;
import com.leonardo.dscommerce.entities.OrderStatus;
import com.leonardo.dscommerce.entities.Product;
import com.leonardo.dscommerce.repositories.OrderItemRepository;
import com.leonardo.dscommerce.repositories.OrderRepository;
import com.leonardo.dscommerce.repositories.ProductRepository;
import com.leonardo.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id){
        OrderDTO dto = new OrderDTO(repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found")
        ));
        return dto;
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setClient(userService.authenticated());

        for(OrderItemDTO itemDTO : dto.getItems()){
            Product product = productRepository.getReferenceById(itemDTO.getProductId());
            OrderItem item = new OrderItem(order, product, itemDTO.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }

        repository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);
    }

}
