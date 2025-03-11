package com.leonardo.dscommerce.services;

import com.leonardo.dscommerce.DTO.OrderDTO;
import com.leonardo.dscommerce.Factory;
import com.leonardo.dscommerce.entities.Order;
import com.leonardo.dscommerce.entities.OrderItem;
import com.leonardo.dscommerce.entities.Product;
import com.leonardo.dscommerce.entities.User;
import com.leonardo.dscommerce.repositories.OrderItemRepository;
import com.leonardo.dscommerce.repositories.OrderRepository;
import com.leonardo.dscommerce.repositories.ProductRepository;
import com.leonardo.dscommerce.services.exceptions.ForbiddenException;
import com.leonardo.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    private Long existingOrderId, nonExistingOrderId, existingProductId, nonExistingProductId;
    private Order order;
    private OrderDTO orderDTO;
    private Product product;
    private User admin, client;

    @BeforeEach
    void setUp() throws Exception{
        existingOrderId = 1L;
        nonExistingOrderId = 2000L;
        existingProductId = 1L;
        nonExistingProductId = 1000L;
        admin = Factory.createCustomAdmin(1L, "Jeff");
        client = Factory.createCustomClient(2L, "Bob");
        order = Factory.createOrder(client);
        orderDTO = new OrderDTO(order);
        product = Factory.createProduct();

        //Order Mock
        Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(repository.findById(nonExistingOrderId)).thenReturn(Optional.empty());
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(order);
        //Product Mock
        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);
        //OrderItem Mock
        Mockito.when(orderItemRepository.saveAll(ArgumentMatchers.any())).thenReturn(new ArrayList<>(order.getItems()));
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged(){
        Mockito.doNothing().when(authService).validateSelfOrAdmin(ArgumentMatchers.any());
        OrderDTO result = service.findById(existingOrderId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndSelfClientLogged(){
        Mockito.doNothing().when(authService).validateSelfOrAdmin(ArgumentMatchers.any());
        OrderDTO result = service.findById(existingOrderId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    public void findByIdShouldThrowsForbiddenExceptionWhenIdExistsAndOtherClientLogged(){
        Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(ArgumentMatchers.any());

        Assertions.assertThrows(ForbiddenException.class, () -> {
            service.findById(existingOrderId);
        });
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingOrderId);
        });
    }

    @Test
    public void insertShouldReturnOrderDTOWhenAdminIsLogged(){
        Mockito.when(userService.authenticated()).thenReturn(admin);
        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void insertShouldReturnOrderDTOWhenClientIsLogged(){
        Mockito.when(userService.authenticated()).thenReturn(client);
        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    public void insertShouldThrowsUsernameNotFoundWhenUserIsNotLogged(){
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            order.setClient(new User());
            service.insert(orderDTO);
        });
    }

    @Test
    public void insertShouldThrowsEntityNotFoundWhenOrderProductIdDoesNotExists(){
        Mockito.when(userService.authenticated()).thenReturn(client);

        product.setId(nonExistingProductId);
        OrderItem orderItem = new OrderItem(order, product, 1, 4500.00);
        order.getItems().add(orderItem);
        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
           service.insert(orderDTO);
        });
    }

}
