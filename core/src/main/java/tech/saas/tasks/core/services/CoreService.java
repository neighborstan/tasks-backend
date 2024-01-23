package tech.saas.tasks.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tech.saas.tasks.core.exceptions.BadRequestException;
import tech.saas.tasks.core.models.Driver;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class CoreService {

    private final String uri;
    private final String token;
    private final ObjectMapper mapper;
    private final RestTemplate rest;

    public CoreService(
            @Value("${services.core.url}") String uri,
            @Value("${services.core.token}") String token,
            ObjectMapper mapper,
            RestTemplate rest) {
        this.mapper = mapper;
        this.uri = uri;
        this.token = token;
        this.rest = rest;
    }

    public Optional<Driver> driver(UUID company, UUID id) {

        try {
            var headers = new HttpHeaders();
            headers.set("X-Internal-Authorization", token);
            var entity = new HttpEntity<>(headers);
            var path = UriComponentsBuilder.fromUriString(uri)
                    .path(String.format("/api/v1/resources/%s/drivers/%s", company, id))
                    .build()
                    .toString();
            var response =
                    rest.exchange(path, HttpMethod.GET, entity, String.class);
            var code = response.getStatusCode();
            var status = HttpStatus.valueOf(code.value());

            switch (status) {
                case OK:
                    return Optional.of(mapper.readValue(response.getBody(), Driver.class));
                default:
                    return Optional.empty();
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


}
