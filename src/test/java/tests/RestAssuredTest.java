package tests;

import models.CreateUserBody;
import models.CreateUserResponse;
import models.MissingPasswordOrUserName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static specs.Specifications.*;

public class RestAssuredTest extends TestBases {

    @Test
    @DisplayName("Получение списка пользователей")
    void checkBodyListUser() {

        get("/users?page=2")
                .then()
                .log().status()
                .body("page", is(2))
                .body("per_page", is(6))
                .body("total", is(12))
                .body("total_pages", is(2))
                .body("data.id", hasItems(7, 8, 9))
                .body("data.first_name", hasItems("Michael", "Lindsay", "Tobias"))
                .statusCode(200);

    }

    @Test
    @DisplayName("Создание пользователя")
    void successfulCreateUser() {

        CreateUserBody credentials = new CreateUserBody();
        credentials.setEmail("eve.holt@reqres.in");
        credentials.setPassword("pistol");

        CreateUserResponse response = step("Create user", () ->
                given(createUserRequestSpec)
                        .body(credentials)


                        .when()
                        .post()

                        .then()
                        .spec(createUserResponseSpec)
                        .extract().as(CreateUserResponse.class));
        step("Check token", () ->
                assertNotNull(response.getToken()));
        step("Check ID", () ->
                assertEquals(4, response.getId()));
    }

    @Test
    @DisplayName("Создание пользователя без емэйла и пароля")
    void missingEmail() {
        CreateUserBody credentials = new CreateUserBody();

        MissingPasswordOrUserName response = step("Create user", () ->
                given(createUserRequestSpec)
                        .body(credentials)

                        .when()
                        .post()

                        .then()
                        .spec(missingPasswordResponseSpec)
                        .extract().as(MissingPasswordOrUserName.class));
        step("Check response", () ->
                assertEquals("Missing email or username", response.getError()));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    void missingPassword() {
        CreateUserBody credentials = new CreateUserBody();
        credentials.setEmail("eve.holt@reqres.in");
        MissingPasswordOrUserName response = step("Make request", () ->
                given(createUserRequestSpec)
                        .body(credentials)

                        .when()
                        .post()

                        .then()
                        .spec(missingPasswordResponseSpec)
                        .extract().as(MissingPasswordOrUserName.class));
        step("Check response", () ->
                assertEquals("Missing password", response.getError()));
    }


    @Test
    @DisplayName("Создание пользователя без пароля")
    void UnsuccessfulRegistration() {
        CreateUserBody credentials = new CreateUserBody();
        credentials.setPassword("sadds");
        MissingPasswordOrUserName response = step("Make request", () ->
                given(createUserRequestSpec)
                        .body(credentials)

                        .when()
                        .post()

                        .then()
                        .spec(missingEmailResponseSpec)
                        .extract().as(MissingPasswordOrUserName.class));
        step("Check response", () ->
                assertEquals("Missing email or username", response.getError()));
    }

}

