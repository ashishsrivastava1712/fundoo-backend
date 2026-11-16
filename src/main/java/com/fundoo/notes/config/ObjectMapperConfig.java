
package com.fundoo.notes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Class Name  : ObjectMapperConfig
 * Description : Configures ObjectMapper bean for JSON processing.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
