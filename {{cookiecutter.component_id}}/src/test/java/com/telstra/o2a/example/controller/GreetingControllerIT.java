package {{cookiecutter.group_id}}.example.controller;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.RestAssured;
import ${groupId}.example.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
        locations = {"classpath:application-test.yml"})
@DirtiesContext
@ActiveProfiles({"test"})
public class GreetingControllerIT {
    static Logger log = LoggerFactory.getLogger(GreetingControllerIT.class);

    @Value("${telstra.microservice.basepath}")
    private String path;

    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost:" + port;
        path = path + "/greetings";
    }

    @Test
    public void verify_doPost_is_success() {
        given().header("Origin", "http://test.telstra.com")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "X-Requested-With")
                .queryParam("name", "World").when().post(path).then().log()
                .body().body("code", equalTo(200)).statusCode(200).assertThat();
    }
    public void verify_doPost_is_success_with_header() {
        given().header("Origin", "http://test.telstra.com")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "X-Requested-With")
                .header("accept-language", "de")
                .queryParam("name", "World").when()
                .post(path).then().log()
                .body().body("code", equalTo(200)).statusCode(200).assertThat();
    }
    @Test
    public void verify_doPost_is_send_error_when_call_without_input_param() {
        given().when().post(path).then().log().body().statusCode(400)
                .assertThat();
    }
    @Test
    public void verify_doPost_is_send_422_error_when_call_invalid_input_param() {
        given().when().post(path + "?name=a").then().log().body()
                .body("code", equalTo(422)).assertThat();
    }
    @Test
    public void verify_doPost_send_404_when_call_with_inalid_url() {
        given().when().post("/test").then().log().body().statusCode(404)
                .assertThat();
    }
    @Test
    public void verify_doPost_send_500_when_call_with_not_supported_locale() {
        given().header("accept-language", "fra").queryParam("name", "World")
                .when().post(path)

                .then().log().body().body("code", equalTo(500)).assertThat();
    }
    @Test
    public void verify_doPost_send_400_when_call_with_not_supported_locale() {
        given().header("accept-language", "aut").queryParam("name", "World")
                .when().post(path)

                .then().log().body().body("code", equalTo(404)).assertThat();
    }

}