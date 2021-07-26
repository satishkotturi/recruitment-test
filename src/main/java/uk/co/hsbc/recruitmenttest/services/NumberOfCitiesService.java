package uk.co.hsbc.recruitmenttest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.co.hsbc.recruitmenttest.exceptions.RestCallException;
import uk.co.hsbc.recruitmenttest.services.response.WeatherMapResponse;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ServiceUnavailableException;

@Service
public class NumberOfCitiesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberOfCitiesService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${endpoint.weathermap}")
    private String weatherMapEndpoint;

    @Retryable(value = {ServiceUnavailableException.class,
            InternalServerErrorException.class}, maxAttemptsExpression = "#{${spring-retry.maxAttempts}}", backoff = @Backoff(delayExpression = "#{${spring-retry.delay}}"))
    public Long getNumberOfCities(String firstCharacter) {

        LOGGER.info("NumberOfCitiesService.getNumberOfCities method started");
        try {
            WeatherMapResponse response = restTemplate.getForObject(weatherMapEndpoint, WeatherMapResponse.class);
            LOGGER.info("Weather map endpoint provider call successful");
            return getNumberOfCities(response, firstCharacter);
        } catch (HttpClientErrorException | HttpServerErrorException httpErrorException) {
            LOGGER.error("Error when trying to call Weather map service");
            if (httpErrorException.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                LOGGER.error("SERVICE UNAVAILABLE - Failure when trying call Weather map service");
                throw new ServiceUnavailableException("Service unavailable while calling  Weather map service, please try again later.");
            } else if (httpErrorException.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                LOGGER.error("INTERNAL SERVER ERROR - Failure when trying call Weather map service");
                throw new InternalServerErrorException("It was not possible to get number of cities.");
            } else {
                LOGGER.error("Non retryable exception - Failure when trying call Weather map service");
                throw new RestCallException("It was not possible to get number of cities.");
            }
        }
    }

    private Long getNumberOfCities(WeatherMapResponse response, String firstCharacter) {
        if (response != null && response.getList() != null) {
            return response.getList().stream().filter(c -> c.getName().toUpperCase().startsWith(firstCharacter.toUpperCase())).count();
        } else {
            return 0L;
        }
    }
}
