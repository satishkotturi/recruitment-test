package uk.co.hsbc.recruitmenttest.controllers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class RecruitmentTestControllerIT {

    private static MockMvc mockMvc;
    private static WireMockServer wireMock;
    private ClassLoader classLoader = getClass().getClassLoader();
    private static final String GET_NUMBER_OF_CITIES = "/numberOfCities";
    private static final String RECRUITMENT_TEST_ENDPOINT = "/recruitment-test";
    private static final String GET_NUMBER_OF_CITIES_QUERY = "?firstCharacter=a";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeClass
    public static void beforeAll() {
        wireMock = new WireMockServer(WireMockSpring.options().port(9090));
        wireMock.start();
        configureFor("localhost", wireMock.port());
    }

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getNumberOfCities_success_scenario() throws Exception {
        stubWeatherResponse_Success();
        ResultActions resultActions =mockMvc.perform(MockMvcRequestBuilders.get(RECRUITMENT_TEST_ENDPOINT+GET_NUMBER_OF_CITIES+GET_NUMBER_OF_CITIES_QUERY));

        MockHttpServletResponse result = resultActions.andReturn().getResponse();

        assertEquals(200, result.getStatus());
        assertEquals(1L, Long.valueOf(result.getContentAsString()));
    }

    @Test
    public void getNumberOfCities_bad_request_scenario() throws Exception {
        stubWeatherResponse_Success();
        ResultActions resultActions =mockMvc.perform(MockMvcRequestBuilders.get(RECRUITMENT_TEST_ENDPOINT+GET_NUMBER_OF_CITIES+"?firstCharacter=ab"));

        MockHttpServletResponse result = resultActions.andReturn().getResponse();

        assertEquals(400, result.getStatus());
        assertEquals("{\"message\":\"getNumberOfCities.firstCharacter: must match \\\"^[A-Za-z]{1}$\\\"\"}", result.getContentAsString());
    }

    private void stubWeatherResponse_Success() {
        stubFor(WireMock.get(urlEqualTo("/data/2.5/box/city?bbox=12,32,15,37,10&appid=b6907d289e10d714a6e88b30761fae22"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-type", "application/json").withBody(getResponseFromFile("response/weather-response-success.json"))));
    }

    private String getResponseFromFile(String filePath) {
        return readResponseFromFile(filePath, classLoader);
    }

    public static String readResponseFromFile(String filePath, ClassLoader loader) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(loader.getResource(filePath).toURI()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentBuilder.substring(0, contentBuilder.lastIndexOf("\n"));
    }
}
