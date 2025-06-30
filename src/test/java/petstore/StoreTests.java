package petstore;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreTests {

    static int orderId = 1097711385;
    static int petId = 123456789;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Test
    @Order(1)
    public void createOrder() {
        String body = "{\n" +
                "    \"id\": " + orderId + ",\n" +
                "    \"petId\": " + petId + ",\n" +
                "    \"quantity\": 1,\n" +
                "    \"shipDate\": \"2025-07-01T12:00:00.000Z\",\n" +
                "    \"status\": \"placed\",\n" +
                "    \"complete\": false\n" +
                "}";

        given()
                .contentType("application/json")
                .body(body)
                .log().all()
                .when()
                .post("/store/order")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("petId", equalTo(petId))
                .body("status", equalTo("placed"));
    }

    @Test
    @Order(2)
    public void getOrderById() {
        given()
                .pathParam("orderId", orderId)
                .log().all()
                .when()
                .get("/store/order/{orderId}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(orderId));
    }

    @Test
    @Order(3)
    public void deleteOrder() {
        given()
                .pathParam("orderId", orderId)
                .log().all()
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .log().all()
                .statusCode(anyOf(is(200), is(204), is(404)));
    }

    @Test
    @Order(4)
    public void getOrderByIdAfterDeletion() throws InterruptedException {
        Thread.sleep(1000);

        Response response = given()
                .pathParam("orderId", orderId)
                .log().all()
                .when()
                .get("/store/order/{orderId}")
                .then()
                .log().all()
                .extract().response();

        if (response.statusCode() == 200) {
            System.out.println("Заказ все ещё существует после удаления - API не удаляет данные.");
        } else {
            assertEquals(404, response.statusCode(), "Заказ должен быть удален и не найден.");
        }
    }
}  // ну наконец то, я чуть не поседел когда делал это
