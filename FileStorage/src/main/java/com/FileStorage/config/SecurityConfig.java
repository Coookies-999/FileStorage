package com.FileStorage.config;

import com.FileStorage.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

            http
                    .cors(cors->cors.configurationSource(request -> {
                        CorsConfiguration configuration=new CorsConfiguration();
                        configuration.setAllowedOrigins(
                                List.of("http://localhost:5000")
                        );

                        configuration.setAllowedHeaders(
                                List.of("*")
                        );

                        configuration.setAllowedMethods(
                                List.of("GET","POST","OPTIONS","DELETE","PUT")
                        );

                        configuration.setExposedHeaders(
                                List.of("Jwt-Token")
                        );

                        return configuration;
                    }))
                    .formLogin(form->form.disable())   //diabled formlogin
                    .httpBasic(basic->basic.disable())
                    .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .csrf(customizer -> customizer.disable())   //disable csrf filtration
                    .authorizeHttpRequests(auth->auth
                            // Allow CORS preflight requests
                            .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                            //Login and signup dont require jwt
                            .requestMatchers("/auth/signup","/auth/login")
                            .permitAll()
                            .anyRequest().authenticated() //only auth/login and auth/signup is granted without any security filter
                    )
                    .addFilterBefore(
                            //All request will go for jwtAuthentication using username and password
                            jwtAuthenticationFilter,
                            UsernamePasswordAuthenticationFilter.class
                    );

            return http.build();
        }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

}
