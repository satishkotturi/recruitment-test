package uk.co.hsbc.recruitmenttest.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceIntegrationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
