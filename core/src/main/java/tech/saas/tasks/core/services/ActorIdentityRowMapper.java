package tech.saas.tasks.core.services;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import tech.saas.tasks.core.models.ActorIdentityDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ActorIdentityRowMapper implements RowMapper<ActorIdentityDto> {

    @Override
    public ActorIdentityDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ActorIdentityDto(
                rs.getString("actor"),
                rs.getObject("id", UUID.class)
        );
    }
}
