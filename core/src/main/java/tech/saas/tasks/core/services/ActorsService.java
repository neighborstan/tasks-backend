package tech.saas.tasks.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.ActorIdentityDto;
import tech.saas.tasks.core.models.TaskDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ActorsService {

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<ActorIdentityDto> actorIdentityRowMapper;
    private final ObjectMapper mapper;

    public List<ActorIdentityDto> actor(String actor) {
        return jdbc.query(
                "select * from identity where actor = :actor",
                Map.ofEntries(Map.entry("actor", actor)),
                actorIdentityRowMapper
        );
    }

    public Optional<ActorIdentityDto> get(String actor, UUID id) {
        return jdbc.query(
                        "select * from identity where actor = :actor and id = :id",
                        Map.ofEntries(
                                Map.entry("actor", actor),
                                Map.entry("id", id)
                        ),
                        actorIdentityRowMapper
                )
                .stream()
                .findFirst();
    }

    public ActorIdentityDto persist(ActorIdentityDto identity) {
        var map = new MapSqlParameterSource();
        map.addValue("actor", identity.getActor());
        map.addValue("id", identity.getId());

        jdbc.update("""
                            insert into identity(actor, id)
                            values (:actor, :id)
                            on conflict (actor, id) do nothing
                            """,
                map
        );

        return get(identity.getActor(), identity.getId()).orElseThrow();
    }
}
