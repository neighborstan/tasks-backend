package tech.saas.tasks.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.TaskAssignmentDto;
import tech.saas.tasks.core.models.TaskDto;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AssignmentService {

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<TaskAssignmentDto> rowMapper;
    private final ObjectMapper mapper;

    public Optional<TaskAssignmentDto> assignment(UUID task, String actor) {
        return jdbc.query(
                        """
                                select * from assignment where task = :task and actor = :actor
                                """,
                        Map.ofEntries(
                                Map.entry("task", task),
                                Map.entry("actor", actor)
                        ),
                        rowMapper
                )
                .stream()
                .findFirst();
    }

    public TaskAssignmentDto persist(TaskAssignmentDto assignment) {

        jdbc.update(
                """
                        insert into assignment(task, actor, instant)
                        values (:task, :actor, :instant)
                        on conflict (task, actor) do nothing
                        """,
                Map.ofEntries(
                        Map.entry("task", assignment.getTask()),
                        Map.entry("actor", assignment.getActor()),
                        Map.entry("instant", Timestamp.from(assignment.getInstant().toInstant()))
                )
        );
        return assignment(assignment.getTask(), assignment.getActor()).orElseThrow();
    }
}
