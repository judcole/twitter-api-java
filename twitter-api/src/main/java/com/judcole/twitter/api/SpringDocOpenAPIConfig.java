package com.judcole.twitter.api;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The class to configure SpringDoc OpenAPI (Swagger).
 */
@Configuration
public class SpringDocOpenAPIConfig {
    /**
     * Get the public API definitions.
     *
     * @return the API definitions
     */
    @Bean
    public GroupedOpenApi publicApi() {
        // Primary group with all matching API calls
        return GroupedOpenApi.builder()
                .group("primary")
                .pathsToMatch("/**")
                .build();
    }
}
