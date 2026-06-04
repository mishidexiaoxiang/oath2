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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/error", "/.well-known/appspecific/**").permitAll()
                        .anyRequest().authenticated())
                .requestCache(cache -> cache.requestCache(oauth2RequestCache()))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(
                                        oauth2AuthorizationRequestResolver(clientRegistrationRepository))))
                .addFilterBefore(oauth2CallbackLoggingFilter(), OAuth2LoginAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("CLIENT_B_SESSION"));

        return http.build();
    }

    private static HttpSessionRequestCache oauth2RequestCache() {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setRequestMatcher(notBrowserProbeOrErrorRequest());
        return requestCache;
    }

    private static RequestMatcher notBrowserProbeOrErrorRequest() {
        return request -> {
            String path = request.getServletPath();
            return !"/error".equals(path) && !path.startsWith("/.well-known/appspecific/");
        };
    }

    private static OAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        OAuth2AuthorizationRequestResolver delegate =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request);
                logAuthorizationRequest(request, authorizationRequest);
                return authorizationRequest;
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest authorizationRequest = delegate.resolve(request, clientRegistrationId);
                logAuthorizationRequest(request, authorizationRequest);
                return authorizationRequest;
            }
        };
    }

    private static void logAuthorizationRequest(
            HttpServletRequest request,
            OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest != null) {
            log.info("OAuth2 login started: requestUrl={}, authorizationUrl={}",
                    fullRequestUrl(request),
                    authorizationRequest.getAuthorizationRequestUri());
        }
    }

    private static OncePerRequestFilter oauth2CallbackLoggingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
                if (request.getServletPath().startsWith("/login/oauth2/code/")) {
                    log.info("OAuth2 login callback received: callbackUrl={}, registrationId={}, code={}, state={}, error={}",
                            fullRequestUrl(request),
                            request.getServletPath().substring("/login/oauth2/code/".length()),
                            request.getParameter("code"),
                            request.getParameter("state"),
                            request.getParameter("error"));
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private static String fullRequestUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder(request.getRequestURL());
        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append('?').append(queryString);
        }
        return url.toString();
    }
}
