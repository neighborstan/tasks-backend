package tech.saas.tasks.core.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.PolymorphMap;
import tech.saas.tasks.core.models.TaskDto;
import tech.saas.tasks.core.models.TaskEntity;
import tech.saas.tasks.core.models.TaskPayload;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TaskRowMapper implements RowMapper<TaskDto<?, ?>> {

    private final ObjectMapper mapper;

    @Override
    public TaskDto<?, ?> mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return new TaskDto<TaskEntity, TaskPayload>(
                    rs.getObject("id", UUID.class),
                    TaskDto.Type.valueOf(rs.getString("type")),
                    TaskDto.Status.valueOf(rs.getString("status")),
                    rs.getString("pipeline"),
                    rs.getString("author"),
                    mapper.readValue(rs.getString("story"), new TypeReference<List<TaskDto.Story>>() {}),
                    rs.getTimestamp("transition").toInstant().atOffset(ZoneOffset.UTC),
                    mapper.readValue(rs.getString("contacts"), new TypeReference<List<TaskDto.Contact>>() {}),
                    new PolymorphMap<>(mapper.readValue(rs.getString("entity"), new TypeReference<Map<String, ?>>() {})),
                    new PolymorphMap<>(mapper.readValue(rs.getString("payload"), new TypeReference<Map<String, ?>>() {})),
                    rs.getString("comment")
            );
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
