package uk.co.hsbc.recruitmenttest.services.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class WeatherMapResponse {

    private List<Cityname> list;
}
