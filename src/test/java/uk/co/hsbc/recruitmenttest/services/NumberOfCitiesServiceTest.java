package uk.co.hsbc.recruitmenttest.services;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.hsbc.recruitmenttest.services.response.Cityname;
import uk.co.hsbc.recruitmenttest.services.response.WeatherMapResponse;

import javax.ws.rs.InternalServerErrorException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NumberOfCitiesServiceTest {

    @InjectMocks
    private NumberOfCitiesService numberOfCitiesService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void getNumberOfCities_Success() {

        ReflectionTestUtils.setField(numberOfCitiesService, "weatherMapEndpoint", "http://www.whatever/fake");

        WeatherMapResponse mockResponse = new WeatherMapResponse();
        List<String> cityNames = Arrays.asList("London","Birmingham");
        mockResponse.setList(cityNames.stream().map(s -> { Cityname c = new Cityname(); c.setName(s); return c; }).collect(Collectors.toList()));

        when(restTemplate.getForObject(anyString(), eq(WeatherMapResponse.class))).thenReturn(mockResponse);

        Long numberOfCities = numberOfCitiesService.getNumberOfCities("b");
        assertEquals(numberOfCities.longValue(), 1L);

    }

    @Test
    public void getNumberOfCities_Response_Null() {

        ReflectionTestUtils.setField(numberOfCitiesService, "weatherMapEndpoint", "http://www.whatever/fake");

        when(restTemplate.getForObject(anyString(), eq(WeatherMapResponse.class))).thenReturn(null);

        Long numberOfCities = numberOfCitiesService.getNumberOfCities("b");
        assertEquals(numberOfCities.longValue(), 0L);

    }

    @Test
    public void getNumberOfCities_Response_Internal_Server_Error() {

        ReflectionTestUtils.setField(numberOfCitiesService, "weatherMapEndpoint", "http://www.whatever/fake");
        String errorMessage = "It was not possible to get number of cities.";
        when(restTemplate.getForObject(anyString(), eq(WeatherMapResponse.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        Throwable actualException = Assertions.assertThrows(InternalServerErrorException.class, () -> numberOfCitiesService.getNumberOfCities("b"));

        verify(restTemplate, times(1))
                .getForObject(anyString(), eq(WeatherMapResponse.class));
        assertEquals(errorMessage, actualException.getMessage());

    }

}
