package com.app;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class ControllerTest {

    @Test
    public void whenPayerEmptyTest(){
        given()
                .header("Content-type","application/json")
                .body("{ \"payer\": \"\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\" }")
                .when().post("/")
                .then()
                .statusCode(400)
                .body(is("{\"success\":false,\"errors\":[\"payer may not be blank\"]}"));
    }

    @Test
    public void happyPathTest(){
        given()
                .header("Content-type","application/json")
                .body("{ \"payer\": \"DANNON\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\" }")
                .when().post("/")
                .then()
                .statusCode(201)
                .body(is("{\"success\":true}"));
    }

}