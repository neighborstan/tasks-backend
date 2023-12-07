package tech.saas.tasks.core.services;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.TaskDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@AllArgsConstructor
public class TemplateRowMapper implements RowMapper<TaskDto> {

    @Override
    public TaskDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
