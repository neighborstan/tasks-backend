package tech.saas.tasks.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.TaskAssignmentDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
@AllArgsConstructor
public class AssignmentRowMapper implements RowMapper<TaskAssignmentDto> {

    private final ObjectMapper mapper;

    @Override
    public TaskAssignmentDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TaskAssignmentDto(
                rs.getObject("task", UUID.class),
                rs.getString("actor"),
                rs.getTimestamp("instant").toInstant().atOffset(ZoneOffset.UTC)
        );
    }
}
