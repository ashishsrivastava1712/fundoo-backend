
package com.fundoo.notes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * Class Name  : PasswordConfig
 * Description : Configures BCrypt password encoding as a
 *               Spring-managed PasswordEncoder bean.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */
@Configuration
public class PasswordConfig {

    // Creates the encoder used by the user service.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
