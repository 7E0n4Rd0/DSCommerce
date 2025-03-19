package com.leonardo.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ProductControllerRA {

    private String productName;
    private Long existingId, nonExistingId;

    @BeforeEach
    void setUp(){
        baseURI = "http://localhost:8080";
        existingId = 2L;
        nonExistingId = 50L;
        productName = "PC Gamer X";
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

}
