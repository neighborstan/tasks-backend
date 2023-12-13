package tech.saas.tasks.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.TaskDto;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TasksService {

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<TaskDto> taskRowMapper;
    private final ObjectMapper mapper;

    public List<TaskDto> actor(String actor) {
        return jdbc.query(
                """
                        select * from tasks join assignment on tasks.id = assignment.task
                        where actor = :actor
                        """,
                Map.ofEntries(Map.entry("actor", actor)),
                taskRowMapper
        );
    }

    public List<TaskDto> pipeline(String pipeline) {
        return jdbc.query(
                """
                        select * from tasks
                        where pipeline = :pipeline
                        """,
                Map.ofEntries(Map.entry("pipeline", pipeline)),
                taskRowMapper
        );
    }

    public Optional<TaskDto> get(UUID id) {
        return jdbc.query(
                        """
                                select * from tasks where id = ?
                                """,
                        Map.ofEntries(Map.entry("id", id)),
                        taskRowMapper
                )
                .stream()
                .findFirst();
    }

    public TaskDto persist(TaskDto task) {
        try {
            var map = new MapSqlParameterSource();
            map.addValue("id", task.getId());
            map.addValue("pipeline", task.getPipeline());
            map.addValue("author", task.getAuthor());
            map.addValue("status", task.getStatus());
            map.addValue("story", mapper.writeValueAsString(task.getStory()));
            map.addValue("transition", Timestamp.from(task.getTransition().toInstant()));
            map.addValue("entity", mapper.writeValueAsString(task.getEntity()));
            map.addValue("payload", mapper.writeValueAsString(task.getPayload()));
            map.addValue("comment", task.getComment());

            jdbc.update("""
                            insert into tasks(id, pipeline, author, status, story, transition, entity, payload, comment)
                            values (:id, :pipeline, :author, :status, :story::jsonb, :transition, :entity::jsonb, :payload::jsonb, :comment)
                            on conflict (id) do update set
                            pipeline = excluded.pipeline,
                            author = excluded.author,
                            status = excluded.status,
                            story = excluded.story,
                            transition = excluded.transition,
                            entity = excluded.entity,
                            payload = excluded.payload,
                            comment = excluded.comment
                            """,
                    map
            );

            return get(task.getId()).orElseThrow();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
