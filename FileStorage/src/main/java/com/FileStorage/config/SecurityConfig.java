package com.FileStorage.config;

import com.FileStorage.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

            http
                    .formLogin(form->form.disable())   //diabled formlogin
                    .httpBasic(basic->basic.disable())
                    .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .csrf(customizer -> customizer.disable())   //disable csrf filtration
                    .authorizeHttpRequests(auth->auth.requestMatchers("/auth/signup","/auth/login")
                            .permitAll()
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(
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
