package com.leonardo.dscommerce.DTO;

import com.leonardo.dscommerce.entities.Order;
import com.leonardo.dscommerce.entities.OrderItem;
import com.leonardo.dscommerce.entities.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private Long id;
    private Instant moment;
    private OrderStatus status;
    private ClientDTO client;
    private PaymentDTO payment;
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO(Long id, Instant moment, OrderStatus status, ClientDTO client, PaymentDTO payment) {
        this.id = id;
        this.moment = moment;
        this.status = status;
        this.client = client;
        this.payment = payment;
    }

    public OrderDTO(Order entity){
        id = entity.getId();
        moment = entity.getMoment();
        client = new ClientDTO(entity.getClient());
        status = entity.getStatus();
        payment = (entity.getPayment() == null) ? null : new PaymentDTO(entity.getPayment());
        for(OrderItem item : entity.getItems()){
            OrderItemDTO itemDTO = new OrderItemDTO(item);
            items.add(itemDTO);
        }
    }

    public Long getId() {
        return id;
    }

    public Instant getMoment() {
        return moment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public ClientDTO getClient() {
        return client;
    }

    public PaymentDTO getPayment() {
        return payment;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public Double getTotal(){
        Double sum = 0.0;
        for(OrderItemDTO item : items){
            sum += item.getSubtotal();
        }
        return sum;
    }
}
