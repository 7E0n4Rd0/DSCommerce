package com.leonardo.dscommerce.controllers;

import com.leonardo.dscommerce.TokenUtil;
import com.leonardo.dscommerce.entities.OrderStatus;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {

    private String clientUsername, clientPassword, clientToken, adminUsername, adminPassword, adminToken, invalidToken;
    private Long existingId, nonExistingId;


    @BeforeEach
    void setUp() throws Exception{
        existingId = 2L;
        nonExistingId = 50L;

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = clientToken+"oidshjf";

    }


    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged(){
        given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + adminToken)
            .accept(ContentType.JSON)
        .when()
            .get("/orders/{id}", existingId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(OrderStatus.DELIVERED.toString()))
            .body("client.name", equalTo("Alex Green"))
            .body("items.name", hasItem("Macbook Pro"));
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged(){
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", 1L)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo(OrderStatus.PAID.toString()))
                .body("client.name", equalTo("Maria Brown"))
                .body("items.name", hasItems("Macbook Pro", "The Lord of the Rings"));
    }

    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndOtherLogged(){
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", 2L)
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndAdminLogged(){
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", nonExistingId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndClientLogged(){
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", nonExistingId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenIdExistsAndNoOneLogged(){
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", existingId)
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
