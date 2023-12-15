package tech.saas.tasks.core.services;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.TaskAssignmentDto;

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

    public List<TaskAssignmentDto> pipeline(String pipeline) {
        return jdbc.query("select distinct assignment.* from assignment where pipeline = :pipeline",
                Map.ofEntries(Map.entry("pipeline", pipeline)),
                rowMapper
        );
    }

    public Optional<TaskAssignmentDto> assignment(UUID task, String actor) {
        return jdbc.query(
                        "select * from assignment where task = :task and actor = :actor",
                        Map.ofEntries(
                                Map.entry("task", task),
                                Map.entry("actor", actor)
                        ),
                        rowMapper
                )
                .stream()
                .findFirst();
    }

    public Optional<TaskAssignmentDto> assignment(UUID task, String pipeline, String actor) {
        return jdbc.query(
                        "select * from assignment where task = :task and actor = :actor",
                        Map.ofEntries(
                                Map.entry("task", task),
                                Map.entry("actor", actor)
                        ),
                        rowMapper
                )
                .stream()
                .findFirst();
    }

    public List<TaskAssignmentDto> assignment(UUID task) {
        return jdbc.query(
                "select * from assignment where task = :task",
                Map.ofEntries(Map.entry("task", task)),
                rowMapper
        );
    }

    public TaskAssignmentDto persist(TaskAssignmentDto assignment) {

        jdbc.update(
                """
                        insert into assignment(task, pipeline, actor, instant)
                        values (:task, :pipeline, :actor, :instant)
                        on conflict (task, actor) do nothing
                        """,
                Map.ofEntries(
                        Map.entry("task", assignment.getTask()),
                        Map.entry("pipeline", assignment.getPipeline()),
                        Map.entry("actor", assignment.getActor()),
                        Map.entry("instant", Timestamp.from(assignment.getInstant().toInstant()))
                )
        );
        return assignment(assignment.getTask(), assignment.getActor()).orElseThrow();
    }

    public void delete(TaskAssignmentDto assignment) {
        jdbc.update(
                """
                        delete from assignment
                        where task = :task and actor = :actor
                        """,
                Map.ofEntries(
                        Map.entry("task", assignment.getTask()),
                        Map.entry("actor", assignment.getActor())
                )
        );
    }

    public void delete(UUID task, String pipeline) {
        jdbc.update(
                """
                        delete from assignment
                        where task = :task and pipeline = :pipeline
                        """,
                Map.ofEntries(
                        Map.entry("task", task),
                        Map.entry("pipeline", pipeline)
                )
        );
    }
}
