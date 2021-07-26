package uk.co.hsbc.recruitmenttest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.hsbc.recruitmenttest.services.NumberOfCitiesService;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/recruitment-test")
@Validated
public class RecruitmentTestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecruitmentTestController.class);

    private static final String GET_NUMBER_OF_CITIES = "/numberOfCities";
    private static final String FIRST_CHARACTER_REGEX_PATTERN = "^[A-Za-z]{1}$";
    private static final String FIRST_CHARACTER_QUERY_PARAM = "firstCharacter";

    @Autowired
    private NumberOfCitiesService numberOfCitiesService;

    @GetMapping(GET_NUMBER_OF_CITIES)
    public ResponseEntity<Long> getNumberOfCities(@Valid @Pattern(regexp = FIRST_CHARACTER_REGEX_PATTERN)
                                                        @RequestParam(FIRST_CHARACTER_QUERY_PARAM) String firstCharacter) {
        LOGGER.info("Starting GET /numberOfCities request...");
        Long numberOfCities = numberOfCitiesService.getNumberOfCities(firstCharacter);
        LOGGER.info("Completed GET /numberOfCities request...");
        return ResponseEntity.ok(numberOfCities);
    }
}
