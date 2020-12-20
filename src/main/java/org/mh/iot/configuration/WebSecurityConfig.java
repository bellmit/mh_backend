package org.mh.iot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Created by evolshan on 16.12.2020.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {



    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable();

        //use default cors + add delete operation
        httpSecurity.cors().configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
            configuration.addAllowedMethod(HttpMethod.DELETE);
            configuration.addAllowedMethod(HttpMethod.PUT);
            return configuration;
        });

    }
}