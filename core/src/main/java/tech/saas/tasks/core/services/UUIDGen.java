package tech.saas.tasks.core.services;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UUIDGen {

    public UUID gen(Object... paths) {
        return UUID.nameUUIDFromBytes(
                Stream.of(paths)
                        .map(String::valueOf)
                        .sorted(Comparator.comparing(x -> x))
                        .collect(Collectors.joining(","))
                        .getBytes(StandardCharsets.UTF_8)
        );
    }
}
