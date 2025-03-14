package com.leonardo.dscommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardo.dscommerce.DTO.CategoryDTO;
import com.leonardo.dscommerce.DTO.ProductDTO;
import com.leonardo.dscommerce.Factory;
import com.leonardo.dscommerce.TokenUtil;
import com.leonardo.dscommerce.entities.Category;
import com.leonardo.dscommerce.entities.Product;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String productName, adminToken, clientToken, invalidToken, adminUsername, clientUsername, password;
    private Product entity;

    @BeforeEach
    void setUp() throws Exception{
        productName = "Macbook";
        adminUsername = "alex@gmail.com";
        clientUsername = "maria@gmail.com";
        password = "123456";
        entity = Factory.createProduct("PlayStation 5", "A videogame Console",
                3500.00,
                "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/18-big.jpg",
                new Category(4L, "Games"));
        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, password);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, password);
        invalidToken = adminToken + "oeihfe";
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
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(3L));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"));
    }

    @Test
    public void insertShouldReturnIsCreatedWhenAdminLoggedAndValidData() throws Exception {
        ProductDTO productDTO = new ProductDTO(entity);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(26L));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(productDTO.getName()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(productDTO.getDescription()));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.price").value(3500.00));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/18-big.jpg"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.categories[0].id").value(4L));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndNameIsInvalid() throws Exception {
        entity.setName("ab");
        ProductDTO productDTO = new ProductDTO(entity);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("name"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Name must be between three and eighty characters."));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndDescriptionIsInvalid() throws Exception {
        entity.setDescription("");
        ProductDTO productDTO = new ProductDTO(entity);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("description"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Description must have ten characters."));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsNegative() throws Exception {
        entity.setPrice(-3500.00);
        ProductDTO productDTO = new ProductDTO(entity);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("price"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("The price must be positive"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndPriceIsZero() throws Exception {
        entity.setPrice(0.0);
        ProductDTO productDTO = new ProductDTO(entity);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("price"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("The price must be positive"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndHasNoCategory() throws Exception {
        entity.getCategories().clear();
        ProductDTO productDTO = new ProductDTO(entity);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());
        result.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].fieldName").value("categories"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("Must have one category"));
    }

    @Test
    public void insertShouldReturnForbiddenWhenUserLogged() throws Exception {
        ProductDTO productDTO = new ProductDTO(entity);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + clientToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
      }

    @Test
    public void insertShouldReturnUnauthorizedWhenUserLogged() throws Exception {
        ProductDTO productDTO = new ProductDTO(entity);
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .header("Authorization", "Bearer " + invalidToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }



}
