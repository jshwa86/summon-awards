package com.app.controllers.payerpointbalancesummary;

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
class PayerPointBalanceSummaryControllerTest {

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
                .when().get("/*,\uD83D\uDC36")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"userIdentifier must be alphanumeric\"]}"));
    }

    @Test
    public void verifyBehaviorWhenJustAddingPoints(){

        recordTransaction("DANNON",1000,"2020-11-02T14:00:00.000+00:00");
        recordTransaction("UNILEVER",200,"2020-11-02T14:02:00.000+00:00");
        recordTransaction("DANNON",200,"2020-11-02T15:00:00.000+00:00");
        recordTransaction("BLUE MOON",1000,"2020-11-02T15:02:00.000+00:00");
        recordTransaction("DANNON",400,"2020-11-02T15:04:00.000+00:00");

        given()
                .header("Content-type","application/json")
                .when().get("/buddy")
                .then()
                .statusCode(200)
                .body(is("{\"UNILEVER\":200,\"BLUE MOON\":1000,\"DANNON\":1600}"));
    }

    @Test
    public void verifyBehaviorWhenAddingNegativePoints(){
        given()
                .header("Content-type","application/json")
                .body("{\"payer\":\"DANNON\",\"points\":-1000,\"timestamp\":\"2020-11-02T14:00:00.000+00:00\"}")
                .when().post("/buddy/recordTransaction")
                .then()
                .statusCode(400)
                .body(is("{\"errors\":[\"Cannot add transaction because it would cause the payer's point balance to become negative.\"]}"));

        given()
                .header("Content-type","application/json")
                .when().get("/buddy")
                .then()
                .statusCode(200)
                .body(is("{}"));
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