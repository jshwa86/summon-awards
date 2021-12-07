package com.app;

import com.app.model.InputValidationFailedResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class RecordTransactionControllerTest {

    @Inject
    ObjectMapper objectMapper;
    @Inject
    InMemoryDataStore inMemoryDataStore;

    @BeforeEach
    public void setup(){
        /*
            This reinitializes the inMemoryData store for each @Test
            which is helpful because it means each @Test starts from the same place
            (an empty data store). Without this the execution of one @Test might interfere with the
            results of a subsequent @Test
         */
        QuarkusMock.installMockForInstance(new InMemoryDataStore(), inMemoryDataStore);
    }

    @Test
    public void happyPathTest() throws JsonProcessingException {
        String transactionAsJson = "{\"payer\":\"DANNON\",\"points\":1000,\"timestamp\":\"2020-11-02T14:00:00.000+00:00\"}";
        given()
                .header("Content-type","application/json")
                .body(transactionAsJson)
                .when().post("/buddy/recordTransaction")
                .then()
                .statusCode(201)
                .body(emptyString());
        assertEquals(1,inMemoryDataStore.retrieveAssociatedTransactions("buddy").size());
        assertEquals(transactionAsJson,objectMapper.writeValueAsString(inMemoryDataStore.retrieveAssociatedTransactions("buddy").get(0)));
    }

    @Test
    public void whenPayerEmptyTest(){
        given()
                .header("Content-type","application/json")
                .body("{ \"payer\": \"\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\" }")
                .when().post("/buddy/recordTransaction")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"payer may not be blank\"]}"));
        assertNull(inMemoryDataStore.retrieveAssociatedTransactions("buddy"));
    }

    @Test
    public void whenPointsNullTest(){
        given()
                .header("Content-type","application/json")
                .body("{\"payer\":\"DANNON\",\"timestamp\":\"2020-11-02T14:00:00.000+00:00\"}")
                .when().post("/buddy/recordTransaction")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"points may not be null\"]}"));
        assertNull(inMemoryDataStore.retrieveAssociatedTransactions("buddy"));
    }

    @Test
    public void whenTimestampNullTest(){
        given()
                .header("Content-type","application/json")
                .body("{\"payer\":\"DANNON\",\"points\":1000}")
                .when().post("/buddy/recordTransaction")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"timestamp may not be null\"]}"));
        assertNull(inMemoryDataStore.retrieveAssociatedTransactions("buddy"));
    }

    @Test
    public void whenUserIdentifierContainsDisallowedCharactersTest() {
        String transactionAsJson = "{\"payer\":\"DANNON\",\"points\":1000,\"timestamp\":\"2020-11-02T14:00:00.000+00:00\"}";
        given()
                .header("Content-type","application/json")
                .body(transactionAsJson)
                .when().post("/*,\uD83D\uDC36/recordTransaction")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"userIdentifier must be alphanumeric\"]}"));
        assertNull(inMemoryDataStore.retrieveAssociatedTransactions("buddy"));
    }

    @Test
    public void whenAbsolutelyEverythingIsWrongWithTheRequestTest() throws JsonProcessingException {
        String transactionAsJson = "{}";
        Response response =
        given()
                .header("Content-type","application/json")
                .body(transactionAsJson)
                .when().post("/*,\uD83D\uDC36/recordTransaction")
                .then()
                .statusCode(400)
                .extract().response();
        InputValidationFailedResponse recordTransactionResponse = objectMapper.readValue(response.getBody().asString(),InputValidationFailedResponse.class);
        assertEquals(4,recordTransactionResponse.getErrors().size());
        assertTrue(recordTransactionResponse.getErrors().contains("timestamp may not be null"));
        assertTrue(recordTransactionResponse.getErrors().contains("payer may not be blank"));
        assertTrue(recordTransactionResponse.getErrors().contains("points may not be null"));
        assertTrue(recordTransactionResponse.getErrors().contains("userIdentifier must be alphanumeric"));
        assertNull(inMemoryDataStore.retrieveAssociatedTransactions("buddy"));
    }
}