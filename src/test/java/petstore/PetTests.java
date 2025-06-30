package petstore;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetTests {

    static int petId = 123456789;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Test
    @Order(1)
    public void createPet() {
        String requestBody = """
                {
                    "id": %d,
                    "name": "Barsik",
                    "status": "available"
                }
                """.formatted(petId);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("id", equalTo(petId));
    }

    @Test
    @Order(2)
    public void getPetById() {
        given()
                .when()
                .get("/pet/" + petId)
                .then()
                .statusCode(200)
                .body("id", equalTo(petId));
    }

    @Test
    @Order(3)
    public void updatePet() {
        String updatedBody = """
                {
                    "id": %d,
                    "name": "BarsikUpdated",
                    "status": "sold"
                }
                """.formatted(petId);

        given()
                .contentType(ContentType.JSON)
                .body(updatedBody)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("BarsikUpdated"));
    }

    @Test
    @Order(4)
    public void deletePet() {
        given()
                .when()
                .delete("/pet/" + petId)
                .then()
                .statusCode(anyOf(is(200), is(202), is(204)));
    }

    @Test
    @Order(5)
    public void getPetByIdAfterDeletion() {
        given()
                .when()
                .get("/pet/" + petId)
                .then()
                .statusCode(404); // должен быть 404, если питомец удалён
    }
}