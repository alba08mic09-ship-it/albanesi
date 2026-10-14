package it.zucchetti.lab;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class AppResourceTest {

    @Test
    public void testRootEndpoint() {
        given()
            .when().get("/")
            .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Messaggio:"))
                .body(containsString("Ciao dalla mia app!"))
                .body(containsString("1.0"));
    }

    @Test
    public void testInfoEndpoint() {
        given()
            .when().get("/info")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("hostname", notNullValue())
                .body("version", equalTo("1.0"))
                .body("startedAt", notNullValue());
    }

    @Test
    public void testHealthEndpoint() {
        given()
            .when().get("/health")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo("ok"));
    }

    @Test
    public void testCrashEndpoint() {
        given()
            .when().post("/crash")
            .then()
                .statusCode(202)
                .contentType(ContentType.JSON)
                .body("status", equalTo("crashing"));
    }
}

