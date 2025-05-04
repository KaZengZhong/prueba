package com.prestabanco.webconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()  // Habilita CORS en Spring Security
                .csrf().disable()  // Deshabilita CSRF (necesario para APIs REST)
                .authorizeHttpRequests()
                .requestMatchers("/api/**").permitAll()  // Permite todas las rutas de API sin autenticación
                .anyRequest().authenticated();  // Cualquier otra ruta requiere autenticación

        return http.build();
    }
}
