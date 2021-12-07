package com.app.spendpoints;

import com.app.InMemoryDataStore;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class SpendPointsControllerTest {

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
    public void whenUserIdentifierContainsDisallowedCharactersTest() {
        given()
                .header("Content-type","application/json")
                .body("{ \"points\": 5000 }")
                .when().post("/*,\uD83D\uDC36/spend")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"userIdentifier must be alphanumeric\"]}"));
    }

    @Test
    public void whenPointsAreNullTest() {
        given()
                .header("Content-type","application/json")
                .body("{}")
                .when().post("/buddy/spend")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"points must be defined for this operation\"]}"));
    }

    @Test
    public void whenPointsAreNegativeTest() {
        given()
                .header("Content-type","application/json")
                .body("{\"points\":-100}")
                .when().post("/buddy/spend")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"points must be a positive number\"]}"));
    }

    @Test
    public void verifyResponseWhenSpendingPoints(){

        recordTransaction("DANNON",1000,"2020-11-02T14:00:00Z");
        recordTransaction("UNILEVER",200,"2020-10-31T11:00:00Z");
        recordTransaction("DANNON",-200,"2020-10-31T15:00:00Z");
        recordTransaction("MILLER COORS",10000,"2020-11-01T14:00:00Z");
        recordTransaction("DANNON",300,"2020-10-31T10:00:00Z");

        //Spend 5000 points and verify which payers that was attributed to:

        given()
                .header("Content-type","application/json")
                .body("{\"points\":5000}")
                .when().post("/buddy/spend")
                .then()
                .statusCode(200)
                .body(is("[{\"payer\":\"UNILEVER\",\"points\":-200},{\"payer\":\"MILLER COORS\",\"points\":-4700},{\"payer\":\"DANNON\",\"points\":-100}]"));

        //Verify retrieving the summary is updated appropriately too:
        given()
                .header("Content-type","application/json")
                .when().get("/buddy")
                .then()
                .statusCode(200)
                .body(is("{\"UNILEVER\":0,\"MILLER COORS\":5300,\"DANNON\":1000}"));

        //Verify that if we try to spend again with more points than we have, then it fails
        given()
                .header("Content-type","application/json")
                .body("{\"points\":50000}")
                .when().post("/buddy/spend")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"Request to spend 50000 points cannot be fulfilled as there are only 50000 points available to spend\"]}"));
    }

    private void recordTransaction(String payer, Integer points, String timestamp){
        given()
                .header("Content-type","application/json")
                .body("{\"payer\":\""+payer+"\",\"points\":"+points+",\"timestamp\":\""+timestamp+"\"}")
                .when().post("/buddy/recordTransaction")
                .then()
                .statusCode(201)
                .body(emptyString());
    }
}