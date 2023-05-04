import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class GetMoc {
    private static final String HOST = "localhost";
    private static final int PORT = 8888;
    private static final String URL = String.format("http://%s:%d/%s", HOST, PORT, "%s");
    private static final WireMockServer WIRE_MOCK_SERVER = new WireMockServer(PORT);
    @BeforeTest
    public void setUpWireMockServer() {
        System.out.println("Start server");
        WIRE_MOCK_SERVER.start();
        WireMock.configureFor(HOST, PORT);
    }

    @AfterMethod(alwaysRun = true)
    public void stopWireMockServer() {
        if (WIRE_MOCK_SERVER.isRunning()) {
            System.out.println("Shot Down");
            WIRE_MOCK_SERVER.stop();
        }
    }
@Test
    public void sendGetRequestGetAllUsers() {

        String responseGetMethodExpected = "{\n  \"users\": [\n    {\n      \"userName\": \"Andry\",\n      \"userId\": 23\n    },\n    {\n      \"userName\": \"Eduard\",\n      \"userId\": 12\n    }\n  ]\n}";

        stubFor(get(urlEqualTo("/api/users"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseGetMethodExpected)));

        String apiURL = String.format(URL, "api/users");

        //Get response and check: status code, header and response for all users
        String responseGetMethodActual = RestAssured.given().log().all()
                .get(apiURL)
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .assertThat()
                .header("Content-Type", ("application/json"))
                .extract()
                .body()
                .asString();

        // Check the responses after get method and compare the responses
        Assert.assertEquals(responseGetMethodActual, responseGetMethodExpected);

    }


    @Test
    public void testGetRequest()
    {
        RequestSpecification specification = RestAssured.given();
        String apisUrl = "http://localhost:8080/api/users";
        ValidatableResponse response = specification.get(apisUrl).then();
        JsonPath jsonPath = response.extract().body().as(JsonPath.class);
        System.out.println(response.extract().statusCode());
        System.out.println(response.extract().body().asString());
        System.out.println(response.extract().contentType());

        //System.out.println("Get response:" + jsonPath.getPath("users[0], userName");
    }

    @Test
    public void testPostBody()
    {
        Map<String, String> addHeaders = new HashMap <String,String>()
        {{
            put("Content-Type", ContentType.JSON.toString());
        }};
        String apisUrl = "http://localhost:8080/api/users";
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.headers(addHeaders);
        requestSpecification.baseUri(apisUrl);
        requestSpecification.body("{\"userName\": \"Jac\", \"userId\": 15}");
        ValidatableResponse response = requestSpecification.post(apisUrl).then();
        System.out.println(response.extract().statusCode());
        System.out.println(response.extract().body().asString());
        System.out.println(response.extract().contentType());

    }

    @Test
    public void testGetWithParams()
    {
        String apisUrl = "http://localhost:8080/api/users";
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.queryParam("userId",23);
        ValidatableResponse validatableResponse = requestSpecification.get(apisUrl).then();
        String body = validatableResponse.extract().body().asString();
        User user = Helper.initFromJsonUser(body);
        System.out.println(user);
    }
}