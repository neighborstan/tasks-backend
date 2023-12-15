package tech.saas.tasks.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TaskAssignmentDto {

    private final UUID task;
    private final String actor;
    private final String pipeline;
    private final OffsetDateTime instant;
}
