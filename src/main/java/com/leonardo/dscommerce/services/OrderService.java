package com.leonardo.dscommerce.services;

import com.leonardo.dscommerce.DTO.OrderDTO;
import com.leonardo.dscommerce.repositories.OrderRepository;
import com.leonardo.dscommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id){
        OrderDTO dto = new OrderDTO(repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found")
        ));
        return dto;
    }

}
