package tech.saas.tasks.core.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.RoutePointDto;
import tech.saas.tasks.core.models.TaskDto;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TaskRowMapper implements RowMapper<TaskDto> {

    private final ObjectMapper mapper;

    @Override
    public TaskDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return new TaskDto(
                    rs.getObject("id", UUID.class),
                    TaskDto.Type.valueOf(rs.getString("type")),
                    TaskDto.Status.valueOf(rs.getString("status")),
                    rs.getString("pipeline"),
                    rs.getString("author"),
                    mapper.readValue(rs.getString("story"), new TypeReference<List<TaskDto.Story>>() {}),
                    rs.getTimestamp("transition").toInstant().atOffset(ZoneOffset.UTC),
                    mapper.readValue(rs.getString("shipping"), new TypeReference<Object>() {}),
                    mapper.readValue(rs.getString("payload"), RoutePointDto.class),
                    rs.getString("comment")
            );
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
