package com.leonardo.dscommerce;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;


@Component
public class TokenUtil {

    private static Response authRequest(String username, String password){
        return
        given()
            .auth()
            .preemptive()
            .basic("myclientid", "myclientsecret")
        .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
        .when()
            .post("/oauth2/token");
    }

    public static String obtainAccessToken(String username, String password){
        Response response = authRequest(username, password);
        JsonPath jsonBody = response.jsonPath();
        return jsonBody.getString("access_token");
    }

}
