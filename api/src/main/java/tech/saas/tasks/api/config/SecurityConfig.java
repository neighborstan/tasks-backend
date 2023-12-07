package tech.saas.tasks.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URI;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ObjectMapper mapper,
                                           @Value("${server.error.path:${error.path:/error}}") String error) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        "/actuator/**",
                                        "/spec/**",
                                        "/error"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .exceptionHandling(v -> {
                            v.accessDeniedHandler((request, response, ex) -> {
                                var status = HttpStatus.FORBIDDEN;
                                response.setStatus(status.value());
                                var body = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
                                body.setType(URI.create("traffic:exception"));
                                body.setTitle(String.valueOf(status));

                                response.sendError(status.value(), mapper.writeValueAsString(body));
                            });
                            v.authenticationEntryPoint((request, response, ex) -> {
                                var status = HttpStatus.FORBIDDEN;
                                response.setStatus(status.value());
                                var body = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
                                body.setType(URI.create("traffic:exception"));
                                body.setTitle(String.valueOf(status));

                                response.sendError(status.value(), mapper.writeValueAsString(body));
                            });
                        }
                )
                .build();
    }


}
