package com.leonardo.dscommerce.controllers;

import com.leonardo.dscommerce.services.ProductService;
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
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService service;

    private String productName;

    @BeforeEach
    void setUp() throws Exception{
        productName = "Macbook";
    }


    @Test
    public void findAllShouldReturnPagedWhenNameParamIsEmpty() throws Exception{
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception{
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.get("/products?name={productName}", productName)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(3));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"));
    }

}
