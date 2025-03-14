package com.leonardo.dscommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardo.dscommerce.TokenUtil;
import com.leonardo.dscommerce.entities.OrderStatus;
import com.leonardo.dscommerce.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService service;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String invalidToken, clientToken, clientUsername, adminToken, adminUsername, password;
    private Long existingId, nonExistingId, alexOrderId, mariaOrderId;

    @BeforeEach
    void setUp() throws Exception{
        adminUsername = "alex@gmail.com";
        clientUsername = "maria@gmail.com";
        password = "123456";
        existingId = 1L;
        nonExistingId = 50L;
        alexOrderId = 2L;
        mariaOrderId = 3L;
        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, password);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, password);
        invalidToken = adminToken+"oiduf";

    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() throws Exception{
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}", existingId)
                .header("Authorization", "Bearer "+ adminToken)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existingId));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(OrderStatus.PAID.toString()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.client").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.payment").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.items").exists());
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenIdExistsAndClientLogged() throws Exception{
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}", mariaOrderId)
                .header("Authorization", "Bearer "+ clientToken)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(mariaOrderId));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(OrderStatus.WAITING_PAYMENT.toString()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.client").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.payment").doesNotExist());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.items").exists());
    }

    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndOrderDoesNotBelongUser() throws Exception{
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}", alexOrderId)
                .header("Authorization", "Bearer "+ clientToken)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndAdminLogged() throws Exception{
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}", nonExistingId)
                .header("Authorization", "Bearer "+ adminToken)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndClientLogged() throws Exception{
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}", nonExistingId)
                .header("Authorization", "Bearer "+ clientToken)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenIsNotLogged() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/orders/{id}", alexOrderId)
                .header("Authorization", "Bearer "+ invalidToken)
                .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
