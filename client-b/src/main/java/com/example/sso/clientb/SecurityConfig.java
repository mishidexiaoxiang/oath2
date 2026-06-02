package com.example.sso.clientb;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .addFilterBefore(oauth2CallbackLoggingFilter(), OAuth2LoginAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("CLIENT_B_SESSION"));

        return http.build();
    }

    private static OncePerRequestFilter oauth2CallbackLoggingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
                if (request.getServletPath().startsWith("/login/oauth2/code/")) {
                    log.info("OAuth2 login callback received: registrationId={}, code={}, state={}, error={}",
                            request.getServletPath().substring("/login/oauth2/code/".length()),
                            request.getParameter("code"),
                            request.getParameter("state"),
                            request.getParameter("error"));
                }
                filterChain.doFilter(request, response);
            }
        };
    }
}
