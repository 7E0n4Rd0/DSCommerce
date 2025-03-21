package com.leonardo.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import com.leonardo.dscommerce.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
public class ProductControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private String productName;
    private Long existingId, nonExistingId;

    private Map<String, Object> postProductInstance;

    @BeforeEach
    void setUp(){

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);

        invalidToken = clientToken + "jdhui9we";

        baseURI = "http://localhost:8080";
        existingId = 2L;
        nonExistingId = 50L;
        productName = "PC Gamer X";

        postProductInstance = new HashMap<>();
        postProductInstance.put("name", "Meu produto");
        postProductInstance.put("description", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia " +
                "deserunt mollit anim id est laborum.");
        postProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/26-big.jpg");
        postProductInstance.put("price", 50.0);

        List<Map<String, Object>> categories = new ArrayList<>();
        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 2);
        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 3);
        categories.add(category1);
        categories.add(category2);

        postProductInstance.put("categories", categories);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists(){
        given()
            .get("/products/{id}", existingId)
        .then()
            .statusCode(200)
            .body("id", is(2))
            .body("name", equalTo("Smart TV"))
            .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
            .body("price", is(2190.0F))
            .body("categories.id", hasItems(2, 3))
            .body("categories.name", hasItems("Eletrônicos", "Computadores"));
    }

    @Test
    public void findAllShouldReturnPageWhenParamIsEmpty(){
        given()
            .get("/products")
        .then()
            .statusCode(200)
            .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));
    }

    @Test
    public void findAllShouldReturnPageWhenParamNotIsEmpty(){
        given()
                .get("/products?name={name}", productName)
        .then()
                .statusCode(200)
                .body("content[0].id", is(7))
                .body("content[0].name", equalTo("PC Gamer X"))
                .body("content[0].price", is(1350.0F))
                .body("content[0].imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/7-big.jpg"))
                .body("pageable.pageNumber", is(0));
    }

    @Test
    public void findAllShouldReturnPageWhenWithPriceMoreThanTwoThousand(){
        given()
            .get("/products?size=25")
        .then()
            .statusCode(200)
            .body("content.findAll {it.price > 2000}.name", hasItems("Smart TV", "PC Gamer Weed"));
    }

    @Test
    public void insertShouldReturnProductCreatedWhenAdminLogged(){
        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + adminToken)
            .body(newProduct)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .post("/products")
        .then()
            .statusCode(201)
            .body("name", equalTo("Meu produto"))
            .body("price", is(50.0F))
            .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/26-big.jpg"))
            .body("categories.id", hasItems(2, 3));
    }

}
