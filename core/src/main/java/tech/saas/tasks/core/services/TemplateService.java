package tech.saas.tasks.core.services;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.TaskDto;

import java.util.List;

@Component
@AllArgsConstructor
public class TemplateService {

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<TaskDto> rowMapper;


    public List<TaskDto> list(String tenant) {
        throw new UnsupportedOperationException();
    }


}
